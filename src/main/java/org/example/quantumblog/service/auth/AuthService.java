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
    public Map<String,Object> loginByAccountAndPassword(AuthRequest authRequest);

    //注册
    public Map<String,Object>  registerByEmail(AuthRequest authRequest);

    //登出
    public void logout(String username);

    //修改密码
    public void changePassword(String username, String oldPassword, String newPassword);

    //删除账号
    public void deleteUser(String username,String password);

    Map<String, Object> loginByEmailCode(AuthRequest authRequest);

    Map<String, Object> loginByPhoneCode(AuthRequest authRequest);

    Map<String, Object> loginByWechatQRCode(AuthRequest authRequest);

    Map<String, Object> loginByGithub(AuthRequest authRequest);

    Map<String, Object> registerByPhone(AuthRequest authRequest);

    void resetPasswordByEmail(String username,String password);

    void resetPasswordByPhone(String username,String password);

    void resetPasswordByWechat(String username,String password);

    void bindEmail(String username, String password, String email,String code);

    void bindPhone(String username, String password, String phone,String code);

    void bindWechat(String username, String password, String wechatCode);

    void bindGithub(String username, String password, String githubCode);

    void unbindEmail(String username, String password, String email,String code);

    void unbindPhone(String username, String password, String phone,String code);

    void unbindWechat(String username, String password, String wechatCode);

    void unbindGithub(String username, String password, String githubCode);
}