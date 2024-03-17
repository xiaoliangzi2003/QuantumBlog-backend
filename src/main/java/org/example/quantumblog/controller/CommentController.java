package org.example.quantumblog.controller;

import org.example.quantumblog.model.Comment;
import org.example.quantumblog.service.ArticleSerivce;
import org.example.quantumblog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.example.quantumblog.service.CommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author xiaol
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleSerivce articleSerivce;

    @PostMapping("/add-comment")
    public Result publishComment(@RequestBody Map<String, Object> comment){
        try{
            String content = (String) comment.get("content");
            String commentator= (String) comment.get("commentator");
            String author = (String) comment.get("author");
            String parentCommentator = (String) comment.get("parentCommentator");
            String decodeTitle = URLDecoder.decode((String) comment.get("articleTitle"), StandardCharsets.UTF_8);
            Comment result=commentService.addComment(content, decodeTitle, commentator, parentCommentator, author);
            return new Result(Result.OK, "评论成功", result);
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }


    @PostMapping("/get-comment-list")
    public Result getCommentListByPage(@RequestBody Map<String, Object> request){
        try{
            int pageNum = (int) request.get("pageNum");
            int pageSize = (int) request.get("pageSize");
            String decodeTitle = URLDecoder.decode((String) request.get("title"), StandardCharsets.UTF_8);
            String author = (String) request.get("author");
            int articleId = articleSerivce.getArticleByTitleAndAuthor(decodeTitle, author).getId();
            return new Result(Result.OK, "获取评论列表成功",
                    commentService.getCommentListByPage(pageNum, pageSize, articleId));
        }catch (Exception e){
            return new Result(Result.ERROR, e.getMessage(), null);
        }
    }
}
