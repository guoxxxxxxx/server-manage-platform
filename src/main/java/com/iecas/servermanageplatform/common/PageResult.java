/**
 * @Time: 2024/8/28 15:18
 * @Author: guoxun
 * @File: PageResult
 * @Description:
 */

package com.iecas.servermanageplatform.common;


import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;



@Data
public class PageResult<T> {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long currentPage;

    /**
     * 页面大小
     */
    private Long pageSize;

    /**
     * 当前数据
     */
    private List<T> data;


    /**
     * 设置基本信息
     * @param page
     */
    public PageResult(IPage<T> page){

        this.total = page.getTotal();
        this.currentPage = page.getCurrent();
        this.pageSize = page.getSize();
        this.data = page.getRecords();
    }
}
