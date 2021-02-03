package com.stupidzhang.zhihu.come.service;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WeiXinService {


    @Autowired
    private WxMpService wxMpService;

    /**
     * 给微信发送消息
     *
     * @param templateDataList 实体
     * @param templateId       模板ID
     */
    @Async
    public void sendMessage(String openId, String templateId, List<WxMpTemplateData> templateDataList) {
        try {
            WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                    .toUser(openId).templateId(templateId).data(templateDataList).build();
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("发送微信消息失败，exception：{}", exception.getMessage());
        }
    }

}
