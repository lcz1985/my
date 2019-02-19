package com.li.cson;

public class CSONVersionNotMatchException extends CSONException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6022574737316758374L;
	public CSONVersionNotMatchException(byte proto) {
		super(String.format("�޷�����CSON���ݣ�CSON���ݵİ汾�뵱ǰ�汾��һ��,���ݰ汾��%d����ǰ�汾��%d", proto, CSON2.PROTOVERSION));
	}
}
