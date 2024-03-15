package org.example.quantumblog.service.auth;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.model.AuthRequest;
import org.example.quantumblog.model.User;
import org.example.quantumblog.service.EmailService;
import org.example.quantumblog.service.RulesService;
import org.example.quantumblog.util.JwtUtils;
import org.example.quantumblog.util.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

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
    RulesService rulesService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


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

    private String authRequestGetUsername(AuthRequest authRequest) {
        String username = null;
        //如果包含@，说明是邮箱
        if (authRequest.getAccount().get("account").contains("@")) {
            username = userMapper.getUsernameByEmail(authRequest.getAccount().get("account"));
        }else if (authRequest.getAccount().get("account").matches(".*[a-zA-Z].*")) {
            username = authRequest.getAccount().get("account");
        }else if (authRequest.getAccount().get("account").matches("^[0-9]*$") && authRequest.getAccount().get("account").length() == 11) {
            username = userMapper.getUsernameByPhone(authRequest.getAccount().get("account"));
        }else {
            throw new GlobalException("非法请求", NOT_FOUND);
        }
        return username;
    }

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
    public Map<String, Object> login(AuthRequest authRequest) {
        Map<String,Object> result=new HashMap<>();
        //先通过邮箱名或手机号查找到用户的用户名
        String username = authRequestGetUsername(authRequest);
        if(username==null){
            throw new GlobalException("用户不存在", NOT_FOUND);
        }

        String key = FAILED_ATTEMPTS + username;
        RedisAtomicInteger failedAttempts = getRedisCounter(key);

        //如果失败次数超过5次，锁定5min
        if (failedAttempts.get() >= MAX_FAILED_ATTEMPTS) {
            throw new GlobalException("登录失败次数超过5次，锁定5min", TOO_MANY_REQUEST);
        }
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String captcha = operations.get("captcha");
        String code=authRequest.getAccount().get("code");
        if (!code.equals(captcha)) {
            throw new GlobalException("验证码错误", UNAUTHORIZED);
        }

        if ("ban".equals(userMapper.getStatus(username))) {
            failedAttempts.incrementAndGet();
            throw new GlobalException("用户已被封禁", FORBIDDEN);
        }
        if ("online".equals(userMapper.getStatus(username))) {
            failedAttempts.incrementAndGet();
            throw new GlobalException("用户已在线，不要重复登录", FORBIDDEN);
        }

        //获取数据库中的密码
        String storedPassword = userMapper.getPasswordByUsername(username);
        //获取请求的密码
        String password = authRequest.getAccount().get("password");
        User user = new User();
        //查询密码是否匹配
        if (PasswordEncryptor.verify(password, storedPassword)) {
            user = userMapper.getUserByUsernameAndPassword(username, storedPassword);
        } else {
            failedAttempts.incrementAndGet();
            throw new GlobalException("用户名和密码不匹配", UNAUTHORIZED);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("token", JwtUtils.generateToken(user.getUsername(), authRequest.getAccount().get("password")));
        log.info(response.get("username") + "登录成功");
        //设置用户当前的状态为在线
        user.setStatus("online");
        userMapper.updateStatus(user.getUsername(), user.getStatus());
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
            log.info(response.get("username") + "注册成功");
            return response;

        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }

    }

    @Override
    public void logout() {

    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        try {
            rulesService.checkIsRightPassword(username, oldPassword);
            rulesService.passwordRule(newPassword);
            userMapper.updatePassword(username, oldPassword, newPassword);
        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void changeEmail(String username, String password, String newEmail) {
        try {
            rulesService.checkIsRightPassword(username, password);
            rulesService.emailRule(newEmail);
            if (userMapper.getUserByEmail(newEmail) != null) {
                throw new GlobalException("邮箱已被注册", BAD_REQUEST);
            }
            userMapper.updateEmail(username, password, newEmail);
        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void changePhone(String username, String password, String newPhone) {
        try {
            String type="phone";
            rulesService.checkIsRightPassword(username, password);
            rulesService.checkExistRule(null, null, newPhone,type);
            rulesService.phoneRule(newPhone);
            userMapper.updatePhone(username, password, newPhone);
        } catch (GlobalException e) {
            throw new GlobalException(e.getMessage(), e.getStatusCode());
        }
    }

    @Override
    public void deleteUser(Integer id, String email, int actiVationCode) {

    }

    @Override
    public String findPassword(String username) {
        return null;
    }
}

