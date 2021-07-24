package com.yufan.kc.dao.timetask;

import java.util.List;
import java.util.Map;

/**
 * 创建人: lirf
 * 创建时间:  2020/12/20 9:42
 * 功能介绍:
 */
public interface TimeTaskDao {


    /**
     * 更新商品库存
     */
    void updateGoodsStore();
    ////////////////开始////商品报表///////////////////

    /**
     * 删除条件数按 year 和 month
     *
     * @param year
     * @param month
     */
    void delGoodsReportData(String year, String month);


    /**
     * 查询订单销售数按 year 和 month
     *
     * @param year
     * @param month
     * @return
     */
    List<Map<String, Object>> findGoodsOrderSaleData(String year, String month);

    /**
     * 查询进货数按 year 和 month
     *
     * @param year
     * @param month
     * @return
     */
    List<Map<String, Object>> findStoreInComeData(String year, String month);
    ////////////////结束////商品报表///////////////////


    ////////////////开始////订单报表///////////////////

    /**
     * 删除条件数按 year 和 month
     *
     * @param year
     * @param month
     */
    void delOrderReportData(String year, String month);


    /**
     * 查询订单销售数按 year 和 month
     *
     * @param year
     * @param month
     * @return
     */
    List<Map<String, Object>> findOrderSaleData(String year, String month);

    /**
     * 查询进货数按 year 和 month
     *
     * @param year
     * @param month
     * @return
     */
    List<Map<String, Object>> findStoreData(String year, String month);

    ////////////////结束////订单报表///////////////////

    void saveObj(Object object);


}
