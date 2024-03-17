package org.example.quantumblog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumblog.cond.CommentCond;
import org.example.quantumblog.model.Comment;

import java.util.List;

/**
 * @author xiaol
 */
@Mapper
public interface CommentMapper {
    //添加评论
    @Insert("INSERT INTO Comment (" +
            "content, " +
            "articleTitle, " +
            "commentator," +
            " parentCommentator," +
            " likes, " +
            "createTime," +
            "id," +
            "authorId,"+
            "articleId,"+
            " updateTime, " +
            "status) " +
            "VALUES (#{content}, " +
            "#{articleTitle}, " +
            "#{commentator}," +
            " #{parentCommentator}, " +
            "#{likes}, " +
            "#{createTime}," +
            "#{id}," +
            "#{authorId},"+
            "#{articleId},"+
            " #{updateTime}, " +
            "#{status})")
    void addComment(Comment comment);

    @Select("SELECT * FROM Comment WHERE articleId = #{articleId}")
    List<Comment> getCommentList(int articleId);
}
