package com.li.myweb.exceptions;

import com.li.myweb.ActionGroupImpl;

public class NeedSetMasterInfoException extends RuntimeException {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4627619990152986777L;

	public NeedSetMasterInfoException(ActionGroupImpl ag){
		super(String.format("��ҪΪ%sָ��ĸ��ҳ��Ϣ����ͨ��setMaster�����������ã�",ag.getClass().getName()));
	}
}
