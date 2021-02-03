package com.stupidzhang.zhihu.come;

import com.jd.open.api.sdk.DefaultJdClient;
import com.stupidzhang.zhihu.come.service.api.JingFenApiService;
import com.stupidzhang.zhihu.come.service.api.JingFenAppService;
import com.stupidzhang.zhihu.come.service.WeiXinService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class WeixinApplicationTests {
    @Autowired
    private JingFenAppService jingFenAppService;


    @Autowired
    private WeiXinService weiXinApiService;


    @Test
    void contextLoads() {
        jingFenAppService.scheduledFromJdSummary(LocalDateTime.now().plusDays(-1), LocalDateTime.now());
    }

}
