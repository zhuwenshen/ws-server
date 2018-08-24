package com.zhuwenshen.ws.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.zhuwenshen.ws.exception.ServerException;
import com.zhuwenshen.ws.model.JsonResult;
import com.zhuwenshen.ws.model.SocketVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemService {

	/**
	 * 系统级别操作
	 * @param socket
	 * @return
	 * @throws ServerException 
	 */
	public JsonResult operate(SocketVo socket) throws ServerException {
		if(socket.getExpired()!=-1&&System.currentTimeMillis()>socket.getExpired()) {
			return JsonResult.fail(socket.getSid(), "消息已过期");
		}
		int optCode = socket.getOptCode();
		switch (optCode) {
		case 1001001: //用户登录通知
			return this.userLogin(socket);			
		case 1001002: //用户登出通知
			return this.userLogout(socket);	
		default:
			break;
		}
		log.info("id["+socket.getSid()+"]暂不支持该操作码["+optCode+"]");
		return JsonResult.fail(socket.getSid(),	"暂不支持该操作码["+optCode+"]");
	}
	
	/**
	 * 用户登出通知
	 * @param socket
	 * @return
	 * @throws ServerException 
	 */
	private JsonResult userLogout(SocketVo socket) throws ServerException {
		if(StringUtils.isEmpty(socket.getUserId())) {
			log.info("id["+socket.getSid()+"]操作码["+socket.getOptCode()+"]中的userId为空");
			throw ServerException.exce(socket.getSid(),"userId不为空");
		}
		try {
			WebSocketServer.removeUserSession(socket.getUserId(),socket.getAppid());
		} catch (Exception e) {
			log.info("用户登录通知操作失败",e);
			throw ServerException.exce(socket.getSid(),"服务器异常");
		}
		
		return JsonResult.ok(socket.getSid(), "操作成功");
	}

	/**
	 * 用户登录通知
	 * @param socket
	 * @return
	 * @throws ServerException 
	 */
	public JsonResult userLogin(SocketVo socket) throws ServerException {
		if(StringUtils.isEmpty(socket.getUserId())) {
			log.info("id["+socket.getSid()+"]操作码["+socket.getOptCode()+"]中的userId为空");
			throw ServerException.exce(socket.getSid(),"userId不为空");
		}
		if(StringUtils.isEmpty(socket.getSessionId())) {
			log.info("id["+socket.getSid()+"]操作码["+socket.getOptCode()+"]中的sessionId为空");
			throw ServerException.exce(socket.getSid(),"sessionId不为空");
		}		
		
		try {
			WebSocketServer.addUserSession(socket.getUserId(), socket.getSessionId(),socket.getAppid());
		} catch (Exception e) {
			log.info("用户登录通知操作失败",e);
			throw ServerException.exce(socket.getSid(),"服务器异常");
		}
		
		return JsonResult.ok(socket.getSid(), "操作成功");
	}
	 
	
}
