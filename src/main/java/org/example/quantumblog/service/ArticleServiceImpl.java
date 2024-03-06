package org.example.quantumblog.service;

import org.example.quantumblog.entity.blog.Article;
import org.example.quantumblog.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void publishArticle(Article article) {
        //设置文章的发布时间
        Date date = new Date();
        article.setPublishDate(date);

        //设置文章的状态：0表示私有，1表示公开
        article.setStatus(1);

        //设置文章的浏览数、点赞数、评论数、收藏数
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCollectCount(0);

        articleMapper.insertArticle(article);
    }

    @Override
    public void deleteArticle(String articleId) {

    }

    @Override
    public void updateArticle(Article article) {

    }

    @Override
    public void updateLikeCount(String articleId) {

    }

    @Override
    public void updateCommentCount(String articleId) {

    }

    @Override
    public void updateViewCount(String articleId) {

    }

    @Override
    public void updateCollectCount(String articleId) {

    }


}
