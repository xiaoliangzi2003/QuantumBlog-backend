package org.example.quantumblog.util;

import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.model.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.example.quantumblog.util.Result.NOT_FOUND;

@Service
public class AuthUtil {
    @Autowired
    private UserMapper userMapper;

    //将AuthRequest对象中的account字段解析为username
    public String authRequestGetUsername(AuthRequest authRequest) {
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

}
