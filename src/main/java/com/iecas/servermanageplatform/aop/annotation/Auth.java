package com.iecas.servermanageplatform.aop.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 鉴权注解
 * 在方法名上添加该注解, 可以当前请求中通过UserThreadLocal类中的getUserInfo方法获取当前请求的用户的详细信息,
 * 若请求头中不包含当前用户token信息, 则会自动抛出用户未登录异常给前端
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
}
