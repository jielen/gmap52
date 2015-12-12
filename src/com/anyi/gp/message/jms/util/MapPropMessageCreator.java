package com.anyi.gp.message.jms.util;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.Assert;

public class MapPropMessageCreator implements MessageCreator {
	private Object message = null;
	
	private Map prop = null;
	
	public MapPropMessageCreator(){};
	
	public MapPropMessageCreator(Object message, Map properties) {
		this.message = message;
		this.prop = properties;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public Map getProp() {
		return prop;
	}

	public void setProp(Map prop) {
		this.prop = prop;
	}

	public Message createMessage(Session session) throws JMSException {
		SimpleMessageConverter convert = new SimpleMessageConverter();
		Assert.notNull(this.message, "消息不能为空!");
		Message mes = convert.toMessage(this.message, session);
		MessageUtils.addMessagePropery(mes, prop);
		return mes;
	}
}
