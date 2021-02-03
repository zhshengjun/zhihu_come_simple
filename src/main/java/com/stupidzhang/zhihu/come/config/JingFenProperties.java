package com.stupidzhang.zhihu.come.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jing.fen")
public class JingFenProperties {


    private Boolean local;

    /**
     * 订单消息模板配置
     */
    private String orderTemplateId;

    /**
     * 日报消息模板配置
     */
    private String summaryTemplateId;


    /**
     * 日报消息模板配置
     */
    private JingFenConfig jingFenConfig;

    @Data
    public static class JingFenConfig {

        private String appKey;
        private String appSecret;

        private String toUser;
    }


}
