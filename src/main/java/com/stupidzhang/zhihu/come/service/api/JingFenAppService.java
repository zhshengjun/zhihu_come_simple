package com.stupidzhang.zhihu.come.service.api;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.constant.WxMessageConstants;
import com.stupidzhang.zhihu.come.service.WeiXinService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 京粉APP请求接口
 */
@Slf4j
@Component
public class JingFenAppService {

    @Autowired
    private WeiXinService weiXinApiService;

    @Autowired
    private JingFenProperties jingFenProperties;

    /**
     * 去获取数据
     * fixme 这里需要获取jingfen APP的登陆数据，后续修改为登陆
     *
     * @param startTime
     * @param endTime
     */
    public void scheduledFromJdSummary(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> paramMap = packageCountParam(startTime, endTime);
        String httpResp;
        try {
            HttpRequest httpRequest = buildRequest(paramMap);
            httpResp = httpRequest.execute().body();
        } catch (Exception e) {
            log.error("请求京粉接口出错了呀!!!");
            return;
        }
        if (httpResp.contains("413")) {
            log.error("登陆信息失效了哦~");
            return;
        }
        JSONObject respJson = JSON.parseObject(httpResp);
        JSONObject queryResponse = respJson.getJSONObject(JdConstants.JD_RESPONSE_RESULT).getJSONObject("spreadReportInfoSum");
        log.info("准备发送微信消息.data :{}", queryResponse.toString());
        List<WxMpTemplateData> dataList = buildSummary(queryResponse);
        weiXinApiService.sendMessage("", jingFenProperties.getSummaryTemplateId(), dataList);
    }


    /**
     * 构建汇总消息实体
     *
     * @param jsonObject
     * @return 消息实体
     */
    private List<WxMpTemplateData> buildSummary(JSONObject jsonObject) {
        List<WxMpTemplateData> dataList = new ArrayList<>();
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_FIRST, jsonObject.getInteger("orderNum").toString(), "#173177"));
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_KEYWORD_ONE, jsonObject.getBigDecimal("cosFee").toString(), "#173177"));
        dataList.add(new WxMpTemplateData(WxMessageConstants.MESSAGE_KEYWORD_TWO, jsonObject.getInteger("clickNum").toString(), "#173177"));
        return dataList;
    }

    /**
     * 构建请求参数
     *
     * @param paramMap
     * @return
     */
    private HttpRequest buildRequest(Map<String, Object> paramMap) {
        HttpRequest httpRequest = HttpRequest.get(JdConstants.JD_API_M_URL);
        httpRequest.form(paramMap);
        HttpCookie httpCookie1 = new HttpCookie("pt_key", "");
        HttpCookie httpCookie2 = new HttpCookie("pt_pin", "zsjlovemm");
        httpRequest.cookie(httpCookie1, httpCookie2);
        httpRequest.header("Referer", "https://jingfenapp.jd.com/pages/commission");
        return httpRequest;
    }

    /**
     * 组装查询参数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    private Map<String, Object> packageCountParam(LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localTimeStart = df.format(startTime);
        String localTimeEnd = df.format(endTime);
        Map<String, Object> paramMap = new HashMap<>(8);
        Map<String, Object> bodyMap = new HashMap<>(8);
        Map<String, Object> queryParamMap = new HashMap<>(8);
        paramMap.put("functionId", "union_report");
        paramMap.put("client", "apple");
        paramMap.put("clientVersion", "3.9.7");
        paramMap.put("appid", "u_jfapp");
        bodyMap.put("funName", "querySpreadEffectData");
        queryParamMap.put("startDate", localTimeStart);
        queryParamMap.put("endDate", localTimeEnd);
        queryParamMap.put("orderType", 0);
        queryParamMap.put("showDetails", false);
        bodyMap.put("param", queryParamMap);
        paramMap.put("body", JSON.toJSONString(bodyMap));
        return paramMap;
    }
}
