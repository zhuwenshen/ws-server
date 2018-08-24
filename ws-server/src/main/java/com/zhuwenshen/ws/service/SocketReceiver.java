package com.zhuwenshen.ws.service;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhuwenshen.ws.exception.ServerException;

import lombok.extern.slf4j.Slf4j;

@Component // "${websocket.socket.queue}"}
// 监听器监听指定的Queue
@Slf4j
/*
 * @RabbitListener(bindings = {
 * 
 * @QueueBinding(value = @Queue( value = "${websocket.socket.queue}", durable =
 * "true", autoDelete = "true", exclusive = "false"), exchange = @Exchange(name
 * = "${websocket.socket.exchange}",type=ExchangeTypes.DIRECT), key =
 * "websocket.socket.1" ) })
 */
public class SocketReceiver {

	@Autowired
	ServerService serverService;

	// 处理rabbit接收到的内容
	// @RabbitHandler(isDefault = true)
	/*
	 * @RabbitListener(bindings = {
	 * 
	 * @QueueBinding(value = @Queue( value = "${websocket.socket.queue}", durable =
	 * "true", autoDelete = "true", exclusive = "false"), exchange = @Exchange(name
	 * = "${websocket.socket.exchange}",type=ExchangeTypes.DIRECT) ) })
	 */
	@RabbitListener(queues = "${websocket.socket.queue}")
	public void process(byte[] body) {
		String json = SerializationUtils.deserialize(body);
		// System.out.println(json);

		try {
			serverService.doService(json , false);
		} catch (ServerException e) {
			log.info("消息处理发生异常：" + e.getMessage() + ",消息内容为：" + json);
		}catch(Exception e) {
			log.info("消息处理发生重大错误：" + e.getMessage() + ",消息内容为：" + json,e);
		}

	}
}
