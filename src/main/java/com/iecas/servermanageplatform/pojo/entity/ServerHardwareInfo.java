package com.iecas.servermanageplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: guo_x
 * @Date: 2025/5/7 10:27
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerHardwareInfo {

    /**
     * cpu 信息
     */
    private String cpu;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 磁盘总空间
     */
    private Long totalDiskSpace;

    /**
     * 空闲磁盘空间
     */
    private Long freeDiskSpace;

    /**
     * 内存总空间
     */
    private Long totalMemSpace;

    /**
     * 空闲内存空间
     */
    private Long freeMemSpace;
}
