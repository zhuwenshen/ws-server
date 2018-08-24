package com.zhuwenshen.ws.dao;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;

@Component
public class Redis {

	// 永久保存对象
	public static Integer PERMANENT_TIME = -1;
	// 消息对象前缀
	public static String CACH_PREFIX = "ws-data-";

	@Autowired
	private Jedis jedis;

	/**
	 * 获取key的value值
	 * 
	 * @param key
	 * @return
	 */
	private String get(String key) {
		String str = null;
		
		str = jedis.get(key);
		
		return str;
	}

	/**
	 * 保存一个值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private String set(String key, String value) {		
		return  jedis.set(key, value);		
	}

	/**	 
	 * 
	 * @param key
	 * @param value
	 * @param timeout 时间戳
	 */
	private String set(String key, Long milliseconds, String value) {		
		return jedis.psetex(key, milliseconds, value);		
	}

	/**
	 * 实现命令：expire 设置过期时间，单位秒
	 * 
	 * @param key
	 * @return
	 */
	public void expire(String key, Long unixTime) {
		jedis.expireAt(key, unixTime);
	}

	/**
	 * 保存一个对象
	 * 
	 * @param key
	 * @param data
	 * @param seconds
	 *            小于等于0为永久保存
	 */
	public void setObject(String key, Object data, Long unixTime) {
		if (unixTime <= 0) {
			set(key.toString(), JSON.toJSONString(data));
		} else {
			set(key.toString(), unixTime, JSON.toJSONString(data));
		}

	}

	/**
	 * 获取一个对象
	 * 
	 * @param key
	 * @param clazz
	 * @param isList
	 * @return
	 */
	public Object getObject(String key, Class<?> clazz, Boolean isList) {
		String data = get(key.toString());
		Object t = null;
		try {
			if (!StringUtils.isEmpty(data)) {
				if (isList) {
					t = JSON.parseArray(data, clazz);
				} else {
					t = JSON.parseObject(data, clazz);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return t;
	}

	
	/**
	 * 删除一个对象
	 * 
	 * @param key
	 */
	public void delete(String key) {
		jedis.del(key);
	}

	/**
	 * 批量删除对象
	 * 
	 * @param key
	 */
	public void deleteBatch(String pattern) {
		try {
			Set<String> keys = jedis.keys(pattern);
			for (String key : keys) {
				jedis.del(key);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Set<String> key(String pattern) {
		Set<String> keys = null;
		try {
			keys = jedis.keys(pattern);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return keys;
	}

}
