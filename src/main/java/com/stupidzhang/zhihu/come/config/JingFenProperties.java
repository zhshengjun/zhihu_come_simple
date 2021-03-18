package com.stupidzhang.zhihu.come.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
    private List<JingFenConfig> configs;

    @Data
    public static class JingFenConfig {

        private String appKey;
        private String appSecret;

        private String toUser;
    }


}
