package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据条件动态查询购物车中的商品
     * @param shoppingCart
     * @return
     */
    ShoppingCart selectShoppingCartByCondition(ShoppingCart shoppingCart);

    /**
     * 修改商品的数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart " +
            "(id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) values " +
            "(#{id},#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    Integer insertShoppingCart(ShoppingCart shoppingCart);

    /**
     * 根据用户id查询购物车中所有菜品
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> selectAllByUserId(String userId);

    /**
     * 根据用户id清除购物车中的所有数据
     * @param userId
     * @return
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    Integer deleteAllByUserId(String userId);

    /**
     * 根据条件清理购物车
     * @param shoppingCart
     * @return
     */
    Integer deleteShoppingCartByCondition(ShoppingCart shoppingCart);

    /**
     * 批量插入购物车中的商品
     * @param shoppingCartList
     * @return
     */
    Integer insertShoppingCartByList(List<ShoppingCart> shoppingCartList);
}
