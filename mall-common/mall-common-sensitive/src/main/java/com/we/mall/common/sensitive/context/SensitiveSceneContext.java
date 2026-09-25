package com.we.mall.common.sensitive.context;

import com.we.mall.common.sensitive.enums.SensitiveSceneType;

/**
 * 脱敏场景上下文
 * <p>
 * 请求进入时设置，请求结束清除。
 *
 * @author we
 * @date 2026-09-24
 * @description
 */
public final class SensitiveSceneContext {

    private static final ThreadLocal<SensitiveSceneType> SCENE = new ThreadLocal<>();

    private SensitiveSceneContext() {
    }

    public static void set(SensitiveSceneType scene) {
        SCENE.set(scene);
    }

    public static SensitiveSceneType get() {
        return SCENE.get();
    }

    public static void clear() {
        SCENE.remove();
    }

    /**
     * 判断当前场景是否命中字段声明的场景
     *
     * @param scenes 字段声明的场景
     * @return true 表示需要脱敏
     */
    public static boolean match(SensitiveSceneType[] scenes) {
        if (scenes == null || scenes.length == 0) {
            return true;
        }
        SensitiveSceneType current = SCENE.get();
        if (current == null) {
            // 没设置场景，默认按 ALL 处理（脱敏）
            return true;
        }
        for (SensitiveSceneType scene : scenes) {
            if (scene == SensitiveSceneType.ALL || scene == current) {
                return true;
            }
        }
        return false;
    }

}
