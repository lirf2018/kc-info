package com.yufan.kc.timetask.impl;

import com.yufan.kc.bean.TbGoodsSaleMonthReport;
import com.yufan.kc.bean.TbKcOrderMonthReport;
import com.yufan.kc.dao.timetask.TimeTaskDao;
import com.yufan.kc.timetask.IGoodsSaleReport;
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
 * 创建时间:  2020/12/20 12:52
 * 功能介绍: 商品销售报表(商品销售额)（商品进货额）
 */
@Configuration
@EnableScheduling
@Transactional
public class GoodsSaleReportImpl implements IGoodsSaleReport {
    private Logger LOG = Logger.getLogger(GoodsSaleReportImpl.class);

    @Autowired
    private TimeTaskDao timeTaskDao;

    /**
     * 每天执行一次，计算上一天的销售数据
     */
    @Scheduled(cron = "0 31 1 * * ?")
    public void goodsStoreInPriceAll() {
        long st = System.currentTimeMillis();
        String now = DatetimeUtil.getNow();
        String year = now.split("-")[0];
        String month = now.split("-")[1];
        initGoodsReportData(year, month);
        long et = System.currentTimeMillis();
        LOG.info("---更新商品销售报表--用时-----=" + (et - st) + " ");
    }

    @Override
    public void goodsReport(String year, String month) {
        initGoodsReportData(year, month);
    }

    /**
     * 根据条件初始化数据,时间条件初始化有数值的商品数据,包括入账数据和出账数据
     */
    private void initGoodsReportData(String year, String month) {
        Map<String, TbGoodsSaleMonthReport> dataMap = new HashMap<>();
        //(1)删除条件数按 year 和 month
        timeTaskDao.delGoodsReportData(year, month);
        //(2)查询订单销售数按 year 和 month
        List<Map<String, Object>> saleDataList = timeTaskDao.findGoodsOrderSaleData(year, month);
        for (int i = 0; i < saleDataList.size(); i++) {
            TbGoodsSaleMonthReport report = new TbGoodsSaleMonthReport();
            String payDate = saleDataList.get(i).get("pay_date").toString();
            String goodsCode = saleDataList.get(i).get("goods_code").toString();
            String goodsName = saleDataList.get(i).get("goods_name").toString();
            String goodsUnitName = saleDataList.get(i).get("goods_unit_name").toString();
            String unitCount = saleDataList.get(i).get("unit_count").toString();
            String buyCount = saleDataList.get(i).get("buy_count").toString();
            String salePriceAll = saleDataList.get(i).get("sale_price_all").toString();
            initGoodsSaleMonthReport(report, year, payDate);
            //
            report.setGoodsCode(goodsCode);
            report.setGoodsName(goodsName);
            report.setGoodsUnitName(goodsUnitName);
            report.setUnitCount(Integer.parseInt(unitCount));
            report.setSaleCount(Integer.parseInt(buyCount));
            report.setSalePriceAll(new BigDecimal(salePriceAll));
            dataMap.put(goodsCode, report);
        }
        //(3)查询进货数按 year 和 month
        List<Map<String, Object>> incomeDataList = timeTaskDao.findStoreInComeData(year, month);
        for (int i = 0; i < incomeDataList.size(); i++) {
            String inTime = incomeDataList.get(i).get("in_time").toString();
            String goodsCode = incomeDataList.get(i).get("goods_code").toString();
            String goodsName = incomeDataList.get(i).get("goods_name").toString();
            String goodsUnitName = incomeDataList.get(i).get("goods_unit_name").toString();
            String unitCount = incomeDataList.get(i).get("unit_count").toString();
            String inCount = incomeDataList.get(i).get("in_count").toString();
            String incomePriceAll = incomeDataList.get(i).get("income_price_all").toString();
            TbGoodsSaleMonthReport report = dataMap.get(goodsCode);
            if (null == report) {
                report = new TbGoodsSaleMonthReport();
                initGoodsSaleMonthReport(report, year, inTime);
            }
            report.setGoodsCode(goodsCode);
            report.setGoodsName(goodsName);
            report.setGoodsUnitName(goodsUnitName);
            report.setUnitCount(Integer.parseInt(unitCount));
            report.setIncomeCount(Integer.parseInt(inCount));
            report.setIncomePriceAll(new BigDecimal(incomePriceAll));
            dataMap.put(goodsCode, report);
        }
        //(4)生成报表数据  year 和 month
        for (Map.Entry<String, TbGoodsSaleMonthReport> map : dataMap.entrySet()) {
            TbGoodsSaleMonthReport orderMonthReport = map.getValue();
            orderMonthReport.setId(0);
            timeTaskDao.saveObj(orderMonthReport);
        }
    }

    private void initGoodsSaleMonthReport(TbGoodsSaleMonthReport report, String year, String month) {
        report.setSaleReport(Integer.parseInt(year + month));
        report.setGoodsCode("");
        report.setGoodsName("");
        report.setSalePriceAll(BigDecimal.ZERO);
        report.setSaleCount(1);
        report.setIncomePriceAll(BigDecimal.ZERO);
        report.setIncomeCount(0);
        report.setUpdateTime(new Timestamp(new Date().getTime()));
        report.setSaleMonth(month);
        report.setSaleYear(year);
        report.setUnitCount(1);
        report.setGoodsUnitName("");
        report.setSaleSeason(CommonMethod.getSaleSeason(month));
    }
}
