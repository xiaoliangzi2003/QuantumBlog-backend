package org.example.quantumblog.mapper;

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
    @Select("insert into comment(content,articleId,authorId,parentId,likes,createTime,updateTime,author,status) values(#{content},#{articleId},#{authorId},#{parentId},#{likes},#{createTime},#{updateTime},#{author},#{status})")
    void addComment(Comment comment);

    //根据文章Id获取评论列表
    @Select("select * from comment where articleId=#{articleId}")
    List<Comment> getCommentListByArticleId(Integer articleId);

    //根据评论Id删除评论
    @Select("delete from comment where id=#{id}")
    void deleteCommentById(Integer id);

    //获取评论的总数
    @Select("select count(*) from comment")
    Integer getCommentCount();

    //更新评论的点赞量
    @Select("update comment set likes=#{likes} where id=#{id}")
    void updateCommentLikes(Integer id);

    //更新评论的状态
    @Select("update comment set status=#{status} where id=#{id}")
    void updateCommentStatus(Integer id, String status);

    //根据Id获取评论
    @Select("select * from comment where id=#{id}")
    Comment getCommentById(Integer id);

    //更新评论
    @Select("update comment set content=#{content},articleId=#{articleId},authorId=#{authorId},parentId=#{parentId},likes=#{likes},createTime=#{createTime},updateTime=#{updateTime},author=#{author},status=#{status} where id=#{id}")
    void updateComment(Comment comment);

    //根据条件获取评论列表
    @Select("select * from comment where 1=1 and parentId=#{parentId} and status=#{status}")
    List<Comment> getCommentByCond(CommentCond commentCond);
}
