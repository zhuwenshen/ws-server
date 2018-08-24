package com.zhuwenshen.ws;

import com.alibaba.fastjson.JSON;
import com.zhuwenshen.ws.model.SocketRequestVo;
import com.zhuwenshen.ws.model.SocketVo;
import com.zhuwenshen.ws.util.MySid;

/**
 * 双重json测试
 * @author zhuwenshen
 * @time 2018年8月20日上午9:53:52
 */
public class JsonTest {

	public static void main(String[] args) {
		SocketRequestVo request = new SocketRequestVo();
		request.setAppid("rabbit");
		request.setAppkey("123456");
		SocketVo socket = new SocketVo();
		socket.setOptCode(2001001);
		socket.setSessionId("165d46a5d3a1da3");
		socket.setParam("dada");
		socket.setT(System.currentTimeMillis());
		socket.setSid(MySid.next());
		socket.setJsonData(JSON.toJSONString(request));
		request.setSocket(socket);
		
		System.out.println(JSON.toJSON(request));
		
		String json =  JSON.toJSON(request).toString();
		System.out.println(JSON.parseObject(json, SocketRequestVo.class));
		/*SocketVo vo2 = JSON.parseObject(json, SocketVo.class);
		String data = vo2.getJsonData();
		System.out.println(data);
		System.out.println(JSON.parseObject(data, SocketRequestVo.class));*/
		
	}
}
