package com.zhuwenshen.ws.model;

import com.alibaba.fastjson.JSON;
import com.zhuwenshen.ws.exception.ServerException;
import com.zhuwenshen.ws.util.MySid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult {

	private String sid;
	private long t;
	private Boolean status = true;
	private String msg;
	private Object data;
	
	public static JsonResult ok(String msg) {
		return new JsonResult(MySid.next(),System.currentTimeMillis(),true,msg,null);
	}
	
	public static JsonResult ok(String sid,String msg) {
		return new JsonResult(sid,System.currentTimeMillis(),true,msg,null);
	}
	
	public static JsonResult ok(String sid,String msg, Object data) {
		return new JsonResult(sid,System.currentTimeMillis(),true,msg,data);
	}
	
	public static JsonResult fail(String msg) {
		return new JsonResult(MySid.next(),System.currentTimeMillis(),false,msg,null);
	}
	
	public static JsonResult fail(String sid,String msg) {
		return new JsonResult(sid,System.currentTimeMillis(),false, msg, null);
	}
	
	public static JsonResult fail(String sid,String msg, Object data) {
		return new JsonResult(sid,System.currentTimeMillis(),false, msg, data);
	}

	public static JsonResult fail(ServerException e) {
		return new JsonResult(e.getSid(),System.currentTimeMillis(),false, e.getMessage(), null);
	}

	
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}	
}
