package com.we.mall.common.excel.result;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
@Data
public class ImportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 成功数
     */
    private Integer success;

    /**
     * 失败数
     */
    private Integer fail;

    /**
     * 失败原因
     */
    private List<String> failReasons = new ArrayList<>();

}
