package com.we.mall.common.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 测试用用户对象
 *
 * @author we
 * @date 2026-09-21
 * @description
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User {

    private Long id;
    private String name;
    private Integer age;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
