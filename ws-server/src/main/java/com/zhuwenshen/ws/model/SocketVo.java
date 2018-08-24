package com.zhuwenshen.ws.model;

import java.util.List;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class SocketVo {

	//sid唯一值
	private String sid;
	//时间戳
	private Long t;
	//用户id
	private String userId;
	
	//appid	
	private String appid;
	
	private List<String> userIdList;
	//sessionid
	private String sessionId;
	//操作码  1001001，最小七位数，从右往左，1-3位是子子模块操作，4-6位是子模块操作，7-9位大模块操作
	/**
	 *  1xxxxxx 为系统模块
	 *  2xxxxxx 为通知模块
	 *  	2000001	 	连接成功
	 *  	2000002 	连接失败
	 */
	private Integer optCode;
	private Object param;
	private String jsonData;
	//过期时间 -1为永不过期
	private Long expired;
	//推送类型 
	/**
	 * 1:在线已登录用户 
	 * 2：appid下的登录用户 
	 * 3：指定人推送
	 */
	private String type;
	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
