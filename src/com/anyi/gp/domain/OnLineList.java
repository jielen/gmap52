package com.anyi.gp.domain;

import java.io.*;
import java.util.*;

import com.anyi.gp.BusinessException;

/**
 *
 * <p>Title: 在线人数统计</p>
 *
 * <p>Description: 用于基于Java的Web应用的在线用户统计(也可以做为计数器使用)</p>
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
      文件说明：
      1. 由于需要保存用户key和data，因此，使用map来做为存放data的容器
      2. 用户的key对应的data可能是任意类型，因此本类中使用Object
      3. 底层容器可能会根据需要进行调整，如：HashMap、Hashtable或其他实现map接口的类
      4. 由于业务逻辑限制，该类只能有一个实例存在，本模块将使用单例模式
     */

    private static final long serialVersionUID = 4184307279919853423L;

    /**
     * OnLineList 唯一实例
     */
    private static OnLineList onLineList;

    /**
     * 返回OnLineList例
     * @return OnLineList
     */
    public static synchronized OnLineList getInstance() {
        if (onLineList == null)
            onLineList = new OnLineList();
        return onLineList;
    }

    private OnLineList() {
        //不能采用 new 实例化
    }

    private int hashValue = 0;


    /*#com.ufgov.online.OnLineException Dependency20*/

    /**
     * 用于保存key地址和data
     */
    private Map map = new HashMap();

    /**
     * 添加一个data到容器，如果指定的key已存在，将会覆盖
     * @param key String
     * @param data Object
     */
    public synchronized void add(String key, Object data) {
        map.put(key, data);
    }

    /**
     * 从容器中删除一个用户
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
     * 返回key迭代器，用于遍历key
     * @return Iterator
     */
    public Iterator keyIterator() {
        return map.keySet().iterator();
    }

    /**
     * 根据key返回key对应的data
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
     * 返回在线人数
     * @return int
     */
    public int Count() {
        return map.size();
    }

    public void clear() {
        map.clear();
    }

    /**
     * 返回指定key是否存在
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
 * 类使用说明：
 * 1. 在服务器启动时将实例设置为application的一个attribute。可以使用ServletContextListener监听，如：
 *    sce.getServletContext().setAttribute(ON_LINE_LIST_KEY,OnLineList.getInstance());
 * 2. 用户登录成功后将session id，登录时间加入到OnlineList；如：OnLineList.getInstance().add(req.getRemoteAddr(), new Date());
 * 3. 当用户退出登录时Session Destroy时将对应的key以及data从 OnLineList 删除；如：OnLineList.getInstance().remove(req.getRemoteAddr());
 */
