package com.yufan.kc.service.impl.order;

import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.kc.dao.order.KcOrderDao;
import com.yufan.kc.pojo.TbStoreInout;
import com.yufan.utils.*;
import com.yufan.common.service.IResultOut;
import com.yufan.kc.pojo.TbKcGoods;
import com.yufan.kc.pojo.TbKcOrder;
import com.yufan.kc.pojo.TbKcOrderDetail;
import com.yufan.kc.dao.goods.GoodsDao;
import com.yufan.kc.dao.order.OpenOrderDao;
import com.yufan.kc.dao.store.StoreInOutDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * 创建人: lirf
 * 创建时间:  2020/11/15 16:00
 * 功能介绍:
 */
@Service("kc_open_order_add")
public class AddOpenOrder implements IResultOut {

    private Logger LOG = Logger.getLogger(FindOpenOrder.class);

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private OpenOrderDao openOrderDao;

    @Autowired
    private StoreInOutDao storeInOutDao;

    @Autowired
    private KcOrderDao kcOrderDao;

    @Override
    public String getResult(ReceiveJsonBean receiveJsonBean) {
        JSONObject dataJson = new JSONObject();
        JSONObject data = receiveJsonBean.getData();
        try {
            Integer goodsId = data.getInteger("goods_id");// 商品id
            String goodsCode = data.getString("goods_code");// 商品条形码
            String orderNo = data.getString("order_no");
            // 判断是否预生成订单号成功
            if (CacheData.preOrderNoMap.get(orderNo) == null) {
                return packagMsg(ResultCode.ORDERNO_OUT_TIME.getResp_code(), dataJson);
            }
            //
            TbKcGoods goods = null;
            if (goodsId != null) {
                goods = goodsDao.loadGoods(goodsId);
            } else {
                goods = goodsDao.loadGoods(goodsCode);
                if (goods == null) {
                    // 通过店铺码查询
                    String goodsCode_ = CacheData.shopGoodsCodeMap.get(goodsCode);
                    if (StringUtils.isEmpty(goodsCode_)) {
                        LOG.info("--------缓存查询goodsCode不存在--直接查询数按库----");
                        TbStoreInout storeInout = storeInOutDao.loadStoreInoutByShopCode(goodsCode);
                        if (null == storeInout) {
                            LOG.info("--------查询不存在------shopCode:" + goodsCode);
                            return packagMsg(ResultCode.GOODS_NOT_SALE.getResp_code(), dataJson);
                        }
                        goodsCode_ = storeInout.getGoodsCode();
                    }
                    goods = goodsDao.loadGoods(goodsCode_);
                    //
                    data.put("shop_no", goodsCode);
                }
            }
            if (null == goods || goods.getStatus().intValue() != 1) {
                LOG.info("-----------商品不存在或者已下架----goodsId=" + goodsId);
                return packagMsg(ResultCode.GOODS_NOT_SALE.getResp_code(), dataJson);
            }
            // 生成订单
            return createOrder(data, goods);
        } catch (Exception e) {
            LOG.error("-------error----", e);
        }
        return packagMsg(ResultCode.FAIL.getResp_code(), dataJson);
    }

    /**
     * 促销折扣总价 = 订单总价格-促销总价
     * 会员折扣总价 = 订单总价格-会员总价
     * 促销折扣总价 = 订单总价格-促销总价
     */
    public synchronized String createOrder(JSONObject data, TbKcGoods goods) {
        LOG.info("-------createOrder------");
        JSONObject dataJson = new JSONObject();
        // 校验订单
        String orderNo = data.getString("order_no");
        TbKcOrder order = kcOrderDao.loadOrder(orderNo.trim());
        //
        String shopNo = data.getString("shop_no");
        // 判断促销是否有过期
        int isDiscounts = goods.getIsDiscounts();
        if (isDiscounts == 1) {
            try {
                String format = "yyyy-MM-dd";
                String stdate = DatetimeUtil.timeStamp2Date(goods.getDiscountsStartTime().getTime(), format);
                String etdate = DatetimeUtil.timeStamp2Date(goods.getDiscountsEndTime().getTime(), format);
                String now = DatetimeUtil.getNow(format);
                LOG.info("-----------stdate=" + stdate + "        etime=" + etdate + "          now=" + now);
                if (!(DatetimeUtil.compareDate(now, stdate) >= 0 && DatetimeUtil.compareDate(now, etdate) <= 0)) {
                    LOG.error("----------促销已过期--------");
                    goods.setIsDiscounts((byte) 1);
                    goods.setDiscountsPrice(goods.getSalePrice());
                }
            } catch (Exception e) {
                LOG.error("----------格式化异常--------");
                goods.setIsDiscounts((byte) 1);
                goods.setDiscountsPrice(goods.getSalePrice());
            }
        }
        String userPhone = data.getString("user_phone");//手机号(先不做会员)
        String goodsUnitName = data.getString("goods_unit_name");
        String discountsRemark = data.getString("discounts_remark");
        //
        String serverName = data.getString("server_name");
        Integer personCount = data.getInteger("person_count");
        String tableName = data.getString("table_name");
        //
        Timestamp dateTime = new Timestamp(new Date().getTime());
        BigDecimal discountsTicketPrice = BigDecimal.ZERO;// 优惠券金额暂时不做
        BigDecimal salePriceTrue = getSalePriceTrue(goods.getIsDiscounts(), goods.getSalePrice(), goods.getMemberPrice(), goods.getDiscountsPrice(), userPhone);
        // 订单详情
        // 详情
        TbKcOrderDetail detail = new TbKcOrderDetail();
        detail.setGoodsId(goods.getGoodsId());
        detail.setBuyCount(1);
        detail.setGoodsCode(goods.getGoodsCode());
        detail.setShopCode(shopNo);
        detail.setGoodsName(goods.getGoodsName());
        detail.setSalePrice(goods.getSalePrice());
        detail.setSalePriceTrue(salePriceTrue);
        detail.setMemberPrice(goods.getMemberPrice());
        detail.setDiscountsPrice(goods.getDiscountsPrice());
        detail.setDiscountsStartTime(goods.getDiscountsStartTime());
        detail.setDiscountsEndTime(goods.getDiscountsEndTime());
        detail.setIsDiscounts(goods.getIsDiscounts());
        detail.setUnitCount(goods.getUnitCount());
        detail.setCreateTime(dateTime);
        detail.setLastUpdateTime(dateTime);
        detail.setGoodsUnitName(goodsUnitName);
        detail.setStatus(1);

        if (order == null) {
            // 生成新订单
            order = new TbKcOrder();
            BigDecimal orderPrice = BigDecimal.ZERO;
            BigDecimal realPrice = BigDecimal.ZERO;
            BigDecimal discountsMemberPrice = BigDecimal.ZERO;
            BigDecimal discountsPrice = BigDecimal.ZERO;
            orderPrice = goods.getSalePrice();
            realPrice = salePriceTrue.subtract(discountsTicketPrice);

            if (StringUtils.isNotEmpty(userPhone)) {
                // 是会员,计算会员折扣
                discountsMemberPrice = orderPrice.subtract(goods.getMemberPrice());
            }
            discountsPrice = orderPrice.subtract(goods.getDiscountsPrice());
            order.setOrderNum(orderNo);
            order.setUserId(0);
            order.setGoodsCount(1);
            order.setOrderPrice(orderPrice);
            order.setRealPrice(realPrice);
            order.setDiscountsTicketPrice(discountsTicketPrice);
            order.setDiscountsMemberPrice(discountsMemberPrice);
            order.setDiscountsPrice(discountsPrice);
            order.setDiscountsRemark(discountsRemark);
            order.setPayMethod(null);
            order.setOrderStatus(new Byte(String.valueOf(Constants.ORDER_STATUS_0)));
            order.setCreateTime(dateTime);
            order.setLastUpdateTime(dateTime);
            order.setOrderSource(new Integer(1).byteValue());
            order.setMemberNo("");
            order.setUserPhone(userPhone);
            order.setRemark("");
            order.setServerName(serverName);
            order.setPersonCount(personCount);
            order.setTableName(tableName);
            order.setRealInpayPrice(BigDecimal.ZERO);
            openOrderDao.saveObj(order);
            detail.setOrderId(order.getOrderId());
            openOrderDao.saveObj(detail);
        } else {
            //订单已存在
            // 判断订单是否已支付
            if (order.getOrderStatus().intValue() != Constants.ORDER_STATUS_0) {
                LOG.info("-------查询订单已付款--------");
                return packagMsg(ResultCode.ORDER_IS_PAY.getResp_code(), dataJson);
            }

            // 检验所有订单商品的店铺编码是否已存在(保证一个出库)
//            if (StringUtils.isNotEmpty(shopNo) && shopNo.length() != 13) {
//                boolean flag = storeInOutDao.checkShopNoOut(shopNo);
//                if (flag) {
//                    // 订单详情中商品店铺编码已存在
//                    LOG.info("-------订单详情中商品店铺编码已存在----shopNo=" + shopNo);
//                    return packagMsg(ResultCode.ORDER_SHOPCODE_EXIST.getResp_code(), dataJson);
//                }
//            }
            updateOrder(orderNo, userPhone, discountsRemark, dateTime, discountsTicketPrice, detail, order);
        }
        dataJson.put("order_no", orderNo);
        dataJson.put("order_id", order.getOrderId());
        return packagMsg(ResultCode.OK.getResp_code(), dataJson);
    }

    private void updateOrder(String orderNo, String userPhone, String discountsRemark, Timestamp dateTime, BigDecimal discountsTicketPrice, TbKcOrderDetail detail, TbKcOrder order) {
        // 计算订单价格
        Integer goodsCount = order.getGoodsCount() + 1;
        BigDecimal orderPriceAll = BigDecimal.ZERO;//订单总金额
        BigDecimal realPriceAll = BigDecimal.ZERO;//订单实付总金额
        //
        BigDecimal memberPriceAll = BigDecimal.ZERO;//会员价格总金额
        BigDecimal discountsPriceAll = BigDecimal.ZERO;//促销价格总金额


        List<TbKcOrderDetail> updateDetailList = new ArrayList<>();//如果用户是会员,则整个订单重新计算实付金额单价
        // 订单已添加的商品数(重新计算实付单价)
        List<TbKcOrderDetail> detailList = openOrderDao.loadOrderDetailList(order.getOrderId());
        detailList.add(detail);
        for (int i = 0; i < detailList.size(); i++) {
            TbKcOrderDetail de = detailList.get(i);
            // 实付单价
            BigDecimal trueGoodsSalePrice = getSalePriceTrue(de.getIsDiscounts(), de.getSalePrice(), de.getMemberPrice(), de.getDiscountsPrice(), userPhone);
            de.setSalePriceTrue(trueGoodsSalePrice);
            orderPriceAll = orderPriceAll.add(de.getSalePrice());
            realPriceAll = realPriceAll.add(trueGoodsSalePrice);
            memberPriceAll = memberPriceAll.add(de.getMemberPrice());
            discountsPriceAll = discountsPriceAll.add(de.getDiscountsPrice());
            updateDetailList.add(de);
        }
        order.setGoodsCount(goodsCount);
        order.setOrderPrice(orderPriceAll);
        order.setRealPrice(realPriceAll);
        order.setDiscountsRemark(discountsRemark);

        //订单优惠券优惠价格
        order.setDiscountsTicketPrice(discountsTicketPrice);
        //订单促销优惠价格
        order.setDiscountsPrice(orderPriceAll.subtract(discountsPriceAll));
        //会员优惠总金额
        BigDecimal discountsMemberPriceAll = BigDecimal.ZERO;
        if (StringUtils.isNotEmpty(userPhone)) {
            // 是会员
            discountsMemberPriceAll = orderPriceAll.subtract(memberPriceAll);
        }
        order.setDiscountsMemberPrice(discountsMemberPriceAll);//订单会员优惠价格

        order.setLastUpdateTime(dateTime);
        openOrderDao.updateObj(order);
        // 重新计算实付单价
        for (int i = 0; i < updateDetailList.size(); i++) {
            TbKcOrderDetail de = updateDetailList.get(i);
            de.setOrderId(order.getOrderId());
            if (de.getDetailId() <= 0) {
                openOrderDao.saveObj(de);
            } else {
                openOrderDao.updateObj(de);
            }
        }
    }

    public static BigDecimal getSalePriceTrue(int isDiscounts, BigDecimal salePrice, BigDecimal memberPrice, BigDecimal discountsPrice, String memberNum) {
        BigDecimal salePriceTrue = salePrice;// 计算价格
        if (isDiscounts == 1) {
            // 促销
            salePriceTrue = discountsPrice;
        }
        if (StringUtils.isNotEmpty(memberNum)) {
            // 是会员
            if (memberPrice.compareTo(discountsPrice) < 0) {
                salePriceTrue = memberPrice;
            }
        }
        return salePriceTrue;
    }

    @Override
    public boolean checkParam(ReceiveJsonBean receiveJsonBean) {
        JSONObject data = receiveJsonBean.getData();
        try {
            Integer goodsId = data.getInteger("goods_id");
            String goodsCode = data.getString("goods_code");// 商品条形码
            String orderNo = data.getString("order_no");
            if ((goodsId == null && StringUtils.isEmpty(goodsCode)) || StringUtils.isEmpty(orderNo)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOG.error("----check-error---", e);
        }
        return false;
    }
}