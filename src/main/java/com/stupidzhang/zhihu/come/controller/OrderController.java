package com.stupidzhang.zhihu.come.controller;


import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.service.ZhiHuComeService;
import me.chanjar.weixin.common.error.WxRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author stupidzhang
 */
@RestController
public class OrderController {

    @Autowired
    private ZhiHuComeService zhiHuComeService;


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
            zhiHuComeService.queryAndSendMessage(startTimeStr, endTimeStr);
        } catch (WxRuntimeException exception) {
            return exception.getMessage();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "请检查微信消息";
    }
}
