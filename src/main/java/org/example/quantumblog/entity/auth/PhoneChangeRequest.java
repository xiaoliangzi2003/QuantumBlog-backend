package org.example.quantumblog.entity.auth;

import lombok.Getter;
import lombok.Setter;
import org.example.quantumblog.entity.user.User;

@Getter
@Setter
public class PhoneChangeRequest {
    private User user;
    private String newPhone;

    public PhoneChangeRequest() {
    }

    public PhoneChangeRequest(User user, String newPhone) {
        this.user = user;
        this.newPhone = newPhone;
    }
}
