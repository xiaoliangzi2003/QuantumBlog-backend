package org.example.quantumblog.service;

import org.example.quantumblog.model.AuthRequest;
import org.springframework.stereotype.Service;

@Service
public interface RulesService {
    public  void passwordRule(String password);
    public void usernameRule(String username);
    public void phoneRule(String phone);
    public void emailRule(String email);
    public void checkExistRule(String username, String email, String phone,String type);
    public void checkIsRightPassword(String username, String password);

    //检查是否至少绑定了手机或邮箱其中一个
    boolean checkAtLeastOneBinding(String username);
}