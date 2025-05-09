package com.iecas.servermanageplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerManagerPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerManagerPlatformApplication.class, args);
    }

}
