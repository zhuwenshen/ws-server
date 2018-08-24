package com.zhuwenshen.ws.model;

import com.alibaba.fastjson.JSON;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SocketRequestVo {

	private String appid;
	private String appkey;
	private String ip;
	private SocketVo socket;
	
	public static SocketRequestVo format(String json) {
		try {
			return JSON.parseObject(json, SocketRequestVo.class);
		} catch (Exception e) {
			log.info("格式化json错误",e);
			throw e;
		}
	}
}
