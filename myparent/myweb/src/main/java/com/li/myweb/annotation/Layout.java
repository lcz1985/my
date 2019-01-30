package com.li.myweb.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Layout {
	//�Ƿ����ģ��
	@Deprecated
	boolean isNeedVm() default true; 
	//ģ��·��
	String template() default "";
	boolean templateOut() default true;
}