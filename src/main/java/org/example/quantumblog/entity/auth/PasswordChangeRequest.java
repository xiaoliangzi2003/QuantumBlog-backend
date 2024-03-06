package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.quantumblog.entity.user.User;

@Getter
@Setter
public class PasswordChangeRequest {
    private User user;
    private String newPassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(User user, String newPassword) {
        this.user = user;
        this.newPassword = newPassword;
    }
}
