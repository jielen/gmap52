package com.anyi.gp.domain;

import java.io.*;
import java.util.*;

import com.anyi.gp.BusinessException;

/**
 *
 * <p>Title: ��������ͳ��</p>
 *
 * <p>Description: ���ڻ���Java��WebӦ�õ������û�ͳ��(Ҳ������Ϊ������ʹ��)</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: ufgov</p>
 *
 * @author manlge
 * @version 1.0
 */


public class OnLineList implements Serializable {
    /*
      �ļ�˵����
      1. ������Ҫ�����û�key��data����ˣ�ʹ��map����Ϊ���data������
      2. �û���key��Ӧ��data�������������ͣ���˱�����ʹ��Object
      3. �ײ��������ܻ������Ҫ���е������磺HashMap��Hashtable������ʵ��map�ӿڵ���
      4. ����ҵ���߼����ƣ�����ֻ����һ��ʵ�����ڣ���ģ�齫ʹ�õ���ģʽ
     */

    private static final long serialVersionUID = 4184307279919853423L;

    /**
     * OnLineList Ψһʵ��
     */
    private static OnLineList onLineList;

    /**
     * ����OnLineList��
     * @return OnLineList
     */
    public static synchronized OnLineList getInstance() {
        if (onLineList == null)
            onLineList = new OnLineList();
        return onLineList;
    }

    private OnLineList() {
        //���ܲ��� new ʵ����
    }

    private int hashValue = 0;


    /*#com.ufgov.online.OnLineException Dependency20*/

    /**
     * ���ڱ���key��ַ��data
     */
    private Map map = new HashMap();

    /**
     * ���һ��data�����������ָ����key�Ѵ��ڣ����Ḳ��
     * @param key String
     * @param data Object
     */
    public synchronized void add(String key, Object data) {
        map.put(key, data);
    }

    /**
     * ��������ɾ��һ���û�
     * @param key String
     */
    public synchronized void remove(String key) throws BusinessException {
        if (map.containsKey(key)) {
            map.remove(key);
        } else {
            ////throw new BusinessException("key " + key + " not exists");
        }
    }

    /**
     * ����key�����������ڱ���key
     * @return Iterator
     */
    public Iterator keyIterator() {
        return map.keySet().iterator();
    }

    /**
     * ����key����key��Ӧ��data
     * @param key String
     * @return Object
     */
    public synchronized Object get(String key) throws BusinessException {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            throw new BusinessException("key " + key + " not exists");
        }
    }

    /**
     * ������������
     * @return int
     */
    public int Count() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    /**
     * ����ָ��key�Ƿ����
     * @param key String
     * @return boolean
     */
    public boolean exists(String key) {
        return map.containsKey(key);
    }

    public String toString() {
        synchronized (this) {
            StringBuffer buff = new StringBuffer();
            for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                Object data = map.get(key);
                buff.append(",");
                buff.append(key);
                buff.append(" ");
                buff.append(data);
            }
            String result = buff.toString();
            if (result.startsWith(","))
                return result.substring(1);
            return result.trim();
        }
    }

    public boolean equals(Object anObject) {
        if (anObject == null)
            return false;
        if (this == anObject)
            return true;
        if (!(anObject instanceof OnLineList))
            return false;
        OnLineList that = (OnLineList) anObject;
        return that.map.equals(this.map);
    }

    public int hashCode() {
        if (this.hashValue == 0) {
            int result = 17;
            int idValue = this.map.hashCode();
            result = result * 37 + idValue;
            this.hashValue = result;
        }
        return this.hashValue;
    }



    public static void main(String[] args) {
        OnLineList o = OnLineList.getInstance();
        o.add("192.168.1.1", new Date().toLocaleString());
        o.add("192.168.1.1", new Date().toLocaleString());
        System.out.println(o.toString() + " Count: " + o.Count());
    }

}


/**
 * ��ʹ��˵����
 * 1. �ڷ���������ʱ��ʵ������Ϊapplication��һ��attribute������ʹ��ServletContextListener�������磺
 *    sce.getServletContext().setAttribute(ON_LINE_LIST_KEY,OnLineList.getInstance());
 * 2. �û���¼�ɹ���session id����¼ʱ����뵽OnlineList���磺OnLineList.getInstance().add(req.getRemoteAddr(), new Date());
 * 3. ���û��˳���¼ʱSession Destroyʱ����Ӧ��key�Լ�data�� OnLineList ɾ�����磺OnLineList.getInstance().remove(req.getRemoteAddr());
 */
