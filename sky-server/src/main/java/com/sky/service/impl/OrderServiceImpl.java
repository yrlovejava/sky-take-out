package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.UUIDUtil;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //处理业务异常(地址簿为空，购物车数据为空)
        String addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        //如果是正常的下单就不会有这些问题，但是如果是通过其他的手段绕过前端直接发起请求，就需要保证数据安全，所以后端需要校验
        if(addressBook==null){
            //地址为空或者id不合法，抛出异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //根据用户id查询购物车中的信息
        String userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectAllByUserId(userId);
        if(shoppingCartList == null || shoppingCartList.isEmpty()){
            //购物车数据为空，抛出异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向订单表中插入一条数据
        //封装实体类
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setId(UUIDUtil.getUUID());
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        //使用时间戳来生成订单号
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setOrderTime(LocalDateTime.now());
        String address = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();
        orders.setAddress(address);
        //调用mapper查询username
        User user = userMapper.getUserById(userId);
        orders.setUserName(user.getName());
        //调用mapper插入数据
        orderMapper.insertOrder(orders);

        //向订单明细表中插入一条或多条数据
        //封装参数
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail;
        for (ShoppingCart cart : shoppingCartList) {
            orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetail.setId(UUIDUtil.getUUID());
            orderDetailList.add(orderDetail);
        }
        //调用mapper
        orderDetailMapper.insertOrderDetailByList(orderDetailList);

        //清空当前用户的购物车数据
        shoppingCartMapper.deleteAllByUserId(userId);

        //封装返回的实体类
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

        return orderSubmitVO;
    }
}
