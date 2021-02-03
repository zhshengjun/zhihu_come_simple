package com.stupidzhang.zhihu.come;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@EnableAsync
@EnableRetry
@SpringBootApplication
public class ZhiHuComeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiHuComeApplication.class, args);
    }

}
