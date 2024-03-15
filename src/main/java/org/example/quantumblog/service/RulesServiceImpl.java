package org.example.quantumblog.service;

import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.model.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.quantumblog.util.Result.*;
import static org.example.quantumblog.util.Result.PASSWORD_EMPTY;

/**
 * @author xiaol
 */
@Service
public class RulesServiceImpl implements RulesService{

    @Autowired
    UserMapper userMapper;


    @Override
    public void passwordRule(String password){
        Pattern pattern= Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");
        Matcher matcher=pattern.matcher(password);
        if(!matcher.matches()){
            throw new GlobalException("密码必须包含字母、数字和符号",PASSWORD_INVALID);
        }
        if(password.length()<6 || password.length()>15){
            throw new GlobalException("密码的长度必须在6到15位之间",PASSWORD_INVALID);
        }
    }

    @Override
    public void usernameRule(String username){
        Pattern pattern=Pattern.compile("[a-zA-Z]");
        Matcher matcher=pattern.matcher(username);
        if(!matcher.find()){
            throw new GlobalException("用户名必须至少包含一个字母",USERNAME_INVALID);
        }
        pattern=Pattern.compile("[\u4e00-\u9fa5]");
        matcher=pattern.matcher(username);
        if(matcher.find()){
            throw new GlobalException("用户名不能包含中文字符",USERNAME_INVALID);
        }
        if(username.length()<6){
            throw new GlobalException("用户名必须大于等于6位",USERNAME_INVALID);
        }
        if(username.length()>15){
            throw new GlobalException("用户名必须小于等于15位",USERNAME_INVALID);
        }
    }

    @Override
    public void phoneRule(String phone){
        Pattern pattern=Pattern.compile("^\\d{11}$");
        Matcher matcher=pattern.matcher(phone);
        if(!matcher.matches()){
            throw new GlobalException("手机号必须是11位数字",PHONE_INVALID);
        }
    }

    @Override
    public void emailRule(String email){
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        Matcher matcher=pattern.matcher(email);
        if(!matcher.matches()){
            throw new GlobalException("邮箱格式不正确",EMAIL_INVALID);
        }
    }

    @Override
    public void checkExistRule(String username, String email, String phone,String type){
        if(userMapper.getUserByUsername(username)!=null) {
            throw new GlobalException("用户名已存在",USERNAME_EXISTS);
        }else if(type=="email"){
            if(userMapper.getUserByEmail(email)!=null) {
                throw new GlobalException("邮箱已存在",EMAIL_EXISTS);
            }
        }else if(type=="phone"){
            if(userMapper.getUserByPhone(phone)!=null) {
                throw new GlobalException("手机号已存在",PHONE_EXISTS);
            }
        }
    }

    @Override
    public void checkIsRightPassword(String username, String password){
        if(userMapper.getUserByUsernameAndPassword(username,password)==null) throw new GlobalException("用户名和密码不匹配",UNAUTHORIZED);
    }
}
