package org.example.quantumblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author xiaol
 */
@Mapper
public interface UserProfileMapper {
    //插入用户名
    @Select("insert into user_profile(username) values(#{username})")
    void insertUsername(String username);

    //修改头像
    @Select("update user_profile set avatar=#{url} where username=#{username}")
    void changeAvatar(String username, String url);
}
