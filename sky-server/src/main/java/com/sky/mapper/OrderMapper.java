package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单记录
     * @param orders
     * @return
     */
    @Insert("insert into orders " +
            "(id, number, status, user_id, address_book_id, order_time, pay_status, amount, remark, phone, address, user_name, consignee, delivery_status, pack_amount, tableware_number, tableware_status) values " +
            "(#{id},#{number},#{status},#{userId},#{addressBookId},#{orderTime},#{payStatus},#{amount},#{remark},#{phone},#{address},#{userName},#{consignee},#{deliveryStatus},#{packAmount},#{tablewareNumber},#{tablewareStatus})")
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
}
