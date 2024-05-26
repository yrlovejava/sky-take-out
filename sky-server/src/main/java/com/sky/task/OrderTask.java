package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 自定义定时任务类,处理订单支付超时和配送超时
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未支付的订单
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void processTimeOutOrder(){
        log.info("定时处理超时订单: {}", LocalDateTime.now());

        //查询超时订单
        List<Orders> orders = orderMapper.selectOrderByStatusAndTime(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

        //取消订单
        if(orders != null && !orders.isEmpty()){
            for (Orders order : orders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("订单超时");
                //调用mapper
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨触发一次
    public void processDeliveryOrder(){
        log.info("定时处理一直派送中的订单: {}",LocalDateTime.now());

        //查询处于派送中的订单
        List<Orders> orders = orderMapper.selectOrderByStatusAndTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));

        //自动完成
        if(orders != null && !orders.isEmpty()){
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);

                //调用mapper
                orderMapper.update(order);
            }
        }
    }
}
