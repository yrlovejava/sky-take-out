package com.sky.service;


import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.*;

public interface OrderService {

    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 统计各个状态的订单数量
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 根据条件分页查询订单
     * @return
     */
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 配送订单
     * @param id
     */
    void delivery(String id);

    /**
     * 查询订单的详细信息
     * @param id
     * @return
     */
    OrderVO details(String id);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒绝接单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 完成订单
     * @param id
     */
    void complete(String id);

    PageResult pageForUser(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 再来一单
     * @param id
     */
    void repetition(String id);

    /**
     * 用户取消订单
     * @param id
     */
    void userCancel(String id);
}
