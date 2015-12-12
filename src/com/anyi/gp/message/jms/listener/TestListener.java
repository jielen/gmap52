package com.anyi.gp.message.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TestListener implements MessageListener {

	public void onMessage(Message mes) {
		// TCJLODO Auto-generated method stub
		try {
			TextMessage tm = (TextMessage) mes;
			Thread.sleep(10000);
			System.out.println("content is : " + tm.getText());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
