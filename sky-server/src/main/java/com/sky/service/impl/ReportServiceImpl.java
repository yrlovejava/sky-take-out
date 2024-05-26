package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表
        //存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        while (begin.isBefore(end)){
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        dateList.add(end);

        //调用mapper查询begin - end 每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据，营业额是指：状态为"已完成"的订单金额总计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String,Object> map = new HashMap<>();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);

            Double sum = orderMapper.selectOneDayAmountByMap(map);
            //如果没有订单营业额不是0 是null 所以要处理null异常
            sum = sum == null ? 0.00 : sum;
            turnoverList.add(sum);
        }

        //封装返回vo
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        //封装为String
        String dateListStr = StringUtils.join(dateList, ",");
        String turnoverListStr = StringUtils.join(turnoverList, ",");
        turnoverReportVO.setDateList(dateListStr);
        turnoverReportVO.setTurnoverList(turnoverListStr);

        return turnoverReportVO;
    }
}
