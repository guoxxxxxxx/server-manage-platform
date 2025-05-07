/**
 * @Time: 2025/4/23 17:10
 * @Author: guoxun
 * @File: AddServerInfoVO
 * @Description:
 */

package com.iecas.servermanageplatform.pojo.vo;


import com.iecas.servermanageplatform.pojo.entity.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddServerInfoVO {

    /**
     * 添加是否全部成功
     */
    private Boolean allSuccess;

    /**
     * 失败的信息
     */
    private List<ServerInfo> failServerInfoList;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;
}
