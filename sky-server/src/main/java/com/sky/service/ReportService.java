package com.sky.service;

import com.aliyuncs.http.HttpResponse;
import com.sky.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin,LocalDate end);

    /**
     * 用户数量统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistics(LocalDate begin,LocalDate end);

    /**
     * 订单数量统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量前10的商品统计
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    /**
     * 运营数据Excel导出
     */
    void export(HttpServletResponse resp);
}
