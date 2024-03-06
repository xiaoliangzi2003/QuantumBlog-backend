package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;

//验证码
@Setter
@Getter
public class VerificationCode {
    //验证码
    private String code;
    //过期时间
    private long expireTime;
    //接收者
    private String receiver;

    public VerificationCode() {
    }

    public VerificationCode(String code, long expireTime, String receiver) {
        this.code = code;
        this.expireTime = expireTime;
        this.receiver = receiver;
    }

}
