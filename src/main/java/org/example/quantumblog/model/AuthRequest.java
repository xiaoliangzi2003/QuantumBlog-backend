package org.example.quantumblog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest{
    private Map<String,String> account;
    //验证码
    private String code;
    //失败次数，超过五次后将会被锁定5min
    private int failedAttempts=0;

    public void updateFailedAttempts(){
        this.failedAttempts++;
    }
}
