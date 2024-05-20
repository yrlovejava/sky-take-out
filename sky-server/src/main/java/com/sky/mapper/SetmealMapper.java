package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(String id);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into setmeal " +
            "(id, category_id, name, price, description, image,status,create_time,create_user,update_time,update_user) values " +
            "(#{id},#{categoryId},#{name},#{price},#{description},#{image},#{status},#{createTime},#{createUser},#{updateTime},#{updateUser})")
    Integer insertSetmeal(Setmeal setmeal);
}
