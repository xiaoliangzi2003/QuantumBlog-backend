package org.example.quantumblog.service;

import com.github.pagehelper.PageHelper;
import org.example.quantumblog.exception.GlobalException;
import org.example.quantumblog.mapper.CommentMapper;
import org.example.quantumblog.mapper.UserMapper;
import org.example.quantumblog.model.Comment;
import org.example.quantumblog.util.Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.quantumblog.util.Result;
import java.util.Date;
import java.util.List;

/**
 * @author xiaol
 */
@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Generator generator;

    @Autowired
    private ArticleSerivce articleSerivce;

    @Override
    public Comment addComment(String content,
                           String articleTitle,
                           String commentator,
                           String parentCommentator,
                              String author) {
        try{
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setArticleTitle(articleTitle);
            comment.setCommentator(commentator);
            comment.setParentCommentator(parentCommentator);
            comment.setLikes(0);
            Date now= new Date();
            long nowTime = now.getTime();
            comment.setCreateTime((int) nowTime);
            comment.setUpdateTime((int) nowTime);
            Integer articleId=articleSerivce.getArticleByTitleAndAuthor(articleTitle,author).getId();
            comment.setArticleId(articleId);
            Integer authorId=userMapper.getUserByUsername(author).getId();
            comment.setAuthorId(authorId);
            //随机生成Id
            comment.setId(generator.generateUniqueNumber(commentator, nowTime));
            comment.setStatus("publish");
            commentMapper.addComment(comment);

            articleSerivce.updateArticleComments(articleId, 0);

            return comment;
        }catch (Exception e) {
            throw new GlobalException(e.getMessage(), Result.ERROR);
        }
    }

    @Override
    public List<Comment> getCommentListByPage(int pageNum, int pageSize, int articleId) {
        if(pageNum<=0) {
            throw new GlobalException("页码错误",Result.WRONG_PAGE_NUM);
        }
        if (pageSize<=0) {
            throw new GlobalException("页面大小错误",Result.WRONG_PAGE_SIZE);
        }
        PageHelper.startPage(pageNum, pageSize);
        return commentMapper.getCommentList(articleId);
    }
}
