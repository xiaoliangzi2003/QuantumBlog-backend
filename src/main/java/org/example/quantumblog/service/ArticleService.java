package org.example.quantumblog.service;

import org.apache.ibatis.annotations.Select;
import org.example.quantumblog.entity.blog.Article;
import org.example.quantumblog.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public interface ArticleService {

    //添加文章
    public void publishArticle(Article article);

    //删除文章
    public void deleteArticle(String articleId);

    //修改文章
    public void updateArticle(Article article);

    //更新文章的点赞数
    public void updateLikeCount(String articleId);

    //更新文章的评论数
    public void updateCommentCount(String articleId);

    //更新文章的浏览数
    public void updateViewCount(String articleId);

    //更新文章的收藏数
    public void updateCollectCount(String articleId);

}
