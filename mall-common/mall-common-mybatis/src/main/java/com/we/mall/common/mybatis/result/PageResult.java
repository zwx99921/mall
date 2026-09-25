package com.we.mall.common.mybatis.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 统一分页结果返回
 *
 * @author we
 * @date 2026-06-09
 * @description
 */

@Data
public class PageResult<T> {

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 数据列表
     */
    private List<T> records;


    private PageResult(Long pages, Long total, Long pageNum, Long pageSize, List<T> records) {
        this.pages = pages;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    /**
     * 静态构造方法
     */
    public static <T> PageResult<T> of(Long pages, Long total, Long pageNum, Long pageSize, List<T> records) {
        return new PageResult<>(pages, total, pageNum, pageSize, records);
    }

    /**
     * 静态构造方法
     */
    public static <T> PageResult<T> of(IPage<?> page, List<T> records) {
        return new PageResult<>(page.getPages(), page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

}
