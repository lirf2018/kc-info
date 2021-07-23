package com.yufan.kc.dao.timetask.impl;

import com.yufan.common.dao.base.IGeneralDao;
import com.yufan.kc.dao.timetask.TimeTaskDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 创建人: lirf
 * 创建时间:  2020/12/20 9:43
 * 功能介绍:
 */
@Repository
public class TimeTaskDaoImpl implements TimeTaskDao {

    @Autowired
    private IGeneralDao iGeneralDao;

    @Override
    public void updateGoodsStore(String goodsCode) {
//        String delTempSql = " delete from tb_temp where tmp_type=1 ";
//        iGeneralDao.executeUpdateForSQL(delTempSql);
//        //
//        String uuid = UUID.randomUUID() + "" + System.currentTimeMillis();
//        uuid = uuid.replace("-", "");
//        // 插入临时表
//        StringBuffer sqlOut = new StringBuffer();
//        sqlOut.append(" INSERT into tb_temp(goods_code,sale_count,uuid_key,tmp_type) (select de.goods_code,count(de.goods_code) as c,'").append(uuid).append("',1 ");
//        sqlOut.append("  from tb_kc_order o JOIN  ");
//        sqlOut.append(" tb_kc_order_detail de on de.order_id=o.order_id ");
//        sqlOut.append(" where o.order_status>0 and de.`status`=1 ");
//        if (StringUtils.isNotEmpty(goodsCode)) {
//            sqlOut.append(" and de.goods_code='").append(goodsCode).append("' ");
//        }
//        sqlOut.append(" GROUP BY de.goods_code)  ");
//        iGeneralDao.executeUpdateForSQL(sqlOut.toString());
//
//        StringBuffer sql = new StringBuffer();
//        sql.append(" update tb_kc_goods_store gs join ");
//        sql.append(" (select count(i.goods_code) as c,i.goods_code from tb_store_inout i where i.income_type=1 and i.`status`=1  ");
//        if (StringUtils.isNotEmpty(goodsCode)) {
//            sql.append(" and i.goods_code='").append(goodsCode).append("' ");
//        }
//        sql.append(" and i.is_matching=1 GROUP BY ");
//        sql.append(" i.goods_code) s on s.goods_code=gs.goods_code  JOIN tb_temp t on t.goods_code=gs.goods_code and t.uuid_key='").append(uuid).append("' ");
//        sql.append(" set gs.store=s.c-t.sale_count,gs.update_type=0,last_update_time=now() ");
//        iGeneralDao.executeUpdateForSQL(sql.toString());
    }

    @Override
    public void delGoodsReportData(String year, String month) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE from tb_goods_sale_month_report where sale_year='").append(year).append("' ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and sale_month='").append(month).append("'  ");
        }
        iGeneralDao.executeUpdateForSQL(sql.toString());
    }

    @Override
    public List<Map<String, Object>> findGoodsOrderSaleData(String year, String month) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT d.goods_code,d.goods_name,d.goods_unit_name,d.unit_count,SUM(d.buy_count) as buy_count,SUM(d.sale_price_true*d.buy_count) as sale_price_all,DATE_FORMAT(o.pay_date,'%m') as pay_date ");
        sql.append(" from tb_kc_order o JOIN tb_kc_order_detail d on o.order_id = d.order_id ");
        sql.append(" where 1=1 ");
        sql.append(" and o.order_status = 1 ");
        sql.append(" and d.status = 1 ");
        sql.append(" and DATE_FORMAT(o.pay_date,'%Y') = '").append(year).append("' ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and DATE_FORMAT(o.pay_date,'%m') = '").append(month).append("' ");
        }
        sql.append(" GROUP BY d.goods_code,DATE_FORMAT(o.pay_date,'%m') ");
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

    @Override
    public List<Map<String, Object>> findStoreInComeData(String year, String month) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select s.goods_code,s.goods_name,p.param_value as goods_unit_name,s.unit_count,COUNT(s.goods_code) as in_count,SUM(s.income_price) as income_price_all,DATE_FORMAT(s.in_time,'%m') as in_time ");
        sql.append(" from tb_store_inout s LEFT JOIN tb_param p on p.param_code='goods_unit' and p.`status`=1 and p.param_key=s.goods_unit ");
        sql.append(" where 1=1 ");
        sql.append(" and s.income_type = 1 and s.status != 2 ");
        sql.append(" and DATE_FORMAT(s.in_time,'%Y')='").append(year).append("' ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and DATE_FORMAT(s.in_time,'%m')='").append(month).append("' ");
        }
        sql.append(" GROUP BY s.goods_code,DATE_FORMAT(s.in_time,'%m') ");
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

    @Override
    public void delOrderReportData(String year, String month) {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE from tb_kc_order_month_report where sale_year='").append(year).append("' ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and sale_month='").append(month).append("'  ");
        }
        iGeneralDao.executeUpdateForSQL(sql.toString());
    }

    @Override
    public List<Map<String, Object>> findOrderSaleData(String year, String month) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUM(o.real_inpay_price) as sale_price_all,DATE_FORMAT(o.pay_date,'%m') as sale_month  ");
        sql.append(" from tb_kc_order o  ");
        sql.append(" where 1=1  ");
        sql.append(" and o.order_status = 1  ");
        sql.append(" and DATE_FORMAT(o.pay_date,'%Y') = '").append(year).append("'  ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and DATE_FORMAT(o.pay_date,'%m') = '").append(month).append("'  ");
        }
        sql.append(" GROUP BY DATE_FORMAT(o.pay_date,'%m')  ");
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

    @Override
    public List<Map<String, Object>> findStoreData(String year, String month) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select SUM(s.income_price) as income_price_all  ,DATE_FORMAT(s.in_time,'%m') as in_month  ");
        sql.append(" from tb_store_inout s  ");
        sql.append(" where 1=1  ");
        sql.append(" and s.income_type = 1 and s.status != 2  ");
        sql.append(" and DATE_FORMAT(s.in_time,'%Y')='").append(year).append("'  ");
        if (StringUtils.isNotEmpty(month)) {
            sql.append(" and DATE_FORMAT(s.in_time,'%m') = '").append(month).append("'  ");
        }
        sql.append(" GROUP BY DATE_FORMAT(s.in_time,'%m')  ");
        return iGeneralDao.getBySQLListMap(sql.toString());
    }

    @Override
    public void saveObj(Object object) {
        iGeneralDao.save(object);
    }
}
