package org.example.quantumblog.service.auth;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.mapper.UserProfileMapper;
import org.example.quantumblog.model.AuthRequest;
import org.example.quantumblog.model.User;
import org.example.quantumblog.service.EmailService;
import org.example.quantumblog.service.RulesService;
import org.example.quantumblog.util.AuthUtil;
import org.example.quantumblog.util.JwtUtils;
import org.example.quantumblog.util.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.example.quantumblog.util.Result.*;
/**
 * @author xiaol
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserProfileMapper userProfileMapper;

    @Autowired
    RulesService rulesService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AuthUtil authUtil;


    /*
     * 时间间隔：分钟
     * */
    private static final int INTERVAL = 5;

    /*
     * 登录失败重试次数
     * */
    private static final int MAX_FAILED_ATTEMPTS = 5;

    /*
     * redis记录登录失败次数的key
     * */
    private static final String FAILED_ATTEMPTS = "FAILED_ATTEMPTS";


    /*
     * 从redis中获取登录失败次数
     * */
    private RedisAtomicInteger getRedisCounter(String key) {
        RedisAtomicInteger counter =
                new RedisAtomicInteger(key, Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()));
        if (counter.get() == 0) {
            counter.expireAt(new Date(System.currentTimeMillis() + INTERVAL * 60 * 1000));
        }
        return counter;
    }

    @Override
    public Map<String, Object> loginByAccountAndPassword(AuthRequest authRequest) {
        Map<String,Object> result=new HashMap<>();

        //获取登录的账户
        Map<String,String> accountMap = authRequest.getAccount();
        String account=accountMap.get("account");
        String username=authUtil.authRequestGetUsername(authRequest);
        String password=accountMap.get("password");
        //获取redis中的登录失败次数
        String key = FAILED_ATTEMPTS + username;
        RedisAtomicInteger failedAttempts = getRedisCounter(key);

        //如果失败次数超过5次，锁定5min
        if (failedAttempts.get() >= MAX_FAILED_ATTEMPTS) {
            throw new GlobalException("登录失败次数超过5次，锁定5min", TOO_MANY_REQUEST);
        }

        //获取redis中的验证码
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String captcha = operations.get("captcha");
        String code=authRequest.getAccount().get("captcha");
        if (!code.equals(captcha)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }

        //用户被封禁
        if ("ban".equals(userMapper.getStatus(username))) {
            failedAttempts.incrementAndGet();
            throw new GlobalException("用户已被封禁", FORBIDDEN);
        }

        //获取数据库中的密码
        User user = new User();
        rulesService.checkIsRightPassword(username, password);

        //登录成功,生成token
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("token", JwtUtils.generateToken(user.getUsername(), password));
        log.info(response.get("username") + "登录成功");

        //设置用户当前的状态为在线
        userMapper.updateStatus(user.getUsername(), "online");

        //更新用户的登录时间
        long loginTimeMillis = System.currentTimeMillis();
        Timestamp loginTimeStamp = new Timestamp(loginTimeMillis);
        userMapper.updateLoginTime(user.getUsername(), loginTimeStamp);
        return response;
    }


    @Override
    public Map<String, Object> registerByEmail(AuthRequest authRequest) {
        try {

            String username = authRequest.getAccount().get("username");
            String password = authRequest.getAccount().get("password");
            String email = authRequest.getAccount().get("email");
            String code = authRequest.getAccount().get("code");
            log.info("当前验证码："+code);

            //从redis中获取验证码
            String correctCode=stringRedisTemplate.opsForValue().get(email);
            log.info("正确验证码："+correctCode);
            if(!code.equals(correctCode)){
                throw new GlobalException("验证码错误", UNAUTHORIZED);
            }

            String registerType="email";
            rulesService.usernameRule(username);
            rulesService.passwordRule(password);
            rulesService.emailRule(email);
            rulesService.checkExistRule(username, email,null,registerType);

            //注册部分的逻辑
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            //数据库中的密码应该加密存储
            String encryptedPassword = PasswordEncryptor.encrypt(password);
            user.setPassword(encryptedPassword);


            userMapper.insertUser(user);
            //注册后直接生成token
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("token", JwtUtils.generateToken(user.getUsername(), user.getPassword()));

            userProfileMapper.insertUsername(username);

            log.info(response.get("username") + "注册成功");
            return response;

        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }

    }

    @Override
    public void logout(String username) {
        //设置用户当前的状态为离线
        userMapper.updateStatus(username, "offline");
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        try{
            //检查旧密码是否正确
            rulesService.checkIsRightPassword(username, oldPassword);
            rulesService.passwordRule(newPassword);
            //如果新密码和旧密码相同
            if (oldPassword.equals(newPassword)) {
                throw new GlobalException("新密码不能和旧密码相同", BAD_REQUEST);
            }
            //更新密码
            userMapper.updatePassword(username, oldPassword, newPassword);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }



    @Override
    public void deleteUser(String username,String password) {
        try{
            //检查密码是否正确
            rulesService.checkIsRightPassword(username, password);
            //获取用户ID
            int id = userMapper.getUserByUsername(username).getId();
            //删除用户
            userMapper.deleteUser(id);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public Map<String, Object> loginByEmailCode(AuthRequest authRequest) {
        //获取邮箱和验证码
        Map<String, String> accountMap = authRequest.getAccount();
        String email = accountMap.get("account");
        String code = accountMap.get("code");

        //如果邮箱不存在
        if (userMapper.getUserByEmail(email) == null) {
            throw new GlobalException("邮箱不存在", NOT_FOUND);
        }

        //从redis中获取验证码并进行比对
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }

        //登录成功，生成token
        String username = authUtil.authRequestGetUsername(authRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("token", JwtUtils.generateToken(username, code));

        //设置用户当前的状态为在线
        userMapper.updateStatus(username, "online");

        //更新用户的登录时间
        long loginTimeMillis = System.currentTimeMillis();
        Timestamp loginTimeStamp = new Timestamp(loginTimeMillis);
        userMapper.updateLoginTime(username, loginTimeStamp);

        return response;
    }

    @Override
    public Map<String, Object> loginByPhoneCode(AuthRequest authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> loginByWechatQRCode(AuthRequest authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> loginByGithub(AuthRequest authRequest) {
        return null;
    }

    @Override
    public Map<String, Object> registerByPhone(AuthRequest authRequest) {
        return null;
    }

    @Override
    public void resetPasswordByEmail(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void resetPasswordByPhone(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void resetPasswordByWechat(String username,String password) {
        try{
            rulesService.passwordRule(password);
            //更新密码
            userMapper.updatePassword(username,password,password);
        }catch (GlobalException e){
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void bindEmail(String username, String password, String email,String code) {
        //如果绑定的邮箱已经被其他用户绑定
        if(!Objects.equals(userMapper.getUserByEmail(email).getUsername(), username)){
            throw new GlobalException("邮箱已被其他用户绑定", BAD_REQUEST);
        }
        if(email.equals(userMapper.getUserByUsername(username).getEmail())){
            throw new GlobalException("邮箱已被绑定", BAD_REQUEST);
        }
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }
        //更新邮箱
        userMapper.updateEmail(username, password, email);
    }

    @Override
    public void bindPhone(String username, String password, String phone,String code) {
        //如果绑定的手机号已经被其他用户绑定
        if(!Objects.equals(userMapper.getUserByPhone(phone).getUsername(), username)){
            throw new GlobalException("手机号已被其他用户绑定", BAD_REQUEST);
        }
        if(phone.equals(userMapper.getUserByUsername(username).getPhone())){
            throw new GlobalException("手机号已被绑定", BAD_REQUEST);
        }
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(phone);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }
        //更新手机号
        userMapper.updatePhone(username, password, phone);
    }

    @Override
    public void bindWechat(String username, String password, String wechatCode) {

    }

    @Override
    public void bindGithub(String username, String password, String githubCode) {

    }

    @Override
    public void unbindEmail(String username, String password, String email,String code) {
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(email);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }
        //如果只剩下一个绑定，不允许解绑
        if (rulesService.checkAtLeastOneBinding(username)) {
            userMapper.updateEmail(username, password, null);
        }else {
            throw new GlobalException("至少绑定了手机或邮箱其中一个", BAD_REQUEST);
        }
    }

    @Override
    public void unbindPhone(String username, String password, String phone,String code) {
        rulesService.checkIsRightPassword(username, password);
        //校验验证码
        String correctCode = stringRedisTemplate.opsForValue().get(phone);
        if (!code.equals(correctCode)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }
        //如果只剩下一个绑定，不允许解绑
        if (rulesService.checkAtLeastOneBinding(username)) {
            userMapper.updatePhone(username, password, null);
        }else {
            userMapper.updatePhone(username, password, null);
        }
    }

    @Override
    public void unbindWechat(String username, String password, String wechatCode) {

    }

    @Override
    public void unbindGithub(String username, String password, String githubCode) {

    }
}

