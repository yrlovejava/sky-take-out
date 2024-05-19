package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(String categoryId);

    /**
     * 插入菜品
     * @param dish
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into dish " +
            "(id, name, category_id, price, image, description, create_time, update_time, create_user, update_user,status) values " +
            "(#{id},#{name},#{categoryId},#{price},#{image},#{description},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    Integer insertDish(Dish dish);
}
