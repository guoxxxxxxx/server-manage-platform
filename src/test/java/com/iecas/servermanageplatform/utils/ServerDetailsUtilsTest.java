package com.iecas.servermanageplatform.utils;

import com.iecas.servermanageplatform.utils.serverDetails.ServerDetailsUtils;
import com.iecas.servermanageplatform.utils.serverDetails.UbuntuServerDetailsUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 10:13
 * @Description:
 */


public class ServerDetailsUtilsTest {

    @Test
    public void test(){
        ServerDetailsUtils serverDetailsUtils = new UbuntuServerDetailsUtils();
    }


    @Test
    public void getCPUInfoTest(){
        ServerDetailsUtils serverDetailsUtils = new UbuntuServerDetailsUtils();
        serverDetailsUtils.connect("192.168.247.149", 22, "guox", "123456");
        System.out.println(serverDetailsUtils.getCPUInfo());
    }


    @Test
    public void getOSInfoTest(){
        ServerDetailsUtils serverDetailsUtils = new UbuntuServerDetailsUtils();
        serverDetailsUtils.connect("192.168.247.149", 22, "guox", "123456");
        String os = serverDetailsUtils.getOS();
        System.out.println(os);
    }


    @Test
    void getMemInfoTest(){
        ServerDetailsUtils serverDetailsUtils = new UbuntuServerDetailsUtils();
        serverDetailsUtils.connect("192.168.247.149", 22, "guox", "123456");
        Pair<Long, Long> memInfo = serverDetailsUtils.getMemInfo();
        Long first = memInfo.getFirst();
        Long second = memInfo.getSecond();
        System.out.println("first = " + first + " second = " + second);
    }


    @Test
    void getDiskInfoTest(){
        ServerDetailsUtils serverDetailsUtils = new UbuntuServerDetailsUtils();
        serverDetailsUtils.connect("192.168.247.149", 22, "guox", "123456");
        Pair<Long, Long> diskInfo = serverDetailsUtils.getDiskInfo();
        System.out.println(diskInfo);
    }
}
