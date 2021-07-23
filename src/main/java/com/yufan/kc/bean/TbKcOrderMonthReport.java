package com.yufan.kc.bean;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * @description:
 * @author: lirf
 * @time: 2021/7/23
 */
@Entity
@Table(name = "tb_kc_order_month_report", schema = "store_kc_db", catalog = "")
public class TbKcOrderMonthReport {
    private int id;
    private int saleReport;
    private BigDecimal orderPriceAll;
    private Timestamp updateTime;
    private BigDecimal goodsInpriceAll;
    private String saleMonth;
    private String saleYear;
    private Byte saleSeason;

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
    @Column(name = "sale_report", nullable = true)
    public int getSaleReport() {
        return saleReport;
    }

    public void setSaleReport(int saleReport) {
        this.saleReport = saleReport;
    }

    @Basic
    @Column(name = "order_price_all", nullable = true, precision = 2)
    public BigDecimal getOrderPriceAll() {
        return orderPriceAll;
    }

    public void setOrderPriceAll(BigDecimal orderPriceAll) {
        this.orderPriceAll = orderPriceAll;
    }

    @Basic
    @Column(name = "update_time", nullable = true)
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "goods_inprice_all", nullable = true, precision = 2)
    public BigDecimal getGoodsInpriceAll() {
        return goodsInpriceAll;
    }

    public void setGoodsInpriceAll(BigDecimal goodsInpriceAll) {
        this.goodsInpriceAll = goodsInpriceAll;
    }

    @Basic
    @Column(name = "sale_month", nullable = true, length = 2)
    public String getSaleMonth() {
        return saleMonth;
    }

    public void setSaleMonth(String saleMonth) {
        this.saleMonth = saleMonth;
    }

    @Basic
    @Column(name = "sale_year", nullable = true, length = 4)
    public String getSaleYear() {
        return saleYear;
    }

    public void setSaleYear(String saleYear) {
        this.saleYear = saleYear;
    }

    @Basic
    @Column(name = "sale_season", nullable = true)
    public Byte getSaleSeason() {
        return saleSeason;
    }

    public void setSaleSeason(Byte saleSeason) {
        this.saleSeason = saleSeason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbKcOrderMonthReport that = (TbKcOrderMonthReport) o;
        return id == that.id &&
                Objects.equals(saleReport, that.saleReport) &&
                Objects.equals(orderPriceAll, that.orderPriceAll) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(goodsInpriceAll, that.goodsInpriceAll) &&
                Objects.equals(saleMonth, that.saleMonth) &&
                Objects.equals(saleYear, that.saleYear) &&
                Objects.equals(saleSeason, that.saleSeason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, saleReport, orderPriceAll, updateTime, goodsInpriceAll, saleMonth, saleYear, saleSeason);
    }
}
