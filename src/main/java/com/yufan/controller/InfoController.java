package com.yufan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yufan.common.bean.ReceiveJsonBean;
import com.yufan.common.service.IResultOut;
import com.yufan.common.service.ServiceFactory;
import com.yufan.utils.ResultCode;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static com.yufan.common.bean.ResponeUtil.packagMsg;

/**
 * 创建人: lirf
 * 创建时间:  2019/8/23 14:44
 * 功能介绍: 接口入口
 */
@Controller
@RequestMapping(value = "/info")
public class InfoController {

    private Logger LOG = Logger.getLogger(InfoController.class);

    /**
     * 库存系统
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "kc")
    public void sysKC(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        PrintWriter pw = null;
        String message = null;
        try {
            pw = response.getWriter();
            message = request.getParameter("message");

            if (null == message || "".equals(message)) {
                message = readStreamParameter(request.getInputStream());
            }
            LOG.info("接收参数:" + message);
            JSONObject obj = JSONObject.parseObject(message);
            if (obj != null) {
                ReceiveJsonBean jsonHeaderBean = JSON.toJavaObject(obj, ReceiveJsonBean.class);
                jsonHeaderBean.setRequest(request);
                jsonHeaderBean.setResponse(response);
                IResultOut resultOut = ServiceFactory.getService(jsonHeaderBean.getReq_type());
                //校验参数
                boolean flag = resultOut.checkParam(jsonHeaderBean);
                if (!flag) {
                    result = packagMsg(ResultCode.NEED_PARAM_ERROR.getResp_code(), new JSONObject());
                } else {
                    result = resultOut.getResult(jsonHeaderBean);
                }
            } else {
                result = packagMsg(ResultCode.PARAM_ERROR.getResp_code(), new JSONObject());
            }
            LOG.info("调用结果：" + result);
            pw.write(result);
            pw.flush();
            pw.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            result = packagMsg(ResultCode.PARAM_ERROR.getResp_code(), new JSONObject());
            pw.write(result);
            pw.flush();
            pw.close();
        }
    }


    /**
     * 从流中读取数据
     *
     * @param in
     * @return
     */
    public String readStreamParameter(ServletInputStream in) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }


}
