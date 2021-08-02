package com.yufan.kc.dao.param;

import com.yufan.common.bean.ConditionCommon;
import com.yufan.kc.pojo.TbParam;
import com.yufan.kc.pojo.TbSequence;
import com.yufan.utils.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 创建人: lirf
 * 创建时间:  2020/10/3 14:34
 * 功能介绍:
 */
public interface ParamDao {


    public PageInfo loadParamPage(ConditionCommon conditionCommon);

    public void saveParam(TbParam param);

    public void deleteParam(int id);

    public void updateParam(TbParam param);

    public List<Map<String, Object>> listParamCode(ConditionCommon conditionCommon);

    public void updateGenerateValue(int sequenceType, int count);

    public void updateSetGenerateValue(int sequenceType, int sequenceValue);

    public TbSequence generateValue(int sequenceType);

    public void resetGenerateValue(int sequenceType,int minValue);

}
