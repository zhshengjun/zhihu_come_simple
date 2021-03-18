package com.stupidzhang.zhihu.come.service;

import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.service.api.JingFenApiClient;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class ZhiHuComeService {

    @Autowired
    private JingFenProperties jingFenProperties;
    @Autowired
    private WeiXinService weiXinApiService;

    @Autowired
    private List<JingFenApiClient> jingFenApiClients;

    @Async
    @Retryable(value = WxRuntimeException.class, backoff = @Backoff(delay = 2000L, multiplier = 1.5))
    public void queryAndSendMessage(JingFenApiClient client, String startTime, String endTime) {
        List<WxMpTemplateData> wxMpTemplateData = client.execute(startTime, endTime);
        if (!CollectionUtils.isEmpty(wxMpTemplateData)) {
            // 发送消息
            weiXinApiService.sendMessage(client.getToUser(), jingFenProperties.getOrderTemplateId(), wxMpTemplateData);
        }
    }

    @Async
    public void queryAndSendMessage(String startTime, String endTime) {
        for (JingFenApiClient jingFenApiClient : jingFenApiClients) {
            List<WxMpTemplateData> wxMpTemplateData = jingFenApiClient.execute(startTime, endTime);
            if (!CollectionUtils.isEmpty(wxMpTemplateData)) {
                // 发送消息
                weiXinApiService.sendMessage(jingFenApiClient.getToUser(), jingFenProperties.getOrderTemplateId(), wxMpTemplateData);
            }
        }
    }
}
