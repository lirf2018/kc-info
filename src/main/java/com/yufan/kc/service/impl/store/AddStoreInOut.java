package com.yufan.kc.service.impl.store;

import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.utils.ResultCode;
import com.yufan.common.service.IResultOut;
import com.yufan.kc.pojo.TbStoreInout;
import com.yufan.kc.dao.store.StoreInOutDao;
import com.yufan.kc.service.impl.param.GenerateShopCode;
import com.yufan.utils.Constants;
import com.yufan.utils.DatetimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * 创建人: lirf
 * 创建时间:  2020/10/29 21:25
 * 功能介绍:
 */
@Service("kc_add_store")
public class AddStoreInOut implements IResultOut {

    private Logger LOG = Logger.getLogger(AddStoreInOut.class);

    @Autowired
    private StoreInOutDao storeInOutDao;

    @Autowired
    private GenerateShopCode generateShopCode;

    @Override
    public String getResult(ReceiveJsonBean receiveJsonBean) {
        JSONObject dataJson = new JSONObject();
        JSONObject data = receiveJsonBean.getData();
        try {
            // 相同商品入库数量
            Integer store = data.getInteger("store");
            Integer warning = data.getInteger("warning");
            Integer endTimeType = data.getInteger("endTimeType");//0 结束日期1年2月3天
            Integer year = data.getInteger("year");
            Integer month = data.getInteger("month");
            TbStoreInout storeInout = JSONObject.toJavaObject(data, TbStoreInout.class);
            storeInout.setLastUpdateTime(new Timestamp(new Date().getTime()));
            // 计算截止天数
            if (endTimeType == 0) {
                // 计算有效天数
                Long day = (storeInout.getEffectToTime().getTime() - storeInout.getMakeDay().getTime()) / (1000 * 60 * 60 * 24) + 1;
                storeInout.setEffectDay(day.intValue());
            } else if (endTimeType == 1) {
                Date date = DatetimeUtil.convertStrToDate(DatetimeUtil.timeStamp2Date(storeInout.getMakeDay().getTime(), null));
                storeInout.setEffectToTime(new Timestamp(DatetimeUtil.addYears(date, year).getTime()));
            } else if (endTimeType == 2) {
                Date date = DatetimeUtil.convertStrToDate(DatetimeUtil.timeStamp2Date(storeInout.getMakeDay().getTime(), null));
                storeInout.setEffectToTime(new Timestamp(DatetimeUtil.addMonths(date, month).getTime()));
            } else {
                Date date = DatetimeUtil.convertStrToDate(DatetimeUtil.timeStamp2Date(storeInout.getMakeDay().getTime(), null));
                storeInout.setEffectToTime(new Timestamp(DatetimeUtil.addDays(date, storeInout.getEffectDay()).getTime()));
            }
            String shopCodeDate = DatetimeUtil.getNow("yyyyMMdd");
            if (store == 1 && StringUtils.isEmpty(storeInout.getShopCode())) {
                String code = generateShopCode.generate(Constants.GENERATE_TYPE, 1);
                code = Constants.SHOP_CODE_MARK + code;
                LOG.info("code1=" + code);
                storeInout.setShopCode(code);
                storeInout.setShopCodeDate(shopCodeDate);
            }
            if (storeInout.getIncomeId() > 0) {
                storeInOutDao.updateStoreInOut(storeInout);
                //同步更新相同商品码的库存商品（商品名称，商品规格，规格数）
                storeInOutDao.updateStoreInOutSync(storeInout);
                // 删除对应商品码对应商品表数据
                storeInOutDao.deleteGoods(storeInout.getIncomeId());
            } else {
                // 查询相同商品条形码的商品是否是相同规格和规格数和商品名称
                Map<String, Object> checkMap = storeInOutDao.findOneStoreByGoodsCode(storeInout.getGoodsCode());
                if (null == warning && null != checkMap) {
                    String goodsName = String.valueOf(checkMap.get("goods_name"));// 商品名称
                    String goodsUnit = String.valueOf(checkMap.get("goods_unit"));// 商品单位
                    int unitCount = Integer.parseInt(String.valueOf(checkMap.get("unit_count")));// 单位数量
                    if (!goodsUnit.equals(storeInout.getGoodsUnit()) || !goodsName.equals(storeInout.getGoodsName()) || unitCount != storeInout.getUnitCount()) {
                        return packagMsg(ResultCode.THE_SAME_GOODS_WARING.getResp_code(), dataJson);
                    }
                }
                // 判断入库商品是否已匹配(入库商品是否已添加到商品表中)
                boolean flag = storeInOutDao.checkGoodsExist(storeInout.getGoodsCode());
                Integer isMatching = flag ? 1 : 0;
                if (store == 1) {
                    if (StringUtils.isEmpty(storeInout.getShopCode())) {
                        String code = generateShopCode.generate(Constants.GENERATE_TYPE, 1);
                        code = Constants.SHOP_CODE_MARK + code;
                        LOG.info("code2=" + code);
                        storeInout.setShopCode(code);
                        storeInout.setShopCodeDate(shopCodeDate);
                    }
                    storeInout.setIsMatching(isMatching.byteValue());
                    storeInout.setCreateTime(new Timestamp(new Date().getTime()));
                    storeInout.setLastUpdateTime(new Timestamp(new Date().getTime()));
                    storeInOutDao.saveStoreInOut(storeInout);
                } else {
                    // 设置商品店铺编号日期 202010302347381008
                    String code = generateShopCode.generate(Constants.GENERATE_TYPE, store);
                    String pefx = code.substring(0, code.length() - 4);
                    int endNum = Integer.parseInt(code.substring(code.length() - 4));
                    for (int i = 0; i < store; i++) {
                        TbStoreInout st = JSONObject.toJavaObject(data, TbStoreInout.class);
                        code = Constants.SHOP_CODE_MARK + pefx + endNum;
                        LOG.info("code3=" + code);
                        st.setShopCode(code);
                        st.setShopCodeDate(shopCodeDate);
                        st.setCreateTime(new Timestamp(new Date().getTime()));
                        st.setIsMatching(isMatching.byteValue());
                        st.setLastUpdateTime(new Timestamp(new Date().getTime()));
                        storeInOutDao.saveStoreInOut(st);
                        endNum++;
                    }
                }
            }
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
            TbStoreInout storeInout = JSONObject.toJavaObject(data, TbStoreInout.class);
            Integer store = data.getInteger("store");
            Integer endTimeType = data.getInteger("endTimeType");//0 结束日期1年2月3天
            Integer year = data.getInteger("year");
            Integer month = data.getInteger("month");
            Integer effectDay = data.getInteger("effectDay");
            if (null == store || store == 0) {
                return false;
            }
            if (null == endTimeType || (endTimeType != 0 && endTimeType != 1 && endTimeType != 2 && endTimeType != 3)) {
                return false;
            }
            if (storeInout.getEffectToTime() == null && year == null && month == null && effectDay == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOG.error("----check-error---", e);
        }
        return false;
    }
}