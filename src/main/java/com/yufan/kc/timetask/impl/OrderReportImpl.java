package com.yufan.kc.timetask.impl;

import com.yufan.kc.pojo.TbKcOrderMonthReport;
import com.yufan.kc.dao.timetask.TimeTaskDao;
import com.yufan.kc.timetask.IOrderReport;
import com.yufan.utils.CommonMethod;
import com.yufan.utils.DatetimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建人: lirf
 * 创建时间:  2020/12/20 12:53
 * 功能介绍: 订单报表
 */
@Configuration
@EnableScheduling
@Transactional
public class OrderReportImpl implements IOrderReport {

    private Logger LOG = Logger.getLogger(OrderReportImpl.class);

    @Autowired
    private TimeTaskDao timeTaskDao;

    @Scheduled(cron = "0 33 1 * * ?")
    public void orderSaleReport() {
        long st = System.currentTimeMillis();
        String now = DatetimeUtil.getNow();
        String year = now.split("-")[0];
        String month = now.split("-")[1];
        initOrderReportData(year, month);
        long et = System.currentTimeMillis();
        LOG.info("---更新订单报表--用时-----=" + (et - st) + " ");
    }

    @Override
    public void orderReport(String year, String month) {
        initOrderReportData(year, month);
    }

    /**
     * 根据条件初始化数据,时间条件初始化有数值的商品数据,包括入账数据和出账数据
     */
    private void initOrderReportData(String year, String month) {
        Map<String, TbKcOrderMonthReport> dataMap = new HashMap<>();
        //(1)删除条件数按 year 和 month
        timeTaskDao.delOrderReportData(year, month);
        //(2)查询订单销售数按 year 和 month
        List<Map<String, Object>> saleDataList = timeTaskDao.findOrderSaleData(year, month);
        for (int i = 0; i < saleDataList.size(); i++) {
            TbKcOrderMonthReport report = new TbKcOrderMonthReport();
            String saleMonth = saleDataList.get(i).get("sale_month").toString();
            String salePriceAll = saleDataList.get(i).get("sale_price_all").toString();
            initOrderSaleMonthReport(report, year, saleMonth);
            //
            report.setOrderPriceAll(new BigDecimal(salePriceAll));
            dataMap.put(saleMonth, report);
        }
        //(3)查询进货数按 year 和 month
        List<Map<String, Object>> incomeDataList = timeTaskDao.findStoreData(year, month);
        for (int i = 0; i < incomeDataList.size(); i++) {
            String inMonth = incomeDataList.get(i).get("in_month").toString();
            String incomePriceAll = incomeDataList.get(i).get("income_price_all").toString();
            TbKcOrderMonthReport report = dataMap.get(inMonth);
            if (null == report) {
                report = new TbKcOrderMonthReport();
                initOrderSaleMonthReport(report, year, inMonth);
            }
            report.setGoodsInpriceAll(new BigDecimal(incomePriceAll));
            dataMap.put(inMonth, report);
        }
        //(4)生成报表数据  year 和 month
        for (Map.Entry<String, TbKcOrderMonthReport> map : dataMap.entrySet()) {
            TbKcOrderMonthReport orderMonthReport = map.getValue();
            orderMonthReport.setId(0);
            timeTaskDao.saveObj(orderMonthReport);
        }
    }

    private void initOrderSaleMonthReport(TbKcOrderMonthReport report, String year, String month) {
        report.setSaleReport(Integer.parseInt(year + month));
        report.setOrderPriceAll(BigDecimal.ZERO);
        report.setGoodsInpriceAll(BigDecimal.ZERO);
        report.setUpdateTime(new Timestamp(new Date().getTime()));
        report.setSaleMonth(month);
        report.setSaleYear(year);
        report.setSaleSeason(CommonMethod.getSaleSeason(month));
    }
}
