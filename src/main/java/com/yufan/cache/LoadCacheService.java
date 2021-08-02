package com.yufan.cache;

import com.yufan.common.dao.param.IParamCodeJpaDao;
import com.yufan.kc.dao.goods.GoodsDao;
import com.yufan.kc.dao.store.StoreInOutDao;
import com.yufan.kc.pojo.TbKcGoods;
import com.yufan.kc.pojo.TbParam;
import com.yufan.kc.pojo.TbStoreInout;
import com.yufan.utils.CacheData;
import com.yufan.utils.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 创建人: lirf
 * 创建时间:  2019/3/26 16:47
 * 功能介绍:
 */
@Component
public class LoadCacheService implements InitializingBean {

    private Logger LOG = Logger.getLogger(LoadCacheService.class);

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private StoreInOutDao storeInOutDao;

    ScheduledExecutorService fixedThreadPool = Executors.newScheduledThreadPool(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        fixedThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                initGoodsList();
                initStoreInOut();
                clearOutTimeCacheData();
            }
        }, 0, 20, TimeUnit.MINUTES);
    }

    /**
     * 初始化文件本地保存路径和web访问路径
     */
//    private void initGoodsList() {
//        try {
//            LOG.info("-----开始初始化商品----");
//            List<TbKcGoods> list = goodsDao.loadKcGoodsList();
//            CacheData.goodsMap = new HashMap<>();
//            for (int i = 0; i < list.size(); i++) {
//                CacheData.goodsMap.put(list.get(i).getGoodsCode(), list.get(i));
//            }
//            LOG.info("-----开始初始化商品----");
//        } catch (Exception e) {
//            LOG.error("初始化商品异常", e);
//        }
//    }

    private void initStoreInOut() {
        try {
            List<TbStoreInout> list = storeInOutDao.loadStoreInout();
            CacheData.shopGoodsCodeMap = new HashMap<>();
            LOG.info("-----开始初始化商品出入库----");
            for (int i = 0; i < list.size(); i++) {
                CacheData.shopGoodsCodeMap.put(list.get(i).getShopCode(), list.get(i).getGoodsCode());
            }
            LOG.info("-----结束初始化商品出入库----");
        } catch (Exception e) {
            LOG.error("初始化商品出入库异常", e);
        }
    }


    private void clearOutTimeCacheData() {
        try {
            Long now = new Date().getTime();
            for (Map.Entry<String, Long> map : CacheData.preOrderNoMapOutTimeMap.entrySet()) {
                String uniqueKey = map.getKey();
                Long value = map.getValue();
                if (value.longValue() < now.longValue()) {
                    CacheData.preOrderNoMap.remove(uniqueKey);
                    CacheData.preOrderNoMapOutTimeMap.remove(uniqueKey);
                }
            }
        } catch (Exception e) {
            LOG.error("clearOutTimeCacheData异常", e);
        }
    }

}
