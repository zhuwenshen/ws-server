package com.zhuwenshen.ws.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataRedisVo implements Serializable{

	private static final long serialVersionUID = -8369123072462143921L;
	private String clientKey;
	private Long maxExpired;
	private List<String> list;
}
