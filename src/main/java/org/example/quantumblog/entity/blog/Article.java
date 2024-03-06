package org.example.quantumblog.entity.blog;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

//文章
@Setter
@Getter
public class Article implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private String authorId;
    private Date publishDate;
    private Date editDate;
    //点赞数
    private Integer likeCount;
    //评论数
    private Integer commentCount;
    //浏览数
    private Integer viewCount;
    //收藏数
    private Integer collectCount;
    //文章状态
    private Integer status;
    //文章分类
    private Integer categoryId;

    public Article() {
    }

    public Article(Integer id,
                   String title,
                   String content,
                   String authorId,
                   Date publishDate,
                   Date editDate,
                   Integer likeCount,
                   Integer commentCount,
                   Integer viewCount,
                   Integer collectCount,
                   Integer status,
                   Integer categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.publishDate = publishDate;
        this.editDate = editDate;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.collectCount = collectCount;
        this.status = status;
        this.categoryId = categoryId;
    }

}
