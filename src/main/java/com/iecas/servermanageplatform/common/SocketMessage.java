/**
 * @Time: 2025/4/29 15:45
 * @Author: guoxun
 * @File: SocketMessage
 * @Description:
 */

package com.iecas.servermanageplatform.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketMessage {

    /**
     * 消息抬头
     */
    private String title;


    /**
     * 消息
     */
    private String message;


    /**
     * 状态码
     */
    private Integer status;


    /**
     * 终端列数
     */
    private Integer colSize;


    /**
     * 终端行数
     */
    private Integer rowSize;


    /**
     * 成功
     * @param message 消息
     * @return SocketMessage
     */
    public static SocketMessage success(String title, String message){
        return SocketMessage.builder()
                .title(title)
                .message(message)
                .status(200)
                .build();
    }


    /**
     * 失败
     * @param message 消息
     * @return SocketMessage
     */
    public static SocketMessage fail(String title, String message){
        return SocketMessage.builder()
                .title(title)
                .message(message)
                .status(500)
                .build();
    }
}
