package com.anyi.gp.message.jms;
import javax.jms.MapMessage;

import com.anyi.gp.message.jms.util.SpringUtils;


public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TCJLODO Auto-generated method stub
		try {
			//DefaultMessageListenerContainer container = (DefaultMessageListenerContainer)SpringUtils.getBean("listenerContainer");
			//container.setMessageSelector("userName='Αυ²¨'");
			//container.start();
			JmsMAO mao = (JmsMAO)SpringUtils.getBean("jmsMAO");
			MapMessage message = mao.receiverMapMessage("NewCommitQueue", null);
			System.out.println(message.getShort("entityData"));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
