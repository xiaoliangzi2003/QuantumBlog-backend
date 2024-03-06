package org.example.quantumblog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.quantumblog.entity.user.User;

/**
 * @author xiaol
 */
@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    public User getUserById(String id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    public User getUserByUsername(String username);

    @Select("SELECT * FROM user WHERE username = #{username}AND password = #{password}")
    public User getUserByUsernameAndPassword(String username,String password);

    @Select("SELECT * FROM user WHERE email = #{email}")
    public User getUserByEmail(String email);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    public User getUserByPhone(String phone);

    @Select("SELECT * FROM user WHERE username = #{username}AND email = #{email}")
    public User getUserByUsernameAndEmail(String username,String email);

    @Select("INSERT INTO user (id, username, password,email,phone) VALUES (#{id}, #{username}, #{password},#{email},#{phone})")
    public void insertUser(User user);

    @Select("UPDATE user SET username = #{username}, password = #{password} WHERE id = #{id}")
    public void updateUser(User user);

    @Select("DELETE FROM user WHERE id = #{id}")
    public void deleteUser(String id);
}
