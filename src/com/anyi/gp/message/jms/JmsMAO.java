package com.anyi.gp.message.jms;

import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.core.SessionCallback;

public interface JmsMAO {
	public Object execute(SessionCallback action);

	public Object execute(final ProducerCallback action);
	
	//通过spring的MessageCreator发送消息
	public void sendMessage(String destName, MessageCreator messageCreator);

	//发送文本消息
	public void sendTextMessage(String destName, String content, Map props);

	//发送可序列化对象消息
	public void sendObjectMessage(String destName, Object content, Map props);

	//发送字节流消息
	public void sendBytesMessage(String destName, Object[] content, Map props);

	public void sendBytesMessage(String destName, byte[] content, Map props);

	//发送哈希表消息
	public void sendMapMessage(String destName, Map content, Map props);
	//接受消息
	public Message receiverMessage(String destName, String selector);
	//接受文本消息
	public TextMessage receiverTextMessage(String destName, String selector);
	//接收可序列化对象消息
	public ObjectMessage receiverObjectMessage(String destName, String selector);
	//接收字节流消息
	public BytesMessage receiverBytesMessage(String destName, String selector);
	//接收哈希对象消息
	public MapMessage receiverMapMessage(String destName, String selector);
	
	public List browserQueue(String destName, String select);
	
}
