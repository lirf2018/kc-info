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
@Table(name = "tb_goods_sale_month_report", schema = "store_kc_db", catalog = "")
public class TbGoodsSaleMonthReport {
    private int id;
    private Integer saleReport;
    private String goodsCode;
    private String goodsName;
    private BigDecimal salePriceAll;
    private Integer saleCount;
    private BigDecimal incomePriceAll;
    private Integer incomeCount;
    private Timestamp updateTime;
    private String saleMonth;
    private String saleYear;
    private Byte saleSeason;
    private String goodsUnitName;
    private Integer unitCount;

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
    public Integer getSaleReport() {
        return saleReport;
    }

    public void setSaleReport(Integer saleReport) {
        this.saleReport = saleReport;
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
    @Column(name = "goods_name", nullable = true, length = 50)
    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @Basic
    @Column(name = "sale_price_all", nullable = true, precision = 2)
    public BigDecimal getSalePriceAll() {
        return salePriceAll;
    }

    public void setSalePriceAll(BigDecimal salePriceAll) {
        this.salePriceAll = salePriceAll;
    }

    @Basic
    @Column(name = "sale_count", nullable = true)
    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    @Basic
    @Column(name = "income_price_all", nullable = true, precision = 2)
    public BigDecimal getIncomePriceAll() {
        return incomePriceAll;
    }

    public void setIncomePriceAll(BigDecimal incomePriceAll) {
        this.incomePriceAll = incomePriceAll;
    }

    @Basic
    @Column(name = "income_count", nullable = true)
    public Integer getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(Integer incomeCount) {
        this.incomeCount = incomeCount;
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

    @Basic
    @Column(name = "goods_unit_name", nullable = true, length = 10)
    public String getGoodsUnitName() {
        return goodsUnitName;
    }

    public void setGoodsUnitName(String goodsUnitName) {
        this.goodsUnitName = goodsUnitName;
    }

    @Basic
    @Column(name = "unit_count", nullable = true)
    public Integer getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(Integer unitCount) {
        this.unitCount = unitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbGoodsSaleMonthReport report = (TbGoodsSaleMonthReport) o;
        return id == report.id &&
                Objects.equals(saleReport, report.saleReport) &&
                Objects.equals(goodsCode, report.goodsCode) &&
                Objects.equals(goodsName, report.goodsName) &&
                Objects.equals(salePriceAll, report.salePriceAll) &&
                Objects.equals(saleCount, report.saleCount) &&
                Objects.equals(incomePriceAll, report.incomePriceAll) &&
                Objects.equals(incomeCount, report.incomeCount) &&
                Objects.equals(updateTime, report.updateTime) &&
                Objects.equals(saleMonth, report.saleMonth) &&
                Objects.equals(saleYear, report.saleYear) &&
                Objects.equals(saleSeason, report.saleSeason) &&
                Objects.equals(goodsUnitName, report.goodsUnitName) &&
                Objects.equals(unitCount, report.unitCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, saleReport, goodsCode, goodsName, salePriceAll, saleCount, incomePriceAll, incomeCount, updateTime, saleMonth, saleYear, saleSeason, goodsUnitName, unitCount);
    }
}
