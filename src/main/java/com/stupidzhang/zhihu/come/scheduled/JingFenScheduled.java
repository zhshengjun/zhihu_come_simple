package com.stupidzhang.zhihu.come.scheduled;

import com.stupidzhang.zhihu.come.config.JingFenProperties;
import com.stupidzhang.zhihu.come.constant.JdConstants;
import com.stupidzhang.zhihu.come.service.ZhiHuComeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private JingFenProperties jingFenProperties;
    @Autowired
    private List<ZhiHuComeService> zhiHuComeServices;


    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduledQueryOrderList() {
        LocalDateTime endTime = LocalDateTime.now().withNano(0);
        LocalDateTime startTime = endTime.minusMinutes(10);
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));
        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern(JdConstants.DATE_TIME_FORMAT));

        if (Boolean.TRUE.equals(jingFenProperties.getLocal())) {
            for (ZhiHuComeService zhiHuComeService : zhiHuComeServices) {
                zhiHuComeService.queryAndSendMessage(endTimeStr, startTimeStr);
            }

        }
    }
}
