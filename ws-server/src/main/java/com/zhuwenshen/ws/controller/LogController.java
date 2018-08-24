package com.zhuwenshen.ws.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuwenshen.ws.service.WebSocketServer;

@Controller
public class LogController {	
	 
	 @RequestMapping(value="/log",method=RequestMethod.GET)
	 @ResponseBody
	 public  String list(String password) {		 
		if("123456789".equals(password)) {
			WebSocketServer.info();			
		}		 
		return "请查看日志";
	 }

}
