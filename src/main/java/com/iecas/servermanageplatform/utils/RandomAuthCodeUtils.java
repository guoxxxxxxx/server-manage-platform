/**
 * @Time: 2024/8/30 16:47
 * @Author: guoxun
 * @File: RandomAuthCodeUtil
 * @Description: 随机生成验证码工具类
 */

package com.iecas.servermanageplatform.utils;

import java.util.Random;

public class RandomAuthCodeUtils {


    /**
     * 随机生成指定长度的验证码
     * @param length 长度
     * @return 长度为length的验证码
     */
    public static String getRandomAuthCode(Integer length){
        Random random=new Random();
        char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9'};
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < length; i++){
            int index=random.nextInt(chars.length);
            code.append(chars[index]);
        }
        return code.toString();
    }


    /**
     * 生成默认长度为4的验证码
     * @return 长度为4的验证码
     */
    public static String getRandomAuthCode(){
        Random random=new Random();
        char[] chars = new char[]{'0','1','2','3','4','5','6','7','8','9'};
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < 4; i++){
            int index=random.nextInt(chars.length);
            code.append(chars[index]);
        }
        return code.toString();
    }
}
