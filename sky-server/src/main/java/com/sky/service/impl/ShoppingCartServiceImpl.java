package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.utils.UUIDUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入到购物车的商品是否已经存在了
        //封装实体类
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        ShoppingCart cart = shoppingCartMapper.selectShoppingCartByCondition(shoppingCart);

        //如果已经存在了，只需要将数量加1
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            //调用mapper，在数据库中修改数量
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在那么就需要插入一条购物车数据
            //首先判断新添加的商品是菜品还是套餐，因为这里shoppingCart表中的冗余字段前端并没有传递过来，所以要保存要从数据库中查出来之后再保存在表中
            if (shoppingCartDTO.getDishId() != null) {
                //说明添加的是菜品
                Dish dish = dishMapper.getById(shoppingCart.getDishId());
                //填充冗余字段
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            } else if (shoppingCartDTO.getSetmealId() != null) {
                //说明添加的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
                //填充冗余字段
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            //填充数量
            shoppingCart.setNumber(1);
            //生成id
            shoppingCart.setId(UUIDUtil.getUUID());
            //设置创建时间(没法使用AOP，因为这里只有创建时间)
            shoppingCart.setCreateTime(LocalDateTime.now());
            //调用mapper，添加记录
            shoppingCartMapper.insertShoppingCart(shoppingCart);
        }

    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCarts() {
        //获取用户id
        String userId = BaseContext.getCurrentId();
        //根据用户id查询所有的商品
        return shoppingCartMapper.selectAllByUserId(userId);
    }
}
