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
	
	//ͨ��spring��MessageCreator������Ϣ
	public void sendMessage(String destName, MessageCreator messageCreator);

	//�����ı���Ϣ
	public void sendTextMessage(String destName, String content, Map props);

	//���Ϳ����л�������Ϣ
	public void sendObjectMessage(String destName, Object content, Map props);

	//�����ֽ�����Ϣ
	public void sendBytesMessage(String destName, Object[] content, Map props);

	public void sendBytesMessage(String destName, byte[] content, Map props);

	//���͹�ϣ����Ϣ
	public void sendMapMessage(String destName, Map content, Map props);
	//������Ϣ
	public Message receiverMessage(String destName, String selector);
	//�����ı���Ϣ
	public TextMessage receiverTextMessage(String destName, String selector);
	//���տ����л�������Ϣ
	public ObjectMessage receiverObjectMessage(String destName, String selector);
	//�����ֽ�����Ϣ
	public BytesMessage receiverBytesMessage(String destName, String selector);
	//���չ�ϣ������Ϣ
	public MapMessage receiverMapMessage(String destName, String selector);
	
	public List browserQueue(String destName, String select);
	
}
