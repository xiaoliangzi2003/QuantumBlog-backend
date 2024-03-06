package org.example.quantumblog.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumblog.entity.blog.Article;

import java.util.Map;

/**
 * @author xiaol
 */
@Mapper
public interface ArticleMapper {
    //获取文章的作者
    @Select("SELECT author FROM article WHERE id = #{articleId}")
    public String getAuthorByArticleId(String articleId);

    //获取文章的标题
    @Select("SELECT title FROM article WHERE id = #{articleId}")
    public String getTitleByArticleId(String articleId);

    //获取文章的内容
    @Select("SELECT content FROM article WHERE id = #{articleId}")
    public String getContentByArticleId(String articleId);

    //获取文章的发布时间
    @Select("SELECT publishDate FROM article WHERE id = #{articleId}")
    public String getPublishDateByArticleId(String articleId);

    //获取文章的编辑时间
    @Select("SELECT editDate FROM article WHERE id = #{articleId}")
    public String getEditDateByArticleId(String articleId);

    //获取文章的点赞数
    @Select("SELECT likeCount FROM article WHERE id = #{articleId}")
    public String getLikeCountByArticleId(String articleId);

    //获取文章的评论数
    @Select("SELECT commentCount FROM article WHERE id = #{articleId}")
    public String getCommentCountByArticleId(String articleId);

    //获取文章的浏览数
    @Select("SELECT viewCount FROM article WHERE id = #{articleId}")
    public String getViewCountByArticleId(String articleId);

    //获取文章的收藏数
    @Select("SELECT collectCount FROM article WHERE id = #{articleId}")
    public String getCollectCountByArticleId(String articleId);

    //获取文章的状态
    @Select("SELECT status FROM article WHERE id = #{articleId}")
    public String getStatusByArticleId(String articleId);

    //获取文章的分类
    @Select("SELECT category_id FROM article WHERE id = #{articleId}")
    public String getCategoryIdByArticleId(String articleId);

    //获取文章的所有信息
    @Select("SELECT * FROM article WHERE id = #{articleId}")
    public Map<String,String> getArticleByArticleId(String articleId);

    //添加文章(标题、内容、作者、发布时间、编辑时间、点赞数、评论数、浏览数、收藏数、状态、分类)
    @Select("INSERT INTO article (title, content, authorId, publishDate, editDate, likeCount, commentCount, viewCount, collectCount, status, categoryId) VALUES (#{title}, #{content}, #{authorId}, #{publishDate}, #{editDate}, #{likeCount}, #{commentCount}, #{viewCount}, #{collectCount}, #{status}, #{categoryId})")
    public void insertArticle(Article article);

    //更新文章(标题、内容、作者、发布时间、编辑时间、点赞数、评论数、浏览数、收藏数、状态、分类)
    @Select("UPDATE article SET title = #{title}, content = #{content}, authorId = #{authorId}, publishDate = #{publishDate}, editDate = #{editDate}, likeCount = #{likeCount}, commentCount = #{commentCount}, viewCount = #{viewCount}, collectCount = #{collectCount}, status = #{status}, categoryId = #{categoryId} WHERE id = #{id}")
    public void updateArticle(Article article);

    //删除文章
    @Select("DELETE FROM article WHERE id = #{id}")
    public void deleteArticle(String id);

}
