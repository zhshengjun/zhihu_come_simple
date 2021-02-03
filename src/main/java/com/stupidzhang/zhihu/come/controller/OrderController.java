package com.stupidzhang.zhihu.come.controller;


import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.service.WeiXinService;
import com.stupidzhang.zhihu.come.service.api.JingFenApiService;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author stupidzhang
 */
@RestController
public class OrderController {

    @Autowired
    private JingFenApiService jingFenApiService;
    @Autowired
    private JingFenProperties jingFenProperties;
    @Autowired
    private WeiXinService weiXinApiService;


    @GetMapping(value = "/api/send")
    public Object testSend(@RequestParam(value = "openId") String openId,
                           @RequestParam(value = "orderTimeStr", required = false) String orderTimeStr,
                           @RequestParam(value = "interval", required = false) Integer interval) {
        LocalDateTime orderTime;
        if (StringUtils.isNotBlank(orderTimeStr)) {
            orderTime = LocalDateTime.parse(orderTimeStr, DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));
        } else {
            orderTime = LocalDateTime.now().withNano(0);
        }
        if (interval == null) {
            interval = 10;
        }
        LocalDateTime startTime = orderTime.minusMinutes(interval);
        String endTimeStr = orderTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));
        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));
        try {
            List<WxMpTemplateData> wxMpTemplateData;
            wxMpTemplateData = jingFenApiService.queryOrderList(startTimeStr, endTimeStr);
            if (!CollectionUtils.isEmpty(wxMpTemplateData)) {
                // 发送消息
                weiXinApiService.sendMessage(openId, jingFenProperties.getOrderTemplateId(), wxMpTemplateData);
            }
        } catch (WxRuntimeException exception) {
            return exception.getMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "请检查微信消息";
    }
}
