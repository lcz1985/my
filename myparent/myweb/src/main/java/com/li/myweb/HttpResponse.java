package com.li.myweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.li.myweb.exceptions.ActionInterrupt;
import com.li.myweb.supports.ActionCaller;

public class HttpResponse extends HttpServletResponseWrapper {

	public HttpResponse(HttpServletResponse response,String outencode) {
		super(response);
		// TODO Auto-generated constructor stub
		this.outencode=outencode;
		//����Ĭ���ĵ�����
		this.setContentType("text/html");
	}
	private String outencode;
	private ActionCaller currentCaller;
	private void setenc(){
		if(this.outencode==null)
			return;
		String ct=this.getContentType();
		if(ct==null)
			return;
		if(ct.equalsIgnoreCase("text/xml")||ct.equalsIgnoreCase("text/html")||ct.equalsIgnoreCase("text/css")){
			//������Ӧ����
			this.setCharacterEncoding(this.outencode);
		}
	}
	/*
	 * ������ݵ�����������
	 */
	public void add(String key,Object obj){
		this.currentCaller.add(key, obj);
	}
	public void addAll(Map<String,Object> map){
		this.currentCaller.addAll(map);
	}
	public void setCurrentCaller(ActionCaller caller){
		this.currentCaller=caller;
	}
	@Override
	public PrintWriter getWriter() throws IOException{
		//����Ĭ�ϱ���
		this.setenc();
		return super.getWriter();
	}
	@Override
	public ServletOutputStream getOutputStream() throws IOException{
		//����Ĭ�ϱ���
		this.setenc();
		return super.getOutputStream();
	}
	public void write(String str){
		PrintWriter writer;
		try {
			writer = this.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		writer.write(str);
	}
	public void writeln(String str){
		PrintWriter writer;
		try {
			writer = this.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		writer.write(str+"\r\n");
	}
	public void end(){
		throw new ActionInterrupt();
	}
	public void redirect(String url,boolean stop){
		try {
			this.sendRedirect(url);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(stop){
			this.end();
		}
	}
	public void redirect(String url){
		this.redirect(url,true);
	}
	@SuppressWarnings("deprecation")
	protected void setExpires(Date date){
		String str=date!=null?date.toGMTString():null;
		this.setHeader("Expires", str);
	}
	//����Cache-Control
	protected void setCacheControl(BrowserCacheControl... cacheControls){
		String str=null;
		if(cacheControls!=null&&cacheControls.length>0){
			StringBuffer buffer=new StringBuffer();
			for(int i=0;i<cacheControls.length;i++){
				if(i>0)
					buffer.append(",");
				buffer.append(cacheControls[i].toString());
			}
			str=buffer.toString();
		}
		this.setHeader("Cache-Control", str);
	}
	//����ETag
	public void setETag(String etag){
		this.setHeader("Etag", etag);
	}
	//��ȡETag
	public String getETag(){
		return this.getHeader("Etag");
	}
	//����LastModified
	public void setLastModified(Date date){
		String str=date!=null?date.getTime()+"":null;;
		this.setHeader("Last-Modified", str);
	}
	//��ȡLastModified
	public Date getLastModified(){
		String str=this.getHeader("Last-Modified");
		if(str==null)
			return null;
		return new Date(Long.parseLong(str));
	}
}
