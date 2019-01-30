package com.li.myweb.security;

import java.io.IOException;
//import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.li.myweb.Configure.Intercept;
import com.li.myweb.HttpRuntime;
import com.li.myweb.HttpServerUtility;
import com.li.myweb.Utils;

//����ȫ������������Ƶ������Ŀͻ���
public class InterceptorFilter implements Filter {
	public InterceptorFilter(ServletContext context){
		this.server=(HttpServerUtility)context.getAttribute(HttpRuntime.APP_ATTR_SERVER);
	    this.interceptorInfo=this.server.getConfigure().getInterceptSet();//this.server.getInterceptorInfo();
	    if(this.interceptorInfo.isAutoIntercept()){
	    	this.autoblacklist=new ConcurrentHashMap<Long,BlacklistEntry>();
	    	this.accessmap=new ConcurrentHashMap<Long,AccessedEntry>();
	    }
	    if(this.interceptorInfo.getBlackList()!=null){
	    	String path=this.interceptorInfo.getBlackList();
	    	if(path.startsWith("http")){
	    		this.blackList=new RemoteList(path);
	    	}
	    	else{
	    		path=this.server.mapPath(path);
		    	this.blackList
		    		=new LocalFileList(path);
	    	}
	    }
	    if(this.interceptorInfo.getWhiteList()!=null){
	    	String path=this.interceptorInfo.getWhiteList();
	    	if(path.startsWith("http")){
	    		this.whiteList=new RemoteList(path);
	    	}
	    	else{
	    		path=this.server.mapPath(path);
		    	this.whiteList
		    		=new LocalFileList(path);
	    	}
	    }
	}
	class AccessedEntry{
		public AccessedEntry(long ip){
			this.ip=ip;
			this.createTime=System.currentTimeMillis();
			this.lastAccessTime=this.createTime;
			this.count=new AtomicInteger();
			this.count.incrementAndGet();
		}
		public final long ip;
		public final long createTime;
		private long lastAccessTime;
		private AtomicInteger count;
		private int[] history=new int[60];    //60s�ڵ���ʷ��¼
		public long getLastAccessTime(){
			return this.lastAccessTime;
		}
		public void setLastAccessTime(){
			this.lastAccessTime=System.currentTimeMillis();
			int count=this.count.incrementAndGet();
			history[(int)((this.lastAccessTime/1000)%history.length)]=count;
		}
		public int getCountPerMinu(){
			/*
			long diff=(this.lastAccessTime-this.createTime)/60000;
			if(diff==0)
				return this.count.get();
			return (int)(this.count.get()/diff);
			*/
			int[] hisshadow=Arrays.copyOf(this.history, 60);
			Arrays.sort(hisshadow);
			//�������ֵΪ0����ֱ�ӷ���0
			if(hisshadow[59]==0){
				return 0;
			}
			//Ѱ����С�ķ�0ֵ��λ��
			int minIndex=0;
			for(int i=0;i<hisshadow.length;i++){
				if(hisshadow[i]>0){
					minIndex=i;
					break;
				}
			}
			return hisshadow[59]-hisshadow[minIndex];
			
		}
	}
	class BlacklistEntry{
		public BlacklistEntry(long ip){
			this.ip=ip;
			this.createTime=System.currentTimeMillis();
		}
		public final long ip;
		public final long createTime;
		public boolean overdue(int expireMinu){
			return (System.currentTimeMillis()-this.createTime)/60000>expireMinu;
		}
	}
	private ConcurrentHashMap<Long,BlacklistEntry> autoblacklist;        //������
	private ConcurrentHashMap<Long,AccessedEntry> accessmap;         //����ͳ����
	private Intercept interceptorInfo;
	private HttpServerUtility server;
	private InterecptorList whiteList;
	private InterecptorList blackList;
	private Thread thr_1;
	private Thread thr_2;
	public boolean isListCheckUpdate(){
		return this.whiteList!=null||this.blackList!=null;
	}
	public void checkListUpdate(){
		if(this.whiteList!=null)
			this.whiteList.checkUpdate();
		if(this.blackList!=null)
			this.blackList.checkUpdate();
	}
	public boolean isAutoInterept(){
		return this.interceptorInfo.isAutoIntercept();
	}
	//�Զ��ж�
	public void autoJudge(){
		ArrayList<Long> removeA=new ArrayList<Long>();    //��Ҫ����ɾ���ķ��ʼ�¼
		for(AccessedEntry entry:accessmap.values()){
			//���������ʱ��������ڳ���30���ӣ�ɾ�����ʼ�¼
			if(System.currentTimeMillis()-entry.lastAccessTime>1000*60*30){
				removeA.add(entry.ip);
				continue;
			}
			//���һ�����ڵķ�����������ֵ��������������ͬʱɾ�����ʼ�¼
			if(entry.getCountPerMinu()>this.interceptorInfo.getAutoAccessLimit()){
				removeA.add(entry.ip);
				BlacklistEntry blackItem=new BlacklistEntry(entry.ip);
				this.autoblacklist.putIfAbsent(entry.ip, blackItem);
			}
		}
		for(long ip:removeA){
			accessmap.remove(ip);
		}
	}
	private boolean destroied=false;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		this.autoblacklist=null;
		this.accessmap=null;
		this.blackList=null;
		this.whiteList=null;
		this.destroied=true;
		if(this.thr_1!=null)
			this.thr_1.interrupt();
		if(this.thr_2!=null)
			this.thr_2.interrupt();
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		long t=System.currentTimeMillis();
		arg0.setAttribute(HttpRuntime.REQATTR_ENTERTIME_INTERCEPTOR, t);
		if(arg0.getAttribute(HttpRuntime.REQATTR_ENTERTIME)==null)
			arg0.setAttribute(HttpRuntime.REQATTR_ENTERTIME, t);
		HttpServletRequest req=(HttpServletRequest)arg0;
		HttpServletResponse resp=(HttpServletResponse)arg1;
		//��ȡ����IP
		String ipStr=req.getRemoteAddr();
		InetAddress ip=InetAddress.getByName(ipStr);
		//Inet4Address ip=(Inet4Address)Inet4Address.getByName(ipStr);
		long ipLong=Utils.readLong(ip.getAddress());
		//��������
		if(this.whiteList!=null&&this.whiteList.contains(ipLong)){
			chain.doFilter(arg0, arg1);
			return;
		}
		//String version=(String)this.server.getApplication().getAttribute(HttpRuntime.APP_DISPLAYINFO);
		//��������
		if(this.blackList!=null&&this.blackList.contains(ipLong)){
			resp.setHeader("eagle-obstruct","in blacklist");
			//resp.setHeader("E-Framework", version);
			resp.sendError(403, "���ķ����Ѿ������Σ�");
			return;
		}
		//����������Զ����ι���
		if(this.interceptorInfo.isAutoIntercept()){
			//���ȼ����������Ƿ��и�IP
			BlacklistEntry blackItem=autoblacklist.get(ipLong);
			if(blackItem!=null){
				//����Ѿ����˺�������Ч�ڣ���ɾ��������
				//���򷵻ؽ�ֹ����
				if(blackItem.overdue(this.interceptorInfo.getAutoForbidExp())){
					autoblacklist.remove(ipLong);
				}
				else{
					resp.setHeader("eagle-obstruct", blackItem.createTime+"");
					//resp.setHeader("E-Framework",version);
					resp.sendError(403, "�������Ƶ����ϵͳ���Զ���������������Ժ����ԣ�");
					return;
				}
			}
			//��ȡ����ͳ����
			AccessedEntry accEntry=this.accessmap.get(ipLong);
			if(accEntry==null){
				accEntry=new AccessedEntry(ipLong);
				this.accessmap.putIfAbsent(ipLong, accEntry);
			}
			else{
				accEntry.setLastAccessTime();
				/*
				//���������ֵ�������������ͬʱɾ�����ʼ�¼
				if(accEntry.getCountPerMinu()>this.interceptorInfo.autoAccessLimit){
					blackItem=new BlacklistEntry(ipLong);
					this.autoblacklist.putIfAbsent(ipLong, blackItem);
					resp.setHeader("eagle-obstruct","auto forbid at "+ blackItem.createTime);
					resp.setHeader("Framework", HttpRuntime.VERSION);
					resp.sendError(403, "�������Ƶ����ϵͳ���Զ���������������Ժ����ԣ�");
					//�Ƴ����ʼ�¼
					this.accessmap.remove(ipLong);
					return;
				}
				*/
			}
		}
		//����������
		chain.doFilter(arg0, arg1);
	}
	@Override
	public void init(FilterConfig config) throws ServletException {
		/*
		this.server=(HttpServerUtility)config.getServletContext().getAttribute(HttpRuntime.APP_ATTR_SERVER);
	    this.interceptorInfo=this.server.getInterceptorInfo();
	    if(this.interceptorInfo.autoIntercept){
	    	this.autoblacklist=new ConcurrentHashMap<Long,BlacklistEntry>();
	    	this.accessmap=new ConcurrentHashMap<Long,AccessedEntry>();
	    }
	    if(this.interceptorInfo.blackListFile!=null){
	    	String path=this.interceptorInfo.blackListFile;
	    	if(path.startsWith("http")){
	    		this.blackList=new RemoteList(path);
	    	}
	    	else{
	    		path=this.server.mapPath(path);
		    	this.blackList
		    		=new LocalFileList(path);
	    	}
	    }
	    if(this.interceptorInfo.whiteListFile!=null){
	    	String path=this.interceptorInfo.whiteListFile;
	    	if(path.startsWith("http")){
	    		this.whiteList=new RemoteList(path);
	    	}
	    	else{
	    		path=this.server.mapPath(path);
		    	this.whiteList
		    		=new LocalFileList(path);
	    	}
	    }
	    */
		final InterceptorFilter intcptFilter=this;
		//������������ԶԺ�/���������и��¼��
		if(this.isListCheckUpdate()){
			Runnable runnable=new Runnable(){
				public void run(){
						while(!intcptFilter.destroied){
							try {
								Thread.sleep(3000);
								intcptFilter.checkListUpdate();
							} catch (InterruptedException e) {
								break;
							}
							catch(Exception e){
								
							}
						}
					}
				};
			//�������¼���߳�
			thr_1 =  new Thread(runnable);
			thr_1.setDaemon(true);
			thr_1.start();
		}
		//����������Զ����ι��ܣ����������ʼ�¼��ʱ����
		if(intcptFilter.isAutoInterept()){
			Runnable runnable=new Runnable(){
				public void run(){
						while(!intcptFilter.destroied){
							try {
								Thread.sleep(10000);
								intcptFilter.autoJudge();
							} catch (InterruptedException e) {
								break;
							}
							catch(Exception e){
								
							}
						}
					}
				};
			//�����Զ������߳�
			thr_2 =  new Thread(runnable);
			thr_2.setDaemon(true);
			thr_2.start();
		}
	}
}
