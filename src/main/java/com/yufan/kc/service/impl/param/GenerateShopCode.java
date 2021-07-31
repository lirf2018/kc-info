package com.yufan.kc.service.impl.param;

import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.utils.ResultCode;
import com.yufan.common.service.IResultOut;
import com.yufan.kc.pojo.TbSequence;
import com.yufan.kc.dao.param.ParamDao;
import com.yufan.utils.DatetimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * 创建人: lirf
 * 创建时间:  2020/10/30 22:20
 * 功能介绍:
 */
@Service("generate_shop_code")
public class GenerateShopCode implements IResultOut {

    private Logger LOG = Logger.getLogger(GenerateShopCode.class);

    @Autowired
    private ParamDao paramDao;

    @Override
    public String getResult(ReceiveJsonBean receiveJsonBean) {
        JSONObject dataJson = new JSONObject();
        JSONObject data = receiveJsonBean.getData();
        try {


            return packagMsg(ResultCode.OK.getResp_code(), dataJson);
        } catch (Exception e) {
            LOG.error("-------error----", e);
        }
        return packagMsg(ResultCode.FAIL.getResp_code(), dataJson);
    }

    public synchronized String generate(int sequenceType, int count) {
        int minValue = 1000;
        int maxValue = 9998;
        String str = "";
        TbSequence sequence = paramDao.generateValue(sequenceType);
        if (count > 1 && (sequence.getSequenceValue() + count) > (maxValue + 1)) {
            sequence.setSequenceValue(minValue);
            paramDao.updateSetGenerateValue(sequenceType, minValue + count);
        } else {
            paramDao.updateGenerateValue(sequenceType, count);
            if ((sequence.getSequenceValue() + count) > maxValue) {
                // 重置 1000
                paramDao.resetGenerateValue(sequenceType);
            }
        }
        // 自动生成 18+3
        str = DatetimeUtil.getNow("yyyyMMddHHmmss") + sequence.getSequenceValue();
        return str;
    }

    @Override
    public boolean checkParam(ReceiveJsonBean receiveJsonBean) {
        JSONObject data = receiveJsonBean.getData();
        try {

            return true;
        } catch (Exception e) {
            LOG.error("----check-error---", e);
        }
        return false;
    }
}