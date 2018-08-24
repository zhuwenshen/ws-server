package com.zhuwenshen.ws.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

//	@Value("${spring.rabbitmq.username}")
//	String username;
//	@Value("${spring.rabbitmq.password}")
//	String password;
//	@Value("${spring.rabbitmq.port}")
//	int port;
//	@Value("${spring.rabbitmq.host}")
//	String host;
	
	//websocket信息队列名
	@Value("${websocket.socket.queue}")
	String socketQueuegName;
	@Value("${websocket.socket.exchange}")
	String socketExchangeName;
	@Value("${websocket.socket.key}")
	String socketKeyName;
//	@Value("${websocket.socket.virtualHost}")
//	String socketHostName;
	
	@Bean(name="socketQueue")
    public Queue socketQueue() {
		Queue queue = new Queue(socketQueuegName, true, false, true);		
        return queue;
    }
	
    @Bean(name="socketExchange")
    TopicExchange exchange() {
        return new TopicExchange(socketExchangeName);
    } 
//  
/*    @Bean
    public ConnectionFactory socketConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(socketHostName);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }*/
 
/*    @Bean("socketRabbitTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate socketRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(socketConnectionFactory());
        return template;
    } 
*/
    /**
     * 将队列socketQueue 与exchange绑定
     * @param queueMessage
     * @param exchange
     * @return
     */
    @Bean
    Binding bindingExchangeMessage(@Qualifier("socketQueue")Queue socketQueue) {
        return BindingBuilder.bind(socketQueue).to(exchange()).with(socketKeyName);
    }    
}
