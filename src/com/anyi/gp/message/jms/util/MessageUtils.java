package com.anyi.gp.message.jms.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;

public class MessageUtils {
	/*
	 * 添加message的客户自定义属性
	 */
	public static void addMessagePropery(Message mes, Map prop)
			throws JMSException {
		if (prop != null) {
			Iterator iterator = prop.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				mes.setStringProperty(entry.getKey().toString(), entry
						.getValue().toString());
			}
		}
	}
	/*
	 * 将用户自定义的数据组转成byte流
	 */
	public static byte[] getDataStream(Object[] datas) throws IOException {
		DataOutputStream out = null;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			out = new DataOutputStream(buffer);
			for (int i = 0; i < datas.length; i++) {
				Object temp = datas[i];
				if (temp instanceof Boolean) {
					out.writeBoolean(((Boolean) temp).booleanValue());
				} else if (temp instanceof Byte) {
					out.writeByte(((Byte) temp).byteValue());
				} else if (temp instanceof Short) {
					out.writeByte(((Short) temp).shortValue());
				} else if (temp instanceof Integer) {
					out.writeInt(((Integer) temp).intValue());
				} else if (temp instanceof Long) {
					out.writeLong(((Long) temp).longValue());
				} else if (temp instanceof Float) {
					out.writeFloat(((Float) temp).floatValue());
				} else if (temp instanceof Double) {
					out.writeDouble(((Double) temp).doubleValue());
				} else if (temp instanceof String) {
					out.writeUTF(temp.toString());
				}
			}
			return buffer.toByteArray();
		} finally {
			out.close();
		}
	}
}
