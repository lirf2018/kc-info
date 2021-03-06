package com.yufan.kc.service.impl.order;

import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.utils.ResultCode;
import com.yufan.common.service.IResultOut;
import com.yufan.kc.pojo.TbKcOrder;
import com.yufan.kc.dao.order.OpenOrderDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * 创建人: lirf
 * 创建时间:  2020/11/21 22:18
 * 功能介绍: 重新设置订单支付(暂时不做)
 */
@Service("kc_update_order_status")
public class UpdateOrderPayStatus implements IResultOut {

    private Logger LOG = Logger.getLogger(UpdateOrderPayStatus.class);

    @Autowired
    private OpenOrderDao openOrderDao;

    @Override
    public String getResult(ReceiveJsonBean receiveJsonBean) {
        JSONObject dataJson = new JSONObject();
        JSONObject data = receiveJsonBean.getData();
        try {
            Integer status = data.getInteger("order_status");
            Integer payMethod = data.getInteger("pay_method");
            BigDecimal payPrice = data.getBigDecimal("pay_price");
            String orderNo = data.getString("order_no");
            //
            TbKcOrder order = openOrderDao.loadOrder(orderNo);
            if (null == order) {
                LOG.info("-------查询订单不存在--------");
                return packagMsg(ResultCode.ORDER_NOT_EXIST.getResp_code(), dataJson);
            }
            order.setOrderStatus(status.byteValue());
            if (null != payMethod) {
                order.setPayMethod(payMethod.byteValue());
            }
            order.setRealInpayPrice(payPrice);
            order.setPayDate(new Timestamp(new Date().getTime()));
            openOrderDao.updateObj(order);
            return packagMsg(ResultCode.OK.getResp_code(), dataJson);
        } catch (Exception e) {
            LOG.error("-------error----", e);
        }
        return packagMsg(ResultCode.FAIL.getResp_code(), dataJson);
    }


    @Override
    public boolean checkParam(ReceiveJsonBean receiveJsonBean) {
        JSONObject data = receiveJsonBean.getData();
        try {
            Integer status = data.getInteger("order_status");
            Integer payMethod = data.getInteger("pay_method");
            BigDecimal payPrice = data.getBigDecimal("pay_price");
            String orderNo = data.getString("order_no");
            if (null == status || status == 0 || payPrice == null || payMethod == null || StringUtils.isEmpty(orderNo)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOG.error("----check-error---", e);
        }
        return false;
    }
}