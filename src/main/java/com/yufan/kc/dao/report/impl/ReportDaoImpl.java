package com.yufan.kc.dao.report.impl;

import com.yufan.common.bean.ConditionCommon;
import com.yufan.common.dao.base.IGeneralDao;
import com.yufan.kc.dao.report.ReportDao;
import com.yufan.utils.Constants;
import com.yufan.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 创建人: lirf
 * 创建时间:  2020/12/26 22:43
 * 功能介绍:
 */
@Transactional
@Repository
public class ReportDaoImpl implements ReportDao {

    @Autowired
    private IGeneralDao iGeneralDao;

    @Override
    public PageInfo loadGoodsReportPage(ConditionCommon conditionCommon) {
        StringBuffer sql = new StringBuffer();

        sql.append("  select s.goods_code,s.goods_name,ifnull(ss.sale_price_all,0) as sale_price_all,ifnull(ss.income_price_all,0) as income_price_all,ifnull(ss.sale_price_all-ss.income_price_all,0) as get_price, ");
        sql.append("  IFNULL(ss.income_count,0) as income_count,ifnull(ss.sale_count,0) as sale_count, ");
        sql.append("  IFNULL(g.status ,-1)  as sale_status ");
        sql.append(" ,IFNULL(ss.unit_count,s.unit_count) as unit_count,IFNULL(ss.goods_unit_name,s.param_value)  as goods_unit_name ");
        sql.append(" ,IFNULL(gst.store,0) as store_all,IFNULL(gst.store_sale,0) as store_sale_all ");
        sql.append("  from (select t.goods_code,t.goods_name,t.unit_count,p.param_value  from tb_store_inout t LEFT JOIN tb_param p on p.`status`=1 and p.param_code='goods_unit' and p.param_key=t.unit_count where t.income_type = 1 and t.status != 2 GROUP BY t.goods_code) s ");
        sql.append("  LEFT JOIN tb_kc_goods g on g.goods_code=s.goods_code ");
        sql.append("  LEFT JOIN tb_kc_goods_store gst on gst.goods_code=s.goods_code ");
        sql.append("  LEFT JOIN ");
        sql.append(" ( ");
        //
        appendGoodsReportCondition(sql, conditionCommon);

        sql.append("  ) ss on ss.goods_code = s.goods_code ");
        sql.append("  ORDER BY ss.sale_price_all desc,g.last_update_time desc ");


        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(conditionCommon.getPageSize() == null ? 20 : conditionCommon.getPageSize());
        pageInfo.setCurrePage(conditionCommon.getCurrePage());
        pageInfo.setSqlQuery(sql.toString());
        pageInfo = iGeneralDao.loadPageInfoSQLListMap(pageInfo);
        return pageInfo;
    }

    @Override
    public List<Map<String, Object>> loadGoodsReportCount(ConditionCommon conditionCommon) {
        StringBuffer sql = new StringBuffer();
        appendGoodsReportCondition(sql, conditionCommon);
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

    private void appendGoodsReportCondition(StringBuffer sql, ConditionCommon conditionCommon) {
        sql.append("  select goods_code,SUM(sale_price_all) as sale_price_all,SUM(income_price_all) as income_price_all,SUM(sale_count) as sale_count,SUM(income_count) as income_count ");
        sql.append(" ,unit_count,goods_unit_name ");
        sql.append("  from tb_goods_sale_month_report where 1=1 ");
        sql.append("  and sale_year='").append(conditionCommon.getReportYear()).append("' ");
        if ("season".equals(conditionCommon.getReportType())) {
            sql.append("  and sale_season=").append(conditionCommon.getReportCondition()).append(" ");
        } else if ("month".equals(conditionCommon.getReportType())) {
            String data = conditionCommon.getReportCondition();
            sql.append("  and sale_month='").append(data.split("-")[1]).append("' ");
        }
        sql.append("  GROUP BY goods_code ");
    }

    @Override
    public List<Map<String, Object>> loadOrderReportPage(ConditionCommon conditionCommon) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select CONCAT(mm.m,'月') as sale_month,IFNULL(rp.order_price_all,0) as order_price_all,IFNULL(rp.goods_inprice_all,0) as goods_inprice_all, ");
        sql.append(" IFNULL((rp.order_price_all-rp.goods_inprice_all),0) as get_price_all ");
        sql.append(" from ");
        sql.append(" (select mon.m from ( ");
        sql.append(" SELECT '01' as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '02'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '03'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '04'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '05'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '06'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '07'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '08'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '09'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '10'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '11'  as m from dual ");
        sql.append(" UNION All ");
        sql.append(" SELECT '12'  as m from dual) as mon ) mm ");
        sql.append(" LEFT JOIN tb_kc_order_month_report rp on rp.sale_month=mm.m  ");
        sql.append(" and rp.sale_year='").append(conditionCommon.getReportYear()).append("' ");
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

}
