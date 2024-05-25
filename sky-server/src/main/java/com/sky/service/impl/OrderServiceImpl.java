package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.UUIDUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    //用来跳过微信支付
    private Orders orders;

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
        this.orders = orders;
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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        String userId = BaseContext.getCurrentId();
        User user = userMapper.getUserById(userId);

        //调用微信支付接口，生成预支付交易单
        //这里因为没法做微信支付，所以直接修改订单状态
        /*JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }*/

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        LocalDateTime check_out_time = LocalDateTime.now();//更新支付时间
        Orders orders = new Orders();
        orders.setStatus(Orders.TO_BE_CONFIRMED);//支付状态，已支付
        orders.setPayStatus(Orders.PAID);//订单状态，待接单
        orders.setCheckoutTime(check_out_time);
        orders.setId(this.orders.getId());
        orderMapper.update(orders);
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) throws Exception {
        //根据id查询订单
        Orders orders = orderMapper.selectById(ordersCancelDTO.getId());

        //如果是已经支付了的订单，就需要安排退款
        if(orders.getPayStatus().equals(Orders.PAID)){
            //因为没法完成退款的功能，所以修改订单状态来模拟
            /*String refund = weChatPayUtil.refund(
                    orders.getNumber(),
                    orders.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);*/
            log.info("申请退款");
        }

        //封装实体类
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setPayStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        //调用mapper
        orderMapper.update(orders);
    }

    /**
     * 统计各个状态的订单数量
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        //调用mapper查询
        Integer toBeConfirmedCount = orderMapper.selectCountByStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmedCount = orderMapper.selectCountByStatus(Orders.CONFIRMED);
        Integer deliveryInProgressCount = orderMapper.selectCountByStatus(Orders.DELIVERY_IN_PROGRESS);

        //封装返回vo
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedCount);
        orderStatisticsVO.setConfirmed(confirmedCount);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressCount);

        return orderStatisticsVO;
    }

    /**
     * 根据条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.selectOrdersForPageByCondition(ordersPageQueryDTO);

        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(),orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();

        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders,orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    /**
     * 配送订单
     * @param id
     */
    @Override
    public void delivery(String id) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(id);

        //判断订单是否存在或者是否是已接单
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(!orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //更新订单状态
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
     * 查询订单的详细信息
     * @param id
     * @return
     */
    @Override
    public OrderVO details(String id) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(id);

        //查询订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        //封装返回vo
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(ordersConfirmDTO.getId());

        //处理业务异常
        //订单不存在
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = orders.getStatus();
        if(!status.equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //封装实体类
        orders.setStatus(Orders.CONFIRMED);

        //调用mapper修改状态
        orderMapper.update(orders);
    }

    /**
     * 拒绝接单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(ordersRejectionDTO.getId());

        //处理业务异常
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Integer status = orders.getStatus();
        if(!status.equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Integer payStatus = orders.getPayStatus();
        if(payStatus.equals(Orders.PAID)){
            //用户已支付，需要退款
            /*String refund = weChatPayUtil.refund(
                    orders.getNumber(),
                    orders.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);*/
            log.info("申请退款");
        }

        //封装实体类
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());

        //调用mapper修改状态
        orderMapper.update(orders);
    }

    /**
     * 完成订单
     * @param id
     */
    @Override
    public void complete(String id) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(id);

        //处理业务异常
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(!orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //封装实体类
        orders.setStatus(Orders.CONFIRMED);
        orders.setDeliveryTime(LocalDateTime.now());

        //调用mapper修改
        orderMapper.update(orders);
    }

    /**
     * 查询历史订单(用户端)
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageForUser(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.selectOrdersForPageByCondition(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                String orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(String id) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(id);
        //根据订单id查找订单中菜品
        List<OrderDetail> orderDetailList = orderDetailMapper.selectByOrderId(orders.getId());

        //封装购物车
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ShoppingCart cart;
        for (OrderDetail orderDetail : orderDetailList) {
            cart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,cart);
            cart.setUserId(BaseContext.getCurrentId());
            cart.setId(UUIDUtil.getUUID());
            cart.setCreateTime(LocalDateTime.now());

            shoppingCartList.add(cart);
        }

        //将这些菜品重新放入购物车中(批量插入)
        shoppingCartMapper.insertShoppingCartByList(shoppingCartList);
    }

    /**
     * 用户取消订单
     * @param id
     */
    @Override
    public void userCancel(String id) {
        //根据id查询订单
        Orders orders = orderMapper.selectById(id);

        //处理业务异常
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(orders.getStatus() > Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //如果已经支付了，处于待接单的情况下是就需要退款
        if(orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            /*//调用微信支付退款接口
            weChatPayUtil.refund(
                    ordersDB.getNumber(), //商户订单号
                    ordersDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额*/
            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        //封装实体类
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason("用户取消");

        //调用mapper
        orderMapper.update(orders);
    }
}
