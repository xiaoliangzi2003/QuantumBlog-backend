package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

//登录失败
@Setter
@Getter
public class LoginFailure {
    //尝试次数
    private int tryCount;
    //最后一次尝试时间
    private Date lastTryTime;
    //尝试的用户id
    private int tryUserId;

    public LoginFailure() {
    }

    public LoginFailure(int tryCount, Date lastTryTime, int tryUserId) {
        this.tryCount = tryCount;
        this.lastTryTime = lastTryTime;
        this.tryUserId = tryUserId;
    }

}
