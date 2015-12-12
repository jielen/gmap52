package com.anyi.gp.message.jms.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jms.core.SessionCallback;
import org.springframework.jms.core.support.JmsGatewaySupport;
import org.springframework.jndi.JndiLookupFailureException;
import org.springframework.jndi.JndiTemplate;
import org.springframework.util.Assert;

import com.anyi.gp.message.jms.JmsMAO;
import com.anyi.gp.message.jms.util.MapPropMessageCreator;
import com.anyi.gp.message.jms.util.MessageUtils;

public class BaseJmsMAOImpl extends JmsGatewaySupport implements JmsMAO {
	
	private static Logger logger = Logger.getLogger(BaseJmsMAOImpl.class);

	private JndiTemplate jndiTemplate = null;

	/**
	 * @return the jndiTemplate
	 */
	public JndiTemplate getJndiTemplate() {
		return jndiTemplate;
	}

	/**
	 * @param jndiTemplate
	 *            the jndiTemplate to set
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiTemplate = jndiTemplate;
	}

	public Object execute(SessionCallback action) {
		// TCJLODO Auto-generated method stub
		return this.getJmsTemplate().execute(action);
	}

	public Object execute(ProducerCallback action) {
		// TCJLODO Auto-generated method stub
		return this.getJmsTemplate().execute(action);
	}

	public void sendMessage(String destName, MessageCreator messageCreator) {
		try {
			// 在weblogic 8.x中未提供动态创建destination的功能，故通过jndi获取
			Destination des = (Destination) jndiTemplate.lookup(destName);
			this.getJmsTemplate().send(des, messageCreator);
		} catch (NamingException ex) {
			logger.error(ex);
			throw new JndiLookupFailureException(destName + " 寻找错误！", ex);
		}
	}

	private void sendMessage(String destName, Object content, Map props) {
		sendMessage(destName, new MapPropMessageCreator(content, props));
	}

	public void sendBytesMessage(String destName, Object[] content, Map props) {
		// TCJLODO Auto-generated method stub
		try {
			byte[] stream = MessageUtils.getDataStream(content);
			sendMessage(destName, stream, null);
		} catch (IOException ex) {
			logger.error(ex);
			throw new RuntimeException(ex);
		}
	}

	public void sendBytesMessage(String destName, byte[] content, Map props) {
		sendMessage(destName, content, props);
	}

	public void sendMapMessage(String destName, Map content, Map props) {
		// TCJLODO Auto-generated method stub
		sendMessage(destName, content, props);
	}

	public void sendObjectMessage(String destName, Object content, Map props) {
		// TCJLODO Auto-generated method stub
		sendMessage(destName, content, props);
	}

	public void sendTextMessage(String destName, String content, Map props) {
		// TCJLODO Auto-generated method stub
		sendMessage(destName, content, props);
	}

	public Message receiverMessage(String destName, String selector) {
		Message mes = null;
		try {
			Destination des = (Destination) jndiTemplate.lookup(destName);
			if (selector == null) {
				mes = this.getJmsTemplate().receive(des);
			} else {
				mes = this.getJmsTemplate().receiveSelected(des, selector);
			}
			return mes;
		} catch (NamingException ex) {
			logger.error(ex);
			throw new JndiLookupFailureException(destName + " 寻找错误！", ex);
		}
	}

	public BytesMessage receiverBytesMessage(String destName, String selector) {
		// TCJLODO Auto-generated method stub
		Message mes = receiverMessage(destName, selector);
		return (BytesMessage) mes;
	}

	public MapMessage receiverMapMessage(String destName, String selector) {
		// TCJLODO Auto-generated method stub
		Message mes = receiverMessage(destName, selector);
		return (MapMessage) mes;
	}

	public ObjectMessage receiverObjectMessage(String destName, String selector) {
		// TCJLODO Auto-generated method stub
		Message mes = receiverMessage(destName, selector);
		return (ObjectMessage) mes;
	}

	public TextMessage receiverTextMessage(String destName, String selector) {
		// TCJLODO Auto-generated method stub
		Message mes = receiverMessage(destName, selector);
		return (TextMessage) mes;
	}

	public List browserQueue(final String destName, final String selector) {
		// TCJLODO Auto-generated method stub
		try {
			final Destination des = (Destination) jndiTemplate.lookup(destName);
			Assert.isInstanceOf(Queue.class, des, "主题不能浏览消息!");
			return (List) this.execute(new SessionCallback() {
				public Object doInJms(Session session) throws JMSException {
					QueueBrowser browser = null;
					List messages = new ArrayList();
					if (selector != null) {
						browser = session.createBrowser((Queue) des, selector);
					} else {
						browser = session.createBrowser((Queue) des);
					}
					Enumeration enu = browser.getEnumeration();
					while (enu.hasMoreElements()) {
						messages.add(enu.nextElement());
					}
					browser.close();
					return messages;
				}
			});
		} catch (NamingException ex) {
			logger.error(ex);
			throw new JndiLookupFailureException(destName + " 寻找错误！", ex);
		}
	}

}
