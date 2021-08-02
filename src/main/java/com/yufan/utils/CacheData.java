package com.yufan.utils;

import com.yufan.kc.pojo.TbKcGoods;
import com.yufan.kc.pojo.TbParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建人: lirf
 * 创建时间:  2019/3/26 16:46
 * 功能介绍:  缓存数据
 */
public class CacheData {


    /**
     * 商品信息
     */
//    public static HashMap<String, TbKcGoods> goodsMap = new HashMap<>();


    /**
     * 店铺码-商品码
     */
    public static HashMap<String, String> shopGoodsCodeMap = new HashMap<>();

    /**
     * 缓存预订单号
     * key = uniqueKey   value = orderNo
     */
    public static Map<String, String> preOrderNoMap = new HashMap<>();

    /**
     * 缓存预订单号
     * key = orderNo   value = 过期时间
     */
    public static HashMap<String, Long> preOrderNoMapOutTimeMap = new HashMap<>();

}
