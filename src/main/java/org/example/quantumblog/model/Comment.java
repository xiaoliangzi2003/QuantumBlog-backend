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
    //评论的文章Id
    private Integer articleId;
    //评论的作者Id
    private Integer authorId;
    //评论的父评论Id(parentId=0表示是一级评论，否则是回复评论)
    private Integer parentId;
    //评论的点赞量
    private Integer likes;
    //评论的创建时间
    private Integer createTime;
    //评论的修改时间
    private Integer updateTime;
    //评论的作者
    private String author;
    //评论的状态
    private String status;
}
