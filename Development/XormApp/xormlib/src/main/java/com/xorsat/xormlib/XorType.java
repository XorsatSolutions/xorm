package com.xorsat.xormlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by khawar on 4/21/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface XorType {
    String DbTableName() default "";

    String JsonArrayName() default "";

    String UniqueColumnName() default "";

    String SyncColumnName() default "";
}