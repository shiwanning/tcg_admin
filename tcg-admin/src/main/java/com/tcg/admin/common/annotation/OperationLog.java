package com.tcg.admin.common.annotation;

import java.lang.annotation.*;

/**
 * com.tcg.admin.common
 *
 * @author lyndon.j
 * @version 1.0
 * @date 2019/6/26 17:11
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String type() default "DEFAULT";
}
