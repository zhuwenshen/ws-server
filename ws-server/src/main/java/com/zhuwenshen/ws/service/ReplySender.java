package com.zhuwenshen.ws.service;

//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSON;
//import com.zhuwenshen.ws.model.JsonResult;
//
//import lombok.extern.slf4j.Slf4j;

//@Component
//@Slf4j
public class ReplySender {

//	// 交换机名
//	@Value("#{websocket.rabbitmq.direct-exchange}")
//	String exchangeName;
//
//	// websocket回复 队列名
//	@Value("#{websocket.rabbitmq.reply-queue}")
//	String replyName;
//
//	@Autowired
//	private AmqpTemplate template;
//
//	public void sendJsonResult(JsonResult json) {
//		template.convertAndSend(exchangeName, replyName, JSON.toJSONString(json));
//		log.info("交换机["+exchangeName+"]-->路由key["+replyName+"]-->信息["+json+"]-->发送成功");
//	}
}
