package org.example.quantumblog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author xiaol
 * 账号信息
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    //账号状态(在线、离线、封禁)
    private String status;
    //账号的创建时间
    private Date createTime;
    //上一次登录时间
    private Date lastLoginTime;
    //账号类型(普通用户、管理员)
    private String type;
    private String wechatCode;
    private String githubCode;
}