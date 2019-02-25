package com.li.myweb.supports;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.li.myweb.HttpMethod;
import com.li.myweb.annotation.Allows;
import com.li.myweb.annotation.Master;
import com.li.myweb.annotation.OutputCache;
import com.li.myweb.annotation.Layout;
import com.li.myweb.annotation.Param;
import com.li.myweb.exceptions.DefParamsUnmatchException;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class ActionInfo {
	public ActionInfo(Method method) throws DefParamsUnmatchException{
		this.method=method;
		Layout layout=method.getAnnotation(Layout.class);
		this.layout=layout;
		
		OutputCache cctrl=method.getAnnotation(OutputCache.class);
		this.outputCache=cctrl;
		
		Master master=method.getAnnotation(Master.class);
		this.master=master;
		
		Allows allow=method.getAnnotation(Allows.class);
		if(allow!=null){
			int temp=0;
			for(String ame:allow.value()){
				temp|=HttpMethod.getCode(ame);
			}
			allows=temp;
		}
		else{
			//û����ʾָ��������£�Ĭ��ֻ����HEAD,GET,POST
			allows=HttpMethod.getCode(HttpMethod.GET)|HttpMethod.getCode(HttpMethod.POST)|HttpMethod.getCode(HttpMethod.HEAD);
		}
			
		//������Ϣ
		Class<?>[] pmTypes=method.getParameterTypes();

//		Parameter[] pms2=method.getParameters();
//		for(Parameter pm:pms2){
//			System.out.println(pm.isNamePresent());
//		}
//		pms2=pms2;




		if(pmTypes==null||pmTypes.length==0){
			paramTypes=null;
			paramNames=null;
		}
		else {//rewrite by Mr.Li 2019-02-25
			paramTypes=pmTypes;
			LocalVariableTableParameterNameDiscoverer u =new LocalVariableTableParameterNameDiscoverer();
			paramNames = u.getParameterNames(method);

		}
//		else{
//			paramTypes=pmTypes;
//			//��ȡ������
//			String[] namesTmp=new String[pmTypes.length];
//			//paramNames=new String[pmTypes.length];
//			Annotation[][] annos=method.getParameterAnnotations();
//			int nameGetted=0;
//			for(int i=0;i<pmTypes.length;i++){
//				for(Annotation anno:annos[i]){
//					if(anno instanceof Param){
//						//paramNames[i]=((Param)anno).value();
//						namesTmp[i]=((Param)anno).value();
//						nameGetted++;
//						break;
//					}
//				}
//				/*if(paramNames[i]==null)
//					throw new DefParamsUnmatchException(
//							String.format("��%s������%s����%d������δ����ParamNameע�ͣ��޷����в���ʶ��!",method.getDeclaringClass().getName(), method.getName(),i));*/
//			}
//			if(nameGetted>0&&nameGetted!=namesTmp.length){
//				throw new DefParamsUnmatchException(
//						String.format("��������ڶ�action������������@ParamName�������%s:%s��������ת��ʱ���������в�������@ParamName�����ղ�����ƥ������������ղ�������˳��ƥ�䡣",method.getDeclaringClass().getName(), method.getName()));
//			}
//			if(nameGetted==0){
//				paramNames=null;
//			}
//			else{
//				paramNames=namesTmp;
//			}
//		}
		
	}
	public final Method method;           //����
	public final Class<?>[] paramTypes;   //��������
	public final String[] paramNames;     //��������
	
	public final int allows;
	public final OutputCache outputCache;   //����������
	public final Layout layout;   
	public final Master master;               //ĸ��
}
