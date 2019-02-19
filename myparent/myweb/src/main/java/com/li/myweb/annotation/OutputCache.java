package com.li.myweb.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

import com.li.myweb.HttpRuntime;

//����ҳ�滺��
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface OutputCache {
	//�Ƿ�����ҳ��Cache
	boolean enable() default true;   
	//������������������Cookie,Ĭ�ϲ���URI+ACTION
	int dependTypes() default 0;   
	//����ʱ��,Ĭ��5����
	int periods() default HttpRuntime.OUTPUTCACHEPERIODS;
	//�Ƿ��������������Cache-Control
	boolean enableCacheControl() default false;
}
