package org.example.quantumblog.mapper;

import org.apache.ibatis.annotations.*;
import org.example.quantumblog.cond.ArticleCond;
import org.example.quantumblog.model.Article;

import java.util.List;

/**
 * @author xiaol
 */
@Mapper
public interface ArticleMapper {
    @Insert("INSERT INTO Article(id, title, summary, top, authorId, views, likes, comments, collects, forwards, createTimeStamp, updateTimeStamp, author, tags, type, status, url) VALUES(#{id}, #{title}, #{summary}, #{top}, #{authorId}, #{views}, #{likes}, #{comments}, #{collects}, #{forwards}, #{createTimeStamp}, #{updateTimeStamp}, #{author}, #{tags}, #{type}, #{status}, #{url})")
    void addArticle(Article article);

    @Delete("DELETE FROM Article WHERE id = #{id}")
    void deleteArticle(Integer id);

    @Update("UPDATE Article SET title = #{title}, summary = #{summary}, top = #{top}, authorId = #{authorId}, views = #{views}, likes = #{likes}, comments = #{comments}, collects = #{collects}, forwards = #{forwards}, createTimeStamp = #{createTimeStamp}, updateTimeStamp = #{updateTimeStamp}, author = #{author}, tags = #{tags}, type = #{type}, status = #{status}, url = #{url} WHERE id = #{id}")
    void updateArticle(Article article);

    @Select("SELECT * FROM Article WHERE id = #{id}")
    Article getArticleById(Integer id);

    @Select("SELECT * FROM Article")
    List<Article> getArticleList();

    @Select("SELECT * FROM Article LIMIT #{offset}, #{pageSize}")
    List<Article> getArticleByPage(int offset, int pageSize);

    @Select("SELECT * FROM article WHERE title = #{title}")
    Article getArticleByTitle(String title);
}