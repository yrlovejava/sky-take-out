package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单记录
     * @param orders
     * @return
     */
    @Insert("insert into orders " +
            "(id, number, status, user_id, address_book_id, order_time, pay_status, amount, remark, phone, address, user_name, consignee, delivery_status, pack_amount, tableware_number, tableware_status,estimated_delivery_time) values " +
            "(#{id},#{number},#{status},#{userId},#{addressBookId},#{orderTime},#{payStatus},#{amount},#{remark},#{phone},#{address},#{userName},#{consignee},#{deliveryStatus},#{packAmount},#{tablewareNumber},#{tablewareStatus},#{estimatedDeliveryTime})")
    Integer insertOrder(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单状态查询各个状态的数量
     * @param status
     * @return
     */
    @Select("select count(*) from orders where status = #{status}")
    Integer selectCountByStatus(Integer status);

    /**
     * 根据条件分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> selectOrdersForPageByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     */
    @Select("select * from orders where id = #{id}")
    Orders selectById(String id);

    /**
     * 根据状态和指定时间来查询订单
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{time}")
    List<Orders> selectOrderByStatusAndTime(Integer status, LocalDateTime time);

}
