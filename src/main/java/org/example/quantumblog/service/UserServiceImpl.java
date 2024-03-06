package org.example.quantumblog.service;

import org.example.quantumblog.entity.user.User;
import org.example.quantumblog.exception.UserLoginException;
import org.example.quantumblog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaol
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public User login(String username, String password) {
        User user=userMapper.getUserByUsernameAndPassword(username,password);
        if(user!=null){
            return user;
        }else{
            throw new UserLoginException("用户名或密码错误",401);
        }
    }

    @Override
    public User loginByEmail(String email, String password) {
        return null;
    }

    @Override
    public User register(String username,
                         String password,
                         String email,
                         String phone) {

        //创建用户信息
        User user=new User();

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);

        //如果用户名、邮箱、手机号已存在，则注册失败
        if(userMapper.getUserByUsername(username)!=null){
            throw new UserLoginException("用户名已存在",409);
        }else if(userMapper.getUserByEmail(email)!=null) {
            throw new UserLoginException("邮箱已存在", 409);
        }else if(userMapper.getUserByPhone(phone)!=null){
            throw new UserLoginException("手机号已存在", 409);
        }else{
            userMapper.insertUser(user);
            user.setId(userMapper.getUserByUsername(username).getId());
            return user;
        }

    }

    @Override
    public void logout() {
        //删除token


    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if(userMapper.getUserByUsername(username)==null){
            throw new UserLoginException("用户名不存在",401);
        }else if(userMapper.getUserByUsernameAndPassword(username,oldPassword)==null) {
            throw new UserLoginException("密码错误", 401);
        }else if(oldPassword.equals(newPassword)){
            throw new UserLoginException("新密码不能与旧密码相同", 401);
        }else{
            User user=userMapper.getUserByUsername(username);
            user.setPassword(newPassword);
            userMapper.updateUser(user);
        }
    }

    @Override
    public void changeEmail(String username, String password, String newEmail) {
        if(userMapper.getUserByUsername(username)==null) {
            throw new UserLoginException("用户名不存在", 401);
        }else if(userMapper.getUserByUsernameAndPassword(username,password)==null) {
            throw new UserLoginException("密码错误", 401);
        }else if(userMapper.getUserByEmail(newEmail)!=null){
            throw new UserLoginException("邮箱已存在", 409);
        }else{
            User user=userMapper.getUserByUsername(username);
            user.setEmail(newEmail);
            userMapper.updateUser(user);
        }

        //如果邮箱不存在的逻辑要补充

    }

    @Override
    public void changePhone(String username, String password, String newPhone) {
        if(userMapper.getUserByUsername(username)==null) {
            throw new UserLoginException("用户名不存在", 401);
        }else if(userMapper.getUserByUsernameAndPassword(username,password)==null) {
            throw new UserLoginException("密码错误", 401);
        }else if(userMapper.getUserByPhone(newPhone)!=null){
            throw new UserLoginException("手机号已存在", 409);
        }else{
            User user=userMapper.getUserByUsername(username);
            user.setPhone(newPhone);
            userMapper.updateUser(user);
        }

        //如果手机号不存在的逻辑要补充,添加验证手机号的逻辑

    }

    @Override
    public void deleteUser(Integer id, String email, int actiVationCode) {
        //生成并发送验证码
        SendCodeService sendCodeService=new SendCodeService();
        int code=sendCodeService.generateCode();
        sendCodeService.sendCode(code,email);


        //从前端接收验证码，验证前端发送的验证码是否正确
        if(!sendCodeService.verifyCode(actiVationCode,email)){
            throw new UserLoginException("验证码错误",401);
        }


        if(userMapper.getUserById(String.valueOf(id))==null){
            throw new UserLoginException("用户不存在", 401);
        }

        userMapper.deleteUser(String.valueOf(id));

    }

    @Override
    public User loginByPhone(String phone, String password) {
        User user=userMapper.getUserByPhone(phone);
        if(user!=null){
            return user;
        }else{
            throw new UserLoginException("手机号或密码错误",401);
        }
    }

    @Override
    public String findPassword(String username) {
        if(userMapper.getUserByUsername(username)==null){
            throw new UserLoginException("用户名不存在",401);
        }else{
            return userMapper.getUserByUsername(username).getPassword();
        }
    }
}
