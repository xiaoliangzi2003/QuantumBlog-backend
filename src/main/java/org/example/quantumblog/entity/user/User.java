package org.example.quantumblog.entity.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiaol
 * 账号信息
 */
@Setter
@Getter
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;

    public User() {
    }

    public User(Integer id, String username, String password, String email, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

}
