package com.zhuwenshen.ws.exception;

import com.zhuwenshen.ws.util.MySid;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ServerException extends Exception{

	private static final long serialVersionUID = 667037043882657266L;
	
	private String sid;
	
	public ServerException(String message, String sid) {
		super(message);
		this.sid = sid;
	}
	
	public static ServerException exce(String message) {
		return new ServerException(message, MySid.next());
	}	
	
	public static ServerException exce(String message , String sid) {
		return new ServerException(message,sid);
	}

	
}
