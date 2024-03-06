package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.quantumblog.entity.user.User;

@Getter
@Setter
public class LoginRequest {
    private User user;
    private String loginType;

    public LoginRequest() {
    }

    public LoginRequest(User user, String loginType) {
        this.user = user;
        this.loginType = loginType;
    }
}
