package com.stupidzhang.zhihu.come.config;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties({JingFenProperties.class, WxMpProperties.class})
public class AppConfiguration {

    @Autowired
    private JingFenProperties jingFenProperties;

    @Bean
    public JdClient jdClient() {
        return new DefaultJdClient(JdConstants.JD_API_URL, "", jingFenProperties.getJingFenConfig().getAppKey(), jingFenProperties.getJingFenConfig().getAppSecret(),
                30000, 90000);
    }

    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        return newRouter;
    }

}
