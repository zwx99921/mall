package com.we.mall.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mybatis 自动配置
 *
 * @author we
 * @date 2026-09-20
 * @description
 */
@AutoConfiguration
@ConditionalOnClass(MybatisPlusInterceptor.class)
public class MyBatisAutoConfiguration {

    /**
     * 分页插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 字段插入
     */
    @Bean
    @ConditionalOnMissingBean
    public MetaObjectHandler metaObjectHandler() {
        // 创建时间字段常量
        final String CREATE_TIME = "createTime";
        // 更新时间字段常量
        final String UPDATE_TIME = "updateTime";
        // 统一时区
        final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                if (metaObject.hasSetter(CREATE_TIME))
                    this.strictInsertFill(metaObject, CREATE_TIME, () -> LocalDateTime.now(ZONE), LocalDateTime.class);
                if (metaObject.hasSetter(UPDATE_TIME))
                    this.strictInsertFill(metaObject, UPDATE_TIME, () -> LocalDateTime.now(ZONE), LocalDateTime.class);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                if (metaObject.hasSetter(UPDATE_TIME))
                    this.strictUpdateFill(metaObject, UPDATE_TIME, () -> LocalDateTime.now(ZONE), LocalDateTime.class);
            }
        };
    }

}
