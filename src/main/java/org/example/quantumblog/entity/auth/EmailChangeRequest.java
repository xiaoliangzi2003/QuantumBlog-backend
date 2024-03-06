package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.quantumblog.entity.user.User;

@Getter
@Setter
public class EmailChangeRequest {
    private User user;
    private String newEmail;

    public EmailChangeRequest() {
    }

    public EmailChangeRequest(User user, String newEmail) {
        this.user = user;
        this.newEmail = newEmail;
    }
}
