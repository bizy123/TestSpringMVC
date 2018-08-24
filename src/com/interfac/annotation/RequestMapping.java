package com.interfac.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Resource;
/**
 * java中有四个元注解
 * 1.@retention  
 * 2.@target
 * 3.@Document
 * 4.@Inherited
 */

@Target({ElementType.TYPE,ElementType.METHOD})   
@Retention(RetentionPolicy.RUNTIME)   
@Inherited
public @interface RequestMapping {
public String value();
}
