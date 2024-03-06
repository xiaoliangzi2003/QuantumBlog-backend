package org.example.quantumblog.entity.blog;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

//评论
@Setter
@Getter
public class Comment {
    //评论id
    private Integer id;
    //评论者id
    private Integer userId;
    //评论内容
    private String content;
    //评论时间
    private Date commentDate;
    //评论状态
    private Integer status;
    //评论所属文章id
    private Integer articleId;
    //评论所属评论id，如果是文章评论则为0，如果是评论的评论则为评论id
    private Integer parentId;

    public Comment() {
    }

    public Comment(Integer id,
                   Integer userId,
                   String content,
                   Date commentDate,
                   Integer status,
                   Integer articleId,
                   Integer parentId) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.commentDate = commentDate;
        this.status = status;
        this.articleId = articleId;
        this.parentId = parentId;
    }
}
