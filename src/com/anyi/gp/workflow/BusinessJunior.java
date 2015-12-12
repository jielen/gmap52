/** 
 * Copyright ? 2004 BeiJing UFGOV Software Co. Ltd. 
 * All right reserved. 
 * Jun 27, 2005 Powered By chihongfeng 
 */
package com.anyi.gp.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.anyi.gp.BusinessException;
import com.anyi.gp.TableData;
import com.anyi.gp.bean.BusinessJuniorBean;

/**
 * <p>Title: 业务上/下级接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 <a href='http://www.ufgov.com.cn'>北京用友政务软件有限公司</a></p>
 * <p>Company: UFGOV</p>
 * @author <a href="mailto:chihf@ufgov.com.cn&cc=jungjo@163.com" title="Chi Hongfeng">Luke Chi</a>
 * @version $Id: BusinessJunior.java,v 1.3 2009/07/10 08:36:57 liuxiaoyong Exp $
 */
public class BusinessJunior {
    
    /**
     * 通过页面数据和当前登录人信息，计算对应业务上级
     * @param data
     * @param junior
     * @param junCoCode
     * @param junOrgCode
     * @param junPosiCode
     * @return
     * @throws BusinessException
     */
    public static Set getSuperior(TableData data,String junior, String junCoCode, 
        String junOrgCode, String junPosiCode,String nd) throws BusinessException {
        Set superior = new HashSet();
        List records = filter(data,junCoCode, junOrgCode, junPosiCode,junior,nd);
        for (int i = 0; i < records.size(); i++) {
            BusinessJuniorBean bj = (BusinessJuniorBean) records.get(i);
            superior.addAll(bj.fallbackSuperior(junCoCode, junOrgCode, junPosiCode, junior,nd));
        }
        return superior;
    }   
    
    /**
     * 计算业务上级，返回最优先的上级定义
     * @return
     */
    public static Set getSuperiorByPri(TableData data, String junior,String junCoCode, 
        String junOrgCode, String junPosiCode,String nd) throws BusinessException{
        Set superior = new HashSet();
        List records = filter(data, junCoCode, junOrgCode, junPosiCode,junior,nd);
        if(records.size()>0) {
        //  找出最大优先级的定义
            Collections.sort(records,new BusinessJuniorBean());
            //业务上级可能是多个，而且他们的优先级相同
            //BusinessJuniorBean bj = (BusinessJuniorBean)records.get(0);//排序后，第一个元素就是最优先定义
            //superior = bj.fallbackSuperior(junCoCode, junOrgCode, junPosiCode, junior);
            //纠正上面两行代码
            List arr = getSmalls(records);
            BusinessJuniorBean bj;
            for(int i=0;i<arr.size();i++){
            	bj = (BusinessJuniorBean)records.get(i);//排序后，第一个元素就是最优先定义
                Set tempSuperior = bj.fallbackSuperior(junCoCode, junOrgCode, junPosiCode, junior,nd);
                if(null!=tempSuperior && tempSuperior.size()>0){
                	superior.addAll(tempSuperior);
                }
            }//纠正结束
        }
        return superior;
    }
    /*
     * 找出最小的几个元素
     */
    private static List getSmalls(List arr){
    	List ar = new ArrayList();
    	if(null!=arr){
        	ar.add(arr.get(0));
	    	for(int i=1; i<arr.size();i++){
	    		if(((new BusinessJuniorBean()).compare(arr.get(0),arr.get(i)))==0){
	    			ar.add(arr.get(i));
	    		}
	    	}
    	}
    	return ar;
    }
    /**
     * 通过页面数据和当前登录人信息过滤出所有符合的下级定义
     * @param data
     * @param junior
     * @param junCoCode
     * @param junOrgCode
     * @param junPosiCode
     * @return
     * @throws BusinessException
     */
    public static List filter(TableData data, String junCoCode, 
        String junOrgCode, String junPosiCode,String junior,String nd) throws BusinessException{
        List ret = new ArrayList();
        List records = BusinessJuniorBean.doFilter(junCoCode, junOrgCode, junPosiCode, junior,nd);
        for (int i = 0; i < records.size(); i++) {
            BusinessJuniorBean bj = (BusinessJuniorBean) records.get(i);
            if(isBelow(data, bj))
                ret.add(bj);
        }
        return ret;
    }


    /**
     * 判断业务数据是否符合业务下级定义规则
     * @param entityData 业务数据
     * @param bj         业务下级表中的某条数据
     * @return
     */
    private static boolean isBelow(TableData entityData, BusinessJuniorBean bj) {
        List compoConditions = bj.getCompoConditions();
        boolean isAllow = false;
        //如果页面部件为AS_TEMP（汇总情况），则不考虑条件规则限制，直接由上下级对应关系中找出执行者
        if("AS_TEMP".equals(entityData.getName())) {
           return true;
        }else if (compoConditions.size()==0) {
            //没有定义业务规则
            return true;
        }
        else {
        //否则判断是否符合业务规则
            for (Iterator it = compoConditions.iterator(); it.hasNext();) {
                BusinessJuniorBean.CompoCondition cc = (BusinessJuniorBean.CompoCondition) it.next();
                BusinessJuniorExp exp = null;
                if (cc.getFieldCode() != null) {
                	//有field字段
                	exp = new BusinessJuniorExp(cc.getCompoCode() + "." + cc.getFieldCode(), cc
                    .getVal(), cc.getSymbol());
                } else {
                	//只是部件名:add by liubo
                	exp = new BusinessJuniorExp(cc.getCompoCode(), cc.getVal(), cc.getSymbol());
                }
                //解析数据，是否符合业务下级选择规则
                //每条规则的表达式间是与的关系，有一个表达式不符合，就表明不符合整条规则
                if (!exp.parse(entityData)) 
                    return false;
                isAllow = true;
            }
            if(isAllow)
                return true;
        }
        return false;
    }
}
