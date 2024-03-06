package org.example.quantumblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.quantumblog.entity.auth.EmailChangeRequest;
import org.example.quantumblog.entity.auth.LoginRequest;
import org.example.quantumblog.entity.auth.PasswordChangeRequest;
import org.example.quantumblog.entity.auth.PhoneChangeRequest;
import org.example.quantumblog.entity.user.User;
import org.example.quantumblog.exception.UserLoginException;
import org.example.quantumblog.service.SendCodeService;
import org.example.quantumblog.util.result.Result;
import org.example.quantumblog.util.token.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.quantumblog.service.UserService;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaol
 */
@Slf4j
@RestController
public class AuthController {
    @Autowired
    UserService userService;


    @Autowired
    SendCodeService sendCodeService;

    @GetMapping("/login")
    public ModelAndView login(){
        log.info("登录页面已经打开");
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest){
        String loginType=loginRequest.getLoginType();
        User user=loginRequest.getUser();
        try{
            User loginUser;
            switch (loginType) {
                case "username":
                    loginUser = userService.login(user.getUsername(), user.getPassword());
                    break;
                case "email":
                    loginUser = userService.loginByEmail(user.getEmail(), user.getPassword());
                    break;
                case "phone":
                    loginUser = userService.loginByPhone(user.getPhone(), user.getPassword());
                    break;
                default:
                    throw new UserLoginException("登录类型错误", 400);
            }
            Map<String,Object> loginData=new HashMap<>();
            loginData.put("username",loginUser.getUsername());
            loginData.put("token", JwtToken.generateToken(loginUser.getUsername(),loginUser.getPassword()));
            log.info("登录成功");
            return new Result(200,"登录成功",loginData);
        }catch (Exception e){
            log.info("登录失败");
            return new Result(401,e.getMessage(),null);
        }
    }

    @PutMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
        User user=passwordChangeRequest.getUser();
        try{
            userService.changePassword(
                    user.getUsername(),
                    user.getPassword(),
                    passwordChangeRequest.getNewPassword());
            log.info("修改密码成功");
            return new Result(200,"修改密码成功","修改密码成功");
        }catch (Exception e){
            log.info("修改密码失败");
            return new Result(401,e.getMessage(),"修改密码失败");
        }
    }

    @PutMapping("/modifyEmail")
    public Result modifyEmail(@RequestBody EmailChangeRequest emailChangeRequest){
        User user=emailChangeRequest.getUser();
        String newEmail=emailChangeRequest.getNewEmail();
        try{
            userService.changeEmail(user.getUsername(),user.getPassword(),newEmail);
            log.info("修改邮箱成功");
            return new Result(200,"修改邮箱成功","修改邮箱成功");
        }catch (Exception e){
            log.info("修改邮箱失败");
            return new Result(401,e.getMessage(),"修改邮箱失败");
        }
    }

    @PutMapping("/modifyPhone")
    public Result modifyPhone(@RequestBody PhoneChangeRequest phoneChangeRequest){
        User user=phoneChangeRequest.getUser();
        String newPhone=phoneChangeRequest.getNewPhone();
        try{
            userService.changePhone(user.getUsername(),user.getPassword(),newPhone);
            log.info("修改手机号成功");
            return new Result(200,"修改手机号成功",null);
        }catch (Exception e){
            log.info("修改手机号失败");
            return new Result(401,e.getMessage(),"修改手机号失败");
        }
    }

    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody User user){
        //@Autowired注入后就不要再创建一个新的对象了
        int code=sendCodeService.generateCode();
        sendCodeService.sendCode(code,user.getEmail());
        log.info("验证码发送成功");
        return new Result(200,"验证码发送成功",code);
    }

    //找回密码
    @PostMapping("/findPassword")
    public Result findPassword(@RequestBody User user){
        try{
            String password=userService.findPassword(user.getUsername());
            log.info("找回密码成功");
            return new Result(200,"找回密码成功","你的密码是"+password);
        }catch (Exception e){
            log.info("找回密码失败");
            return new Result(401,e.getMessage(),null);
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user){
        try{
            User registerUser=userService.register(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getPhone());
            log.info("注册成功");
            return new Result(200,"注册成功",registerUser);
        }catch (Exception e){
            log.info("注册失败");
            return new Result(409,e.getMessage(),null);
        }
    }

    @GetMapping("/register")
    public ModelAndView register(){
        log.info("注册页面已经打开");
        return new ModelAndView("register");
    }
}
