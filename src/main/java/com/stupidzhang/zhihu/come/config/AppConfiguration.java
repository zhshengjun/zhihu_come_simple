package com.stupidzhang.zhihu.come.config;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.service.api.JingFenApiClient;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties({JingFenProperties.class, WxMpProperties.class})
public class AppConfiguration {

    private final JingFenProperties properties;


    @Bean
    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        return newRouter;
    }


    @Bean
    public List<JingFenApiClient> clients() {
        // 代码里 getConfigs()处报错的同学，请注意仔细阅读项目说明，你的IDE需要引入lombok插件！！！！
        List<JingFenProperties.JingFenConfig> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("大哥，拜托先看下项目首页的说明（readme文件），添加下相关配置，注意别配错了！");
        }
        return configs
                .stream().map(a -> new JingFenApiClient(new DefaultJdClient(JdConstants.JD_API_URL, "", a.getAppKey(), a.getAppSecret(),
                        30000, 90000), a.getToUser())).collect(Collectors.toList());
    }


    @Bean
    public JdClient jdClient() {
        return new DefaultJdClient(JdConstants.JD_API_URL, "", properties.getConfigs().get(0).getAppKey(), properties.getConfigs().get(0).getAppSecret(),
                30000, 90000);
    }

}
