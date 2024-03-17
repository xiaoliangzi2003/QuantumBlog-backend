package org.example.quantumblog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author xiaol
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfile {
    //昵称
    String nickname;
    //关联的用户名
    String username;
    //头像
    String avatar;
    //性别
    String gender;
    //生日
    String birthday;
    //个性签名
    String signature;
    //收藏夹(文章id列表)
    List<Integer> collections;
    //浏览记录(文章id列表)
    List<Integer> history;
    //关注列表(用户id列表)
    List<Integer> followings;
    //粉丝列表(用户id列表)
    List<Integer> followers;
    //文章列表(文章id列表)
    List<Integer> articles;
    //评论列表(评论id列表)
    List<Integer> comments;
    //点赞列表(文章id列表)
    List<Integer> likes;
    //转发列表(文章id列表)
    List<Integer> forwards;
}
