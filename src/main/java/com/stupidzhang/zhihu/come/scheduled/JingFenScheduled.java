package com.stupidzhang.zhihu.come.scheduled;

import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.service.WeiXinService;
import com.stupidzhang.zhihu.come.service.api.JingFenApiService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author zhangshengjun
 */
@Slf4j
@Component
public class JingFenScheduled {


    @Autowired
    private JingFenApiService jingFenApiService;
    @Autowired
    private JingFenProperties jingFenProperties;
    @Autowired
    private WeiXinService weiXinApiService;


    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduledQueryOrderList() {
        LocalDateTime endTime = LocalDateTime.now().withNano(0);
        LocalDateTime startTime = endTime.minusMinutes(10);
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));
        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));

        if (Boolean.TRUE.equals(jingFenProperties.getLocal())) {
            extracted(startTimeStr, endTimeStr);
        }
    }

    @Async
    public void extracted(String startTimeStr, String endTimeStr) {
        try {
            List<WxMpTemplateData> wxMpTemplateData = jingFenApiService.queryOrderList(startTimeStr, endTimeStr);
            if (!CollectionUtils.isEmpty(wxMpTemplateData)) {
                // 发送消息
                weiXinApiService.sendMessage(jingFenProperties.getJingFenConfig().getToUser(), jingFenProperties.getOrderTemplateId(), wxMpTemplateData);
            }
        } catch (WxRuntimeException e) {
            log.error("查询订单错误：{}", e.getMessage());
        }
    }
}
