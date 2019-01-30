package com.li.myweb.supports;

import com.li.myweb.IEtagSupported;
import com.li.myweb.ILastModifySupported;
import com.li.myweb.Utils;
import com.li.myweb.annotation.Master;
import com.li.myweb.annotation.OutputCache;
import com.li.myweb.annotation.Layout;

public class ActionGroupInfo {
	public ActionGroupInfo(Class<?> inst){
		this.impl=inst;
		Layout layout=inst.getAnnotation(Layout.class);
		this.layout=layout;
		
		OutputCache cctrl=inst.getAnnotation(OutputCache.class);
		this.outputCache=cctrl;
		
		Master master=inst.getAnnotation(Master.class);
		this.master=master;
		boolean[] impls=Utils.isImplements(inst, new Class<?>[]{IEtagSupported.class,ILastModifySupported.class});
		etagEnable=impls[0];
		modifiedEnable=impls[1];
	}
	public final Layout layout;               //�������
	public final OutputCache outputCache;     //����������
	public final Master master;               //ĸ��
	public final Class<?> impl;         	  //����
	public final boolean etagEnable;          //�Ƿ�ʵ��IETag�ӿ�
	public final boolean modifiedEnable;      //�Ƿ�ʵ��ILastModified�ӿ�
	@Override
	public String toString(){
		return this.impl.toString()
		+" [EtagEnable="+etagEnable+";"+"ModifiedEnable="+modifiedEnable+"]"
		+(this.layout==null?"":(" [Layout:IsNeedVm="+this.layout.isNeedVm()+" Template="+this.layout.template()+"]"));
	}
}
