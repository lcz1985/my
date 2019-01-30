package com.li.myweb.caching;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.li.myweb.HttpMethod;
import com.li.myweb.HttpRuntime;
import com.li.myweb.caching.OutputOSCache.CacheContext;
import com.li.myweb.caching.OutputOSCache.CacheKey;

public class OutputCachedFilter implements Filter {

	private OutputOSCache cache;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		long t1=System.currentTimeMillis();
		arg0.setAttribute(HttpRuntime.REQATTR_ENTERTIME_OUTPUTCACHE, t1);
		if(arg0.getAttribute(HttpRuntime.REQATTR_ENTERTIME)==null)
			arg0.setAttribute(HttpRuntime.REQATTR_ENTERTIME, t1);
		HttpServletRequest req=(HttpServletRequest)arg0;
		String method=req.getMethod();
		//ֻ��GET��HEAD���������Ч
		if(!(method.equals(HttpMethod.GET)||method.equals(HttpMethod.HEAD))){
			chain.doFilter(arg0, arg1);
			return;
		}
		//����������
		CacheKey key=cache.getkey(req);
		//���keyΪnull��ʾ������ִ�н����������
		if(key==null){
			chain.doFilter(arg0, arg1);
			return;
		}
			
		HttpServletResponse resp=(HttpServletResponse)arg1;
		
		CacheContext context=cache.getContext(key);
		long cacheVersion=cache.hit(key,context.periods,req, resp);
		if(cacheVersion>0){
			long time_global=System.currentTimeMillis()-(Long)(req.getAttribute(HttpRuntime.REQATTR_ENTERTIME));
			if(context.cacheControlEnable){
				long age=((context.periods*1000+cacheVersion)-System.currentTimeMillis())/1000;
				resp.setHeader("Cache-Control", "max-age="+age);
			}
			resp.addHeader("E-cached", "time-total:"+time_global+" time-getcached:"+(System.currentTimeMillis()-t1));
			return;
		}
		//
		CacheHttpServletResponseWrapper wresp=new CacheHttpServletResponseWrapper(resp);
		chain.doFilter(req, wresp);
		
		int status=wresp.getStatus();
		cacheVersion=System.currentTimeMillis();
		if(status==200){
			wresp.setHeader("Last-Modified", cacheVersion+"");
			//����Cache-Controlͷ
	    	if(context.cacheControlEnable){
				long age=((context.periods*1000+cacheVersion)-System.currentTimeMillis())/1000;
				wresp.setHeader("Cache-Control", "max-age="+age);
			}
		}
		//�����Ӧ����
	    wresp.flushBuffer();
	    //�ж���Ӧ���Ƿ���200�������200�����û���
	    if(status==200){
	    	cache.set(cacheVersion,key, wresp);
	    }
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
		this.cache=(OutputOSCache)config.getServletContext().getAttribute(HttpRuntime.APP_ATTR_OUTPUTCACHE);
	}

}
