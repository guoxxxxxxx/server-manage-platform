package com.iecas.servermanageplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.iecas.servermanageplatform.dao")
public class ServerManagerPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerManagerPlatformApplication.class, args);
    }

}
