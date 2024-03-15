package org.example.quantumblog.service.auth;

import jakarta.servlet.http.HttpSession;
import org.example.quantumblog.model.AuthRequest;
import org.example.quantumblog.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户认证服务接口
 * @author xiaol
 */
@Service
public interface AuthService {
    public Map<String,Object> login(AuthRequest authRequest);

    //注册
    public Map<String,Object>  registerByEmail(AuthRequest authRequest);

    //登出
    public void logout();

    //修改密码
    public void changePassword(String username, String oldPassword, String newPassword);

    //修改邮箱
    public void changeEmail(String username, String password, String newEmail);

    //修改手机号
    public void changePhone(String username, String password, String newPhone);

    //删除账号
    public void deleteUser(Integer id, String email, int actiVationCode);

    //找回密码
    public String findPassword(String username);

}