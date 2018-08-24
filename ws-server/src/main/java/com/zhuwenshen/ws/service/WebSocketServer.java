package com.zhuwenshen.ws.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.zhuwenshen.ws.model.DataVo;
import com.zhuwenshen.ws.util.MySid;
import com.zhuwenshen.ws.util.SpringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ServerEndpoint("/websocket/{appid}/{userId}/{sessionId}")
@Component
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static volatile int onlineCount = 0;
    //concurrent包的线程安全map，用来存放每个客户端对应的MyWebSocket对象。
    private static ConcurrentHashMap<String, WebSocketServer> clients = new ConcurrentHashMap<String,WebSocketServer>(); 
    //存放已登录信息，用于校验
    private static ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<String,String>();
    //待分发通知
    //private static ConcurrentHashMap<String, List<String>> massgeMap = new ConcurrentHashMap<String,List<String>>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;    
	//@SuppressWarnings("unused")
    //private String userId;
    private String sessionId;
    //@SuppressWarnings("unused")
	private String appid;
    private String clientKey;
    
 
    /**
     * 连接建立成功调用的方法
     * 
     * */
    @OnOpen
    public void onOpen(@PathParam("sessionId")String sessionId,@PathParam("userId")String userId,@PathParam("appid")String appid,Session session) {
    	this.session = session;
    	clientKey = createClientKey(userId, appid);
    	String seId = userSessionMap.get(clientKey);
    	if(seId == null||!seId.equals(sessionId)) {
    		try {
    			//this.userId = userId;
    			String succMsg = new DataVo(MySid.next(), System.currentTimeMillis(), 2000002, "连接失败，无验证", 0L).toString();
            	 
				sendMessage(succMsg);
				session.close();
			} catch (IOException e) {
				log.error("websocket IO异常");
				return;
			}
    	}else {    	
	    	//this.userId = userId;
	    	this.appid = appid;
	    	this.sessionId = sessionId;
	        clients.put(clientKey, this);     //加入map中
	        addOnlineCount();           //在线数加1
	        log.info("新连接-->appid["+appid+"]-->userId["+userId+"]-->sessionId["+sessionId+"]-->在线总人数["+getOnlineCount()+"]");       
	        try {
	        	 String succMsg = new DataVo(MySid.next(), System.currentTimeMillis(), 2000001, "连接成功",0L).toString();
	        	 
	        	 sendMessage(succMsg);
	        	 
	        	 //发送已经等待的信息
	        	 //获取以等待消息	
	        	//接入redis
	        	 List<String> dataList = SpringUtil.getBean(WebSocketRedisService.class).getJsonDataList(clientKey);
	        	// List<String> dataList = massgeMap.get(clientKey);
	        	 if(dataList!=null) {
	        		 for(String msg:dataList) {
	        			 sendMessage(msg);
	        		 }
	        	 }
	        } catch (IOException e) {
	            log.error("websocket IO异常");
	        }
    	}
    }	
 
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
    	if(sessionId == null) {
    		return;
    	}else {
	    	clients.remove(clientKey);  //从set中删除
	    	userSessionMap.remove(clientKey);
	        subOnlineCount();           //在线数减1
	        log.info("连接关闭-->clientKey["+clientKey+"]-->sessionId["+sessionId+"]-->在线总人数["+getOnlineCount()+"]");  
    	}
    }
 
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
    	
    }
 
	/**
	 * 
	 * @param session
	 * @param error
	 */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误",error);
        if(!session.isOpen()) {
        	clients.remove(this.clientKey);
        	log.info("移除了客户端["+this.clientKey+"]["+this.sessionId+"]");
        }
    }
 
    /**
     * 给客户端发送信息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        log.info("给clientKey["+clientKey+"]发送信息成功-->信息原文："+message);
    }
 
    
    public void sendMessageTo(String message, String To) throws IOException {         
         
    }  
    
    /**
     * 给所有客户端发送同一信息
     * @param message
     * @throws IOException
     */
    public static int sendMessageAll(String message){  
    	int num = 0;
    	for (String key : clients.keySet()) {
            try {
            	clients.get(key).sendMessage(message);
            	num++;
            } catch (Exception e) {
                log.info("给所有用户推送-->clientKey["+key+"]-->推送["+message+"]-->失败");
            }
        } 
    	return num;
    }  
    
    /**
     * 给一个appid的客户端发送同一信息
     * @param message
     * @throws IOException
     */
    public static int sendMessageAll(String message, String appid){  
    	WebSocketServer server = null;
    	int num = 0;
    	for (String key : clients.keySet()) {
            try {
            	server = clients.get(key);
            	if(server.appid!=null&&server.appid.equals(appid)) {
            		server.sendMessage(message);
            		num++;
            	}
            } catch (Exception e) {
            	log.info("给所有["+appid+"]用户推送-->clientKey["+key+"]-->推送["+message+"]-->失败");
            }
        }  
    	return num;
    } 
 
    /**
     * 获取当前在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    /**
     * 增加一个在线人数
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
 
    /**
     * 减少一个在线人数
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
    
    /**
     * 获取所有客户端
     * @return
     */
    public static synchronized ConcurrentHashMap<String, WebSocketServer> getClients() {  
        return clients;  
    }  
    
    /**
     * 根据clientKey获取客户端
     * @param clientKey
     * @return
     */
    public static WebSocketServer getClientByClientKey(String clientKey) { 
    	if(clientKey == null) {
    		return null;
    	}        
       return clients.get(clientKey);        
    }   
    
    /**
     * 添加一个session验证
     * @param userId
     * @param sessionId
     * @param appid
     */
    public static void addUserSession(String userId, String sessionId, String appid) {
    	String key = createClientKey(userId, appid);
    	userSessionMap.put(key, sessionId);
    	log.info("加入userSessionMap:clientKey["+key+"],sessionId["+sessionId+"]");
    }
    
    /**
     * 移除一个session验证
     * @param userId
     * @param appid
     */
    public static void removeUserSession(String userId, String appid) {
    	String key = createClientKey(userId, appid);
    	userSessionMap.remove(key);
    	log.info("移除userSessionMap:clientKey["+key+"]");
    }
    
    /**
     * 发送信息
     * @param keyList
     * @param data
     * @return
     */
    public static List<String> sandMessage(Set<String> keyList , String data) {
    	List<String> failure = new ArrayList<String>();    
    	for (String key : keyList) {
    		WebSocketServer client = getClientByClientKey(key);
    		if(client == null) {
    			log.info("clientKey["+key+"]暂无连接");
    			failure.add("clientKey["+key+"]暂无连接");
    			//加入到暂时推送队列    	
    			addWaitSendMsg(key, data);
    			continue;
    		}
    		try {
				client.sendMessage(data);
			} catch (IOException e) {				
				log.info("clientKey["+key+"]发送消息["+data+"]异常");
				addWaitSendMsg(key, data);
				failure.add("clientKey["+key+"]发送消息["+data+"]异常");
			}    		
		}
    	
    	return failure;
    }
    
    /**
     * 加入暂时推送系列
     * @param userId
     * @param data
     */
    public static void addWaitSendMsg(String key, String data) {
    	if(key == null || data == null) {
    		return;
    	}
    	//无redis模块
    	/*List<String> dataList = null;    	
    	if(massgeMap.containsKey(key)) {
    		dataList = massgeMap.get(key);
    	}
    	if(dataList == null) {
    		dataList = new ArrayList<>();
    		massgeMap.put(key, dataList);
    	}
    	
    	dataList.add(data);*/
    	
    	//接入redis
    	SpringUtil.getBean(WebSocketRedisService.class).addJsonData(key, data);
    }    
    
    /**
     * 创建一个clientKey-->userId-appid
     * @param userId
     * @param appid
     * @return
     */
    public static String createClientKey(String userId, String appid) {
    	return userId +"-"+appid;
    }
    
    /**
     * 打印当前websocket主要信息
     */
    public static void info() {
    	log.info("已连接用户,数量["+onlineCount+"]");		
		for(String key :clients.keySet()) {
			log.info("userId:"+key);
		}
		log.info("已登录用户,数量["+userSessionMap.keySet().size()+"]");		
		for(String key :userSessionMap.keySet()) {
			log.info("clientKey["+key+"],sessionId["+userSessionMap.get(key)+"]");
		}
		/*log.info("未通知信息,数量["+massgeMap.keySet().size()+"]");		
		for(String key :massgeMap.keySet()) {
			log.info("clientKey["+key+"],data["+userSessionMap.get(key)+"]");
		}*/
    }
    
}

