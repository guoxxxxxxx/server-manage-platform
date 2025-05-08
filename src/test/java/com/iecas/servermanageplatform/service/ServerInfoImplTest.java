package com.iecas.servermanageplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @Author: guo_x
 * @Date: 2025/5/7 16:43
 * @Description:
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerInfoImplTest {


    @Autowired
    ServerInfoService serverInfoService;


    @Test
    void updateHardwareInfoTest(){
        System.out.println("123");
        boolean b = serverInfoService.updateHardwareInfo(6L);
        System.out.println(b);
    }
}
