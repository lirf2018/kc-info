package com.yufan.kc.pojo;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @description:
 * @author: lirf
 * @time: 2021/7/24
 */
@Entity
@Table(name = "tb_kc_goods_store", schema = "store_kc_db", catalog = "")
public class TbKcGoodsStore {
    private int id;
    private String goodsCode;
    private Integer store;
    private Integer storeSale;
    private Timestamp lastUpdateTime;

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "goods_code", nullable = true, length = 50)
    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    @Basic
    @Column(name = "store", nullable = true)
    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    @Basic
    @Column(name = "store_sale", nullable = true)
    public Integer getStoreSale() {
        return storeSale;
    }

    public void setStoreSale(Integer storeSale) {
        this.storeSale = storeSale;
    }

    @Basic
    @Column(name = "last_update_time", nullable = true)
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbKcGoodsStore that = (TbKcGoodsStore) o;
        return id == that.id &&
                Objects.equals(goodsCode, that.goodsCode) &&
                Objects.equals(store, that.store) &&
                Objects.equals(storeSale, that.storeSale) &&
                Objects.equals(lastUpdateTime, that.lastUpdateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, goodsCode, store, storeSale, lastUpdateTime);
    }
}
