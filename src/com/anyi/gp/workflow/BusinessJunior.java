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
 * <p>Title: ҵ����/�¼��ӿ�</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 <a href='http://www.ufgov.com.cn'>������������������޹�˾</a></p>
 * <p>Company: UFGOV</p>
 * @author <a href="mailto:chihf@ufgov.com.cn&cc=jungjo@163.com" title="Chi Hongfeng">Luke Chi</a>
 * @version $Id: BusinessJunior.java,v 1.3 2009/07/10 08:36:57 liuxiaoyong Exp $
 */
public class BusinessJunior {
    
    /**
     * ͨ��ҳ�����ݺ͵�ǰ��¼����Ϣ�������Ӧҵ���ϼ�
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
     * ����ҵ���ϼ������������ȵ��ϼ�����
     * @return
     */
    public static Set getSuperiorByPri(TableData data, String junior,String junCoCode, 
        String junOrgCode, String junPosiCode,String nd) throws BusinessException{
        Set superior = new HashSet();
        List records = filter(data, junCoCode, junOrgCode, junPosiCode,junior,nd);
        if(records.size()>0) {
        //  �ҳ�������ȼ��Ķ���
            Collections.sort(records,new BusinessJuniorBean());
            //ҵ���ϼ������Ƕ�����������ǵ����ȼ���ͬ
            //BusinessJuniorBean bj = (BusinessJuniorBean)records.get(0);//����󣬵�һ��Ԫ�ؾ��������ȶ���
            //superior = bj.fallbackSuperior(junCoCode, junOrgCode, junPosiCode, junior);
            //�����������д���
            List arr = getSmalls(records);
            BusinessJuniorBean bj;
            for(int i=0;i<arr.size();i++){
            	bj = (BusinessJuniorBean)records.get(i);//����󣬵�һ��Ԫ�ؾ��������ȶ���
                Set tempSuperior = bj.fallbackSuperior(junCoCode, junOrgCode, junPosiCode, junior,nd);
                if(null!=tempSuperior && tempSuperior.size()>0){
                	superior.addAll(tempSuperior);
                }
            }//��������
        }
        return superior;
    }
    /*
     * �ҳ���С�ļ���Ԫ��
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
     * ͨ��ҳ�����ݺ͵�ǰ��¼����Ϣ���˳����з��ϵ��¼�����
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
     * �ж�ҵ�������Ƿ����ҵ���¼��������
     * @param entityData ҵ������
     * @param bj         ҵ���¼����е�ĳ������
     * @return
     */
    private static boolean isBelow(TableData entityData, BusinessJuniorBean bj) {
        List compoConditions = bj.getCompoConditions();
        boolean isAllow = false;
        //���ҳ�沿��ΪAS_TEMP��������������򲻿��������������ƣ�ֱ�������¼���Ӧ��ϵ���ҳ�ִ����
        if("AS_TEMP".equals(entityData.getName())) {
           return true;
        }else if (compoConditions.size()==0) {
            //û�ж���ҵ�����
            return true;
        }
        else {
        //�����ж��Ƿ����ҵ�����
            for (Iterator it = compoConditions.iterator(); it.hasNext();) {
                BusinessJuniorBean.CompoCondition cc = (BusinessJuniorBean.CompoCondition) it.next();
                BusinessJuniorExp exp = null;
                if (cc.getFieldCode() != null) {
                	//��field�ֶ�
                	exp = new BusinessJuniorExp(cc.getCompoCode() + "." + cc.getFieldCode(), cc
                    .getVal(), cc.getSymbol());
                } else {
                	//ֻ�ǲ�����:add by liubo
                	exp = new BusinessJuniorExp(cc.getCompoCode(), cc.getVal(), cc.getSymbol());
                }
                //�������ݣ��Ƿ����ҵ���¼�ѡ�����
                //ÿ������ı��ʽ������Ĺ�ϵ����һ�����ʽ�����ϣ��ͱ�����������������
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
