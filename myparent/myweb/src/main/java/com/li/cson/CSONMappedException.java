package com.li.cson;

public class CSONMappedException extends CSONException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2019960878413410828L;

	public CSONMappedException(String n1,String n2){
		super(String.format("%s������˶�%s��ӳ�䣬�޷��ٽ���ӳ�䣡", n1,n2));
	}
}
