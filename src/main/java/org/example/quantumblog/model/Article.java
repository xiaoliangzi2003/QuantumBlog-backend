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
public class Article {
    //文章Id 从10000开始自增
    private Integer id;

    //文章标题
    private String title;

    //文章简介 默认为文章的前50个字符
    private String summary;

    //文章是否置顶 false不置顶 true置顶
    private Boolean top;

    //文章的作者Id
    private Integer authorId;

    //文章访问量 默认为0
    private Integer views;

    //文章点赞量 默认为0
    private Integer likes;

    //文章评论量 默认为0
    private Integer comments;

    //文章收藏量 默认为0
    private Integer collects;

    //文章转发量 默认为0
    private Integer forwards;

    //文章创建时间戳
    private long createTimeStamp;

    //文章修改时间戳
    private long updateTimeStamp;

    //文章作者
    private String author;

    //文章分类
    private String category;

    //文章标签 默认值为"未分类"
    private String tags;


    //文章状态 默认状态为"publish"
    private String status;

    //文章本地url
    private String url;

    //文章内容
    private String content;

    //文章可见性
    private boolean visibility;

}
