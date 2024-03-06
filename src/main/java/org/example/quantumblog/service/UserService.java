package org.example.quantumblog.service;

import org.example.quantumblog.entity.user.User;
import org.springframework.stereotype.Service;

/**
 * @author xiaol
 */
@Service
public interface UserService {
    //用户名登录
    public User login(String username, String password);

    //邮箱登录
    public User loginByEmail(String email, String password);



    public User register(String username,
                  String password,
                  String email,
                  String phone
                  );

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

    public User loginByPhone(String phone, String password);

    //找回密码
    public String findPassword(String username);
}
