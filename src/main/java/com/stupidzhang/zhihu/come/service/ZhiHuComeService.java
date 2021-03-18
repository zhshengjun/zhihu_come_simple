package com.stupidzhang.zhihu.come.service;

import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.service.api.JingFenApiClient;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.springframework.beans.factory.annotation.Autowired;
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
