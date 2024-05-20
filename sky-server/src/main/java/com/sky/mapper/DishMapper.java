package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 根据条件分页查询代码
     * @return
     */
    Page<DishVO> selectDishForPageByCondition(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    Integer deleteDishByIds(List<String> ids);

    /**
     * 通过id得到菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(String id);

    /**
     * 更新菜品信息
     * @param dish
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    Integer updateDish(Dish dish);

    /**
     * 根据id查询菜品详细信息
     * @param id
     * @return
     */
    DishVO selectDishForDetailById(String id);

    /**
     * 根据分类id查询所有的菜品
     * @param dishDTO
     * @return
     */
    List<Dish> selectDishForListByCondition(DishDTO dishDTO);

    /**
     * 根据套餐id获取菜品
     * @param setmealId
     * @return
     */
    @Select("select d.* from dish d left join setmeal_dish sd on sd.dish_id = d.id where sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(String setmealId);
}
