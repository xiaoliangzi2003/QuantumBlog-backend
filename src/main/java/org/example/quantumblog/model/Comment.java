package org.example.quantumblog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xiaol
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    //评论Id
    private Integer id;

    //评论内容
    private String content;

    //评论的文章标题
    private String articleTitle;

    //评论者的用户名
    private String commentator;

    //评论者的父评论的用户名(如果是一级评论则为null)
    private String parentCommentator;

    //评论的点赞量
    private Integer likes;

    //评论的创建时间
    private Integer createTime;

    //评论的修改时间
    private Integer updateTime;

    //评论的状态
    private String status;

    //评论的文章Id
    private Integer articleId;

    //评论的文章的作者的Id
    private Integer authorId;
}
