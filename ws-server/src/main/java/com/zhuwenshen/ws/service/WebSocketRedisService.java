package com.zhuwenshen.ws.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zhuwenshen.ws.dao.Redis;
import com.zhuwenshen.ws.model.DataRedisVo;
import com.zhuwenshen.ws.model.DataVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebSocketRedisService {	
	
	@Autowired
	Redis redis;

	public void addJsonData(String clientKey, String data) {
		if(data == null) {
			return;
		}
		DataVo dataVo = null;
		try {
			dataVo = JSON.parseObject(data, DataVo.class);
		} catch (Exception e) {
			log.info("强制转换成SocketVO时出现异常", e);
			return;
		}
		//已过期
		if(dataVo== null || (dataVo.getExpired()!=-1&&dataVo.getExpired()<System.currentTimeMillis())) {
			return;
		}
		String redisKey = createRedisDataKey(clientKey);
		DataRedisVo dataRedis = (DataRedisVo) redis.getObject(redisKey, DataRedisVo.class, false);
		List<String> dataList = null;
		if(dataRedis == null) {
			dataList = new ArrayList<>();
			dataRedis = new DataRedisVo(clientKey, dataVo.getExpired(), dataList);
		}else{
			
			dataList = dataRedis.getList();
			if(dataList==null) {
				dataList = new ArrayList<>();
				dataRedis.setList(dataList);
			}
			
			if(dataRedis.getMaxExpired()!=-1) {
				if(dataRedis.getMaxExpired()<System.currentTimeMillis()) {
					dataList.clear();
				}else {
					if(dataVo.getExpired()==-1) {
						dataRedis.setMaxExpired(-1L);
					}else {
						dataRedis.setMaxExpired(dataRedis.getMaxExpired()>dataVo.getExpired()?dataRedis.getMaxExpired():dataVo.getExpired());
					}
				}
			}
			
		}
		dataList.add(data);
		
		redis.setObject(redisKey, dataRedis, dataRedis.getMaxExpired());
	}

	/**
	 * 根据clientKey获取信息
	 * @param clientKey
	 * @return
	 */
	public List<String> getJsonDataList(String clientKey) {
		String redisKey = createRedisDataKey(clientKey);
		DataRedisVo dataRedis = (DataRedisVo) redis.getObject(redisKey, DataRedisVo.class, false);
		redis.delete(redisKey);
		if(dataRedis == null) {
			return new ArrayList<>();
		}
		
		if(dataRedis.getMaxExpired()!=-1&&dataRedis.getMaxExpired()<System.currentTimeMillis()) {
			return new ArrayList<>();
		}			
		
		return dataRedis.getList();
	}

	private String createRedisDataKey(String clientKey) {
		return Redis.CACH_PREFIX+clientKey;
	}
}
