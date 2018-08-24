package com.zhuwenshen.ws.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.zhuwenshen.ws.model.DataVo;
import com.zhuwenshen.ws.model.JsonResult;
import com.zhuwenshen.ws.model.SocketVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NoticeService {

	/**
	 * 通知客户端
	 * 
	 * @param socket
	 * @return
	 */
	public JsonResult notice(SocketVo socket) {

		if ((socket.getExpired() != -1&&socket.getExpired() != 0) && System.currentTimeMillis() > socket.getExpired()) {
			return JsonResult.fail(socket.getSid(), "消息已过期");
		}

		// 根据推送类型推送
		switch (socket.getType()) {
		case "1":
			return sendToAllOnline(socket);
		case "2":
			return sendToAllAppOnline(socket);
		case "3":
			return sendToMore(socket);

		default:
			return JsonResult.fail(socket.getSid(), "推送类型["+socket.getType()+"]不作处理");
		}

	}

	/**
	 * 给所有的用户推送
	 * @param socket
	 * @return
	 */
	private JsonResult sendToAllOnline(SocketVo socket) {
		Long start = System.currentTimeMillis();
		int num = 	WebSocketServer.sendMessageAll(DataVo.getDataVo(socket).toString());
		log.info("给所有用户推送-->-->推送["+socket+"]-->成功推送["+num+"]人-->耗时["+(System.currentTimeMillis()-start)+"]毫秒");
		return JsonResult.ok(socket.getSid(), "成功推送["+num+"]");
	}

	/**
	 * 给登录appid的推送
	 * @param socket
	 * @return
	 */
	private JsonResult sendToAllAppOnline(SocketVo socket) {
		Long start = System.currentTimeMillis();
		int num = 	WebSocketServer.sendMessageAll(DataVo.getDataVo(socket).toString(), socket.getAppid());
		log.info("给app["+socket.getAppid()+"]所有用户推送-->-->推送["+socket+"]-->成功推送["+num+"]人-->耗时["+(System.currentTimeMillis()-start)+"]毫秒");
		return JsonResult.ok(socket.getSid(), "成功推送["+num+"]");
	}

	/**
	 * 推送指定人
	 * @param socket
	 * @return
	 */
	public JsonResult sendToMore(SocketVo socket) {
		// 提取clientKey
		Set<String> keySet = new HashSet<>();
		if (StringUtils.isNotEmpty(socket.getUserId())) {
			keySet.add(WebSocketServer.createClientKey(socket.getUserId(), socket.getAppid()));
		}

		List<String> list = socket.getUserIdList();
		if (list != null && !list.isEmpty()) {
			for (String userId : list) {
				keySet.add(WebSocketServer.createClientKey(userId, socket.getAppid()));
			}
		}

		List<String> failure = WebSocketServer.sandMessage(keySet, DataVo.getDataVo(socket).toString());
		if (failure == null || failure.isEmpty()) {
			return JsonResult.ok(socket.getSid(), "通知全部发送成功");
		}

		String result = StringUtils.join(failure, ";");

		return JsonResult.ok(socket.getSid(), result);
	}
}
