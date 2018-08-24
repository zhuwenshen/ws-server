package com.zhuwenshen.ws;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableRabbit
public class WsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsServerApplication.class, args);
	}
}
