package demo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import demo.model.CartInfo;
import demo.model.Sex;
import demo.model.UserInfo;

import com.li.cson.CSON2;
import com.li.myweb.HttpContext;
import com.li.myweb.annotation.*;

public class simple {
	/*
	 * Ĭ��action
	 */
	public void doGet(){
		
	}
	/*
	 * �޲����޷�������
	 */
	public void Default(){
		
	}
	/*
	 * �޲�������������ΪString
	 */
	@Model("say")
	public String string(){

	    return "���ѽ��Hello,world,now time is "+new Date();
	}
	@Layout(templateOut=false)
	public String string2(){
		return "���ѽ2��Hello,world,now time is "+new Date();
	}
	/*
	 * �򵥲���,��������ΪObject[]
	 */
	@Model("rs")
	public Object[] simpleparam(String title,Long index,String m2a){
		return new Object[]{title,index,m2a,new Date()};
	}
	/*
	 * ���������������
	 */
	@Model("rs")
	public String[] simpleArray(String[] inputs){
		return inputs;
	}
	/*
	 * ʵ�����Ͳ���
	 */
	@Model("u")
	public UserInfo model(UserInfo user){
		return user;
	}
	/*
	 * ʵ�������Ͳ���
	 */
	@Model("us")
	public UserInfo[] modelArray(UserInfo[] user){
		String cson=CSON2.serializeBase64(user, false);
		System.out.println(cson);
		return user;
	}
	/*
	 * CSON2�Ͳ���
	 */
	@Model("u")
	public UserInfo csonparam(@Param("cson")CSON2 cson){

	    return (UserInfo)cson.getData(0, UserInfo.class);
	}
	/*
	 * ����JSON����
	 */
	@JSON
	public UserInfo json(UserInfo user){

	    return user;
	}
	/*
	 * ����CSON����(BYTE)
	 */
	@CSON
	public UserInfo cson(UserInfo user){

	    return user;
	}
	/*
	 * ����CSON����(String)
	 */
	@CSON(binary=false)
	public UserInfo csonstr(@Param("user")UserInfo user){
		user.Cart=new CartInfo();
		//user.Cart.ID=35222885;
		//user.Cart.Name="";
		return user;
	}
	/*
	 * ���ض�������
	 */
	public Map<String,Object> multiout(@Param("title")String title,@Param("user")UserInfo user){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("title", title);
		map.put("u", user);
		map.put("date", new Date());
		return map;
	}
	public void endresponse(){
		HttpContext.current().response().writeln("End Response");
		HttpContext.current().response().end();
	}
	@JSON
	public UserInfo orderedParam(UserInfo user1,UserInfo user2,int m){
		if(m==0)
			return user1;
		else
			return user2;
	}
	@Layout(templateOut=false)
	public void pp(@Param("a")Integer a,@Param("b")int b){
		HttpContext.current().response().writeln(a.toString());
		HttpContext.current().response().writeln(b+"");
	}
	@Layout(templateOut=false)
	public void enumer(@Param("sex")Sex sex){
		HttpContext.current().response().writeln(sex.toString());
	}
	public void couponOrder(@Param("orderNum") String orderNum,
			@Param("activityId") Long activityId,
			@Param("activityName") String activityName,
			@Param("activityType") Integer activityType,
			@Param("createTime1") String createTime1,
			@Param("createTime2") String createTime2,
			@Param("realName") String realName,
			@Param("couponCode") String couponCode,
			@Param("pageNum") Integer pageNum,
			@Param("pageSize") Integer pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();

		if (pageNum == null || pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize == null || pageSize < 1) {
			pageSize = 20;
		}

		params.put("orderNum", orderNum);
		params.put("activityId", activityId);
		params.put("activityName", activityName);
		params.put("activityType", activityType);
		params.put("createTime1", createTime1);
		params.put("createTime2", createTime2);
		params.put("realName", realName);
		params.put("couponCode", couponCode);

		/*ReturnDataListWithPage<Map<String, Object>> result = orderImpl
				.queryCouponOrder(params, pageNum, pageSize);

		this.addMapData("params", params);*/
		//this.addData("pageInfo", result);
		//this.addData("pageProxy", this);
	}
}
