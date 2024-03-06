package org.example.quantumblog.entity.auth;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ToEmail implements Serializable {
    //邮件接收方
    private String[] recipients;
    //邮件主题
    private String subject;
    //邮件内容
    private String content;
}
