package com.li.myweb.supports;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.li.myweb.HttpContext;
import com.li.myweb.HttpRuntime;

public class CaseFilter implements Filter {
	private int contextlen;
	//private ClassLoader apploader;
	//private HttpServerUtility server;
	//private Configure appConfig;
	//public final static Pattern REGEX_UPER=Pattern.compile("[A-Z]+");
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// �������ת����ֱ��ִ��
		if(request.getAttribute("CASEFILTER_FORWARD")!=null){
			chain.doFilter(request,response);
			return;
		}
		/*//���õ�ǰ��ClassLoader
		if(this.apploader!=null)
			Thread.currentThread().setContextClassLoader(this.apploader);*/
		final HttpServletRequest httpRequest=(HttpServletRequest)request;
        final HttpServletResponse httpResponse=(HttpServletResponse)response;
		//���ÿ����Ϣ
		String frameInfo=(String)request.getServletContext().getAttribute(HttpRuntime.APP_DISPLAYINFO);
		if(frameInfo!=null)
			httpResponse.setHeader("E-framework",frameInfo);
		httpResponse.setHeader("Cache-Control", "max-age=0");
		//���ý���ʱ��
		request.setAttribute(HttpRuntime.REQATTR_ENTERTIME_HTTPSERVLET, System.currentTimeMillis());
		String url=httpRequest.getRequestURI().substring(this.contextlen);
		
		boolean dispantcher=false;
		//�ж��Ƿ��д�д��ĸ���������ת��ΪСд
		//boolean url2low=false;
        for(int i=0;i<url.length();i++){
			char c=url.charAt(i);
			if(c>='A'&&c<='Z'){
				dispantcher|=true;
				break;
			}
		}
        /*//�ж��Ƿ������_action��,��������ת��Ϊpath/action.do����ʽ
        String actionName=httpRequest.getParameter("_action");
        if(!(actionName==null||(actionName=actionName.trim()).length()==0)){
        	int a=url.lastIndexOf('.');
        	if(a>0){
        		url=url.substring(0,a)+"/"+actionName+url.substring(a);
        	}
        	else{
        		url=url.substring(0,a)+"/"+actionName;
        	}
        	httpRequest.setAttribute("ACTION_NAME", actionName);
        }*/
		//Matcher matcher=REGEX_UPER.matcher(url);
		//url2low=matcher.find();
        //��ʼ��HttpContext
        //HttpContext context=new HttpContext(httpRequest,httpResponse);
        if(dispantcher){
        	url=url.toLowerCase();
        	if(url.charAt(0)!='/')
    			url="/"+url;
        	//httpResponse.setHeader("debug-context", request.getServletContext().getContextPath());
        	//httpResponse.setHeader("debug-dispatcher", url);
        	
			RequestDispatcher rd=request.getRequestDispatcher(url);
			request.setAttribute("CASEFILTER_FORWARD", true);
			rd.forward(request, response);
        }else{
        	chain.doFilter(request, response); 
        }
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		this.contextlen=arg0.getServletContext().getContextPath().length();
		/*Object objLoader=arg0.getServletContext().getAttribute("APP_CLASSLOADER");
		if(objLoader!=null)
			this.apploader=(ClassLoader)objLoader;*/
		//this.server=(HttpServerUtility)arg0.getServletContext().getAttribute(HttpRuntime.APP_ATTR_SERVER);
		//this.appConfig=(Configure)arg0.getServletContext().getAttribute(HttpRuntime.APP_CONFIGS);
	}

}
