package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
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

    /**
     * 新增套餐
     * @param setmeal
     * @return
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into setmeal " +
            "(id, category_id, name, price, description, image,status,create_time,create_user,update_time,update_user) values " +
            "(#{id},#{categoryId},#{name},#{price},#{description},#{image},#{status},#{createTime},#{createUser},#{updateTime},#{updateUser})")
    Integer insertSetmeal(Setmeal setmeal);

    /**
     * 根据条件分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
