package com.yufan.kc.service.impl.report;

import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.common.service.IResultOut;
import com.yufan.kc.timetask.IGoodsSaleReport;
import com.yufan.utils.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * @description: 手动生成商品月报表
 * @author: lirf
 * @time: 2021/7/23
 */
@Service("create_goods_report")
public class CreateGoodsReport implements IResultOut {

    private Logger LOG = Logger.getLogger(CreateGoodsReport.class);


    @Autowired
    private IGoodsSaleReport iGoodsSaleReport;

    @Override
    public String getResult(ReceiveJsonBean receiveJsonBean) {
        JSONObject dataJson = new JSONObject();
        JSONObject data = receiveJsonBean.getData();
        try {
            String year = data.getString("year");
            String month = data.getString("month");
            iGoodsSaleReport.goodsReport(year, month);
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
            String year = data.getString("year");
            if (StringUtils.isEmpty(year)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOG.error("----check-error---", e);
        }
        return false;
    }

    public static void main(String[] args) {

    }
}