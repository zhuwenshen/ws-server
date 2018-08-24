package com.zhuwenshen.ws.model;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataVo {

	//sid唯一值
	private String sid;
	//时间戳
	private Long t;
	//操作码  1001001，最小七位数，从右往左，1-3位是子子模块操作，4-6位是子模块操作，7-9位大模块操作
	/**
	 *  1xxxxxx 为系统模块
	 *  2xxxxxx 为通知模块
	 *  	2000001 连接成功
	 */
	private Integer optCode;
	private String jsonData;
	//过期时间 -1为永不过期
	private Long expired;
	
	public static DataVo getDataVo(SocketVo socketVo) {
		return new DataVo(socketVo.getSid(),socketVo.getT(),socketVo.getOptCode(),socketVo.getJsonData(),socketVo.getExpired());
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	
}
