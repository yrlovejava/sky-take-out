package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}
