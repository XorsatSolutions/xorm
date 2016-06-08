package com.xorsat.xormlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by khawar on 4/21/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface XorField {
    String DbColumnName() default "";

    String DbDataType() default "TEXT";

    String JsonKeyName() default "";

    boolean IsPrimaryKey() default false;
}
