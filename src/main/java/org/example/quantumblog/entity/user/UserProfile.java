package org.example.quantumblog.entity.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author xiaol
 */
//个人信息
@Setter
@Getter
public class UserProfile {
    //昵称
    private String nickname;
    //头像
    private String avatar;
    //性别
    private int gender;
    //生日
    private Date birthday;
    //个性签名
    private String signature;

    public UserProfile() {
    }

    public UserProfile(String nickname,
                       String avatar,
                       int gender,
                       Date birthday,
                       String signature) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.birthday = birthday;
        this.signature = signature;
    }
}
