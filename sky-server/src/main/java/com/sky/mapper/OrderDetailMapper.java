package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细
     * @param orderDetailList
     * @return
     */
    Integer insertOrderDetailByList(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单菜品
     * @param id
     * @return
     */
    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> selectByOrderId(String id);
}
