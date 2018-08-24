package com.zhuwenshen.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuwenshen.ws.exception.ServerException;
import com.zhuwenshen.ws.model.JsonResult;
import com.zhuwenshen.ws.model.SocketRequestVo;
import com.zhuwenshen.ws.service.ServerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ServerController {

	@Autowired
	ServerService serverService;
	
	/**
	 * 通知客户端
	 * @param sokectVo
	 * @return
	 */
	//@RequestMapping("/server")
	public JsonResult  server2(@RequestBody String json) {
		log.info("服务器参数，json:"+json);
		try {
			return serverService.doService(json , true);
		} catch (ServerException e) {
			log.info("请求错误：sid["+e.getSid()+"],错误："+e.getMessage());
			return JsonResult.fail(e);
		}catch (Exception e) {
			log.info("未知错误",e);
			return JsonResult.fail("服务器异常");
		}
	}
	
	@RequestMapping("/server")
	public JsonResult  server(@RequestBody SocketRequestVo request) {
		log.info("服务器参数，request:"+request);
		try {
			return serverService.doService(request, true);
		} catch (ServerException e) {
			log.info("请求错误：sid["+e.getSid()+"],错误："+e.getMessage());
			return JsonResult.fail(e);
		}catch (Exception e) {
			log.info("未知错误",e);
			return JsonResult.fail("服务器异常");
		}
	}
}
