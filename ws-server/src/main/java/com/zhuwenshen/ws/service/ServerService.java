package com.zhuwenshen.ws.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zhuwenshen.ws.exception.ServerException;
import com.zhuwenshen.ws.model.JsonResult;
import com.zhuwenshen.ws.model.SocketRequestVo;
import com.zhuwenshen.ws.model.SocketVo;
import com.zhuwenshen.ws.util.IpUtil;
import com.zhuwenshen.ws.util.MySid;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServerService {
	

	// @Value输入属性值appid，默认值rabbit
	@Value("${appid:rabbit}")
	String appid;
	//@Value输入属性值appkey，默认值123456789
	@Value("${appkey:123456789}")
	String appkey;
	@Autowired
	NoticeService noticeService;
	@Autowired
	SystemService systemService;

	public JsonResult doService(String json, boolean checkIp) throws ServerException {
		//数据转换
		SocketRequestVo request = null;
		try {
			request = SocketRequestVo.format(json);
			if(request == null) {
				throw ServerException.exce("json数据格式错误");
			}
		} catch (Exception e) {
			log.error("数据转换错误", e);
			throw ServerException.exce("json数据格式错误");
		}
		
		return doService(request, checkIp);
		
	}
	
	public JsonResult doService(SocketRequestVo request, boolean checkIp) throws ServerException {
				
		//校验数据
		checkSocketRequest(request , checkIp);
		
		SocketVo socket = request.getSocket();		
		//校验sokect
		checkSocket(socket);
		if(socket.getAppid()==null) {
			socket.setAppid(request.getAppid());
		}		
		log.info("完整请求参数："+request);
		
		//操作分派
		JsonResult jsonResult = null;
		int opt = socket.getOptCode()/1000000;
		switch (opt) {
		case 0:
			throw ServerException.exce("操作码错误，操作码必须大于1000000");			
		case 1: //系统操作码
			jsonResult = systemService.operate(socket);
			break;
		case 2: //通知客户端操作码
			jsonResult = noticeService.notice(socket);
			break;
		default:
			throw ServerException.exce("操作码错误，操作码["+socket.getOptCode()+"]暂不处理");
			
		}
		
		//返回数据
		return jsonResult;
		
	}
	/**
	 * 校验sokect
	 * @param socket
	 * @throws ServerException 
	 */
	private void checkSocket(SocketVo socket) throws ServerException {
		if(socket == null) {
			throw ServerException.exce("socket不能为空");
		}
		
		//校验操作码
		if(socket.getOptCode() == null) {
			throw ServerException.exce("操作码不能为空");
		}
		
		//校验推送类型
		if(socket.getType()== null) {
			throw ServerException.exce("推送类型不能为空");
		}
		
		//生成sid
		if(StringUtils.isEmpty(socket.getSid())) {
			socket.setSid(MySid.next());
		}
		//生成时间戳
		if(socket.getT() == null) {
			socket.setT(System.currentTimeMillis());
		}	
		
		if(socket.getExpired() == null) {
			socket.setExpired(-1L);
		}
	}
	
	/**
	 * 校验数据
	 * @param requestVo
	 * @throws ServerException
	 */
	public void checkSocketRequest(SocketRequestVo requestVo , boolean checkIp) throws ServerException {
		//校验app空判断
		if(StringUtils.isEmpty(requestVo.getAppid())) {
			throw ServerException.exce("appid不能为空");
		}
		if(StringUtils.isEmpty(requestVo.getAppkey())) {
			throw ServerException.exce("appkey不能为空");
		}		
		
		if(checkIp) {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			String ip = IpUtil.getIpAddr(request);
			log.info("访问ip["+ip+"]");
			//TODO 校验ip
			requestVo.setIp(ip);
		}		
		
		//校验app值
		checkAppValue(requestVo.getAppid(), requestVo.getAppkey());		
	}
	
	/**
	 * 校验app值
	 * @param appid2
	 * @param appkey2
	 * @return
	 * @throws ServerException
	 */
	private boolean checkAppValue(String appid2, String appkey2) throws ServerException {		
		if(!appid.equals(appid2)) {
			throw ServerException.exce("appid错误");
		}
		
		if(!appkey.equals(appkey2)) {
			throw ServerException.exce("appkey错误");
		}
		
		return true;
	}

}
