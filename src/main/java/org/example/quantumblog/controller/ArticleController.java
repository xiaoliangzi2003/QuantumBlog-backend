package org.example.quantumblog.controller;

import org.example.quantumblog.entity.blog.Article;
import org.example.quantumblog.util.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.example.quantumblog.service.ArticleService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    //发布文章
    @PostMapping("/publish-article")
    public Result userPublishArticle(@RequestBody Article article) {
        Map<String,Object> articleData=new HashMap<>();
        articleData.put("articleId",article.getId());
        articleData.put("content",article.getContent());
        articleData.put("title",article.getTitle());
        articleData.put("userId",article.getAuthorId());
        if(article.getContent()==null){
            return new Result(400,"文章内容不能为空",null);
        }else if(article.getTitle()==null){
            return new Result(400,"文章标题不能为空",null);
        }else{
            articleData.put("publishDate",article.getPublishDate());
            return new Result(200, "添加文章成功", articleData);
        }
    }

    //删除文章
    @PostMapping("/delete-article")
    public Result userDeleteArticle(@RequestBody Integer articleId) {
        if(articleId==null){
            return new Result(400,"文章id不能为空",null);
        }else{
            return new Result(200, "删除文章成功", null);
        }
    }

    //更新文章
    @PutMapping("/update-article")
    public Result userUpdateArticle(@RequestBody Article article) {
        return new Result(200, "更新文章成功", null);
    }

    //更新文章的点赞数
    @PutMapping("/update-like-count")
    public Result userUpdateLikeCount(@RequestBody Integer articleId) {
        if(articleId==null){
            return new Result(400,"文章id不能为空",null);
        }else{
            return new Result(200, "更新点赞数成功", null);
        }
    }

    //更新文章的评论数
    @PutMapping("/update-comment-count")
    public Result userUpdateCommentCount(@RequestBody Integer articleId) {
        if(articleId==null){
            return new Result(400,"文章id不能为空",null);
        }else{
            return new Result(200, "更新评论数成功", null);
        }
    }

    //更新文章的浏览数
    @PutMapping("/update-view-count")
    public Result userUpdateViewCount(@RequestBody Integer articleId) {
        if(articleId==null){
            return new Result(400,"文章id不能为空",null);
        }else{
            return new Result(200, "更新浏览数成功", null);
        }
    }

    //更新文章的收藏数
    @PutMapping("/update-collect-count")
    public Result userUpdateCollectCount(@RequestBody Integer articleId) {
        if(articleId==null){
            return new Result(400,"文章id不能为空",null);
        }else{
            return new Result(200, "更新收藏数成功", null);
        }
    }

    //查看文章详情

    //获取文章列表
}
