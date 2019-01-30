package com.li.myweb;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.li.myweb.templates.Template;
/*import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;*/

import com.li.myweb.exceptions.ActionFlushImme;
import com.li.myweb.exceptions.ActionInterrupt;

@Deprecated
public abstract class ActionGroupImpl {
	public ActionGroupImpl(){
		HttpContext context=HttpContext.current();
		if(context!=null){
			this.httpContext=context;
			this.request=context.request();
			this.response=context.response();
			this.server=context.server();
		}
		//templateContext=new VelocityContext();
	}
	protected HttpRequest request;                  //Response����
	protected HttpResponse response;                //Request����
	protected HttpServerUtility server;			  //Servlet����
	protected HttpContext httpContext;		      //��ǰ��HttpContext
	//protected Context templateContext;
	protected Map<String,Object> templateContext;
	public void init(){
		if(this.httpContext==null){
			HttpContext context=HttpContext.current();
			this.httpContext=context;
			this.request=context.request();
			this.response=context.response();
			this.server=context.server();
		}
		templateContext=new HashMap<String,Object>();
	}
	//������ݵ�ģ����������
	protected void add(String name,Object value){
		this.templateContext.put(name, value);
	}
	public void mergeOut(Template temp,Writer writer){
		//temp.merge(this.templateContext, writer);
		temp.render(this.templateContext, writer);
	}
	//ֹͣ��ǰҳ��ִ��
	protected void stop(){
		throw new ActionInterrupt();
	}
	//��ɵ�ǰҳ��ִ�в��������
	protected void compleate(){
		throw new ActionFlushImme();
	}
}
