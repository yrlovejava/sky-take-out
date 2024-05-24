package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid来查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getUserByOpenid(String openid);

    @Insert("insert into user (id,openid,create_time) values (#{id},#{openid},#{createTime})")
    Integer insertUser(User user);

    @Select("select * from user where id = #{id}")
    User getUserById(String id);
}
