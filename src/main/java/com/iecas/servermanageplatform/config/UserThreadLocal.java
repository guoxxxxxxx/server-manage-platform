/**
 * @Time: 2025/4/23 14:51
 * @Author: guoxun
 * @File: UserThreadLocal
 * @Description:
 */

package com.iecas.servermanageplatform.config;

import com.iecas.servermanageplatform.pojo.entity.UserInfo;

public class UserThreadLocal {

    private static final ThreadLocal<UserInfo> USER_INFO_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);


    /**
     * 设置用户信息
     * @param userInfo 用户信息
     */
    public static void setUserInfo(UserInfo userInfo){
        USER_INFO_THREAD_LOCAL.set(userInfo);
    }


    /**
     * 获取用户
     * @return 用户信息
     */
    public static UserInfo getUserInfo(){
        return USER_INFO_THREAD_LOCAL.get();
    }


    /**
     * 移除用户
     */
    public static void removeUser() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}
