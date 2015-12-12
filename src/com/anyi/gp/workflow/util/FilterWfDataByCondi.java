/** $Id: FilterWfDataByCondi.java,v 1.1 2008/03/24 03:55:44 liubo Exp $*/
package com.anyi.gp.workflow.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.anyi.gp.Delta;
import com.anyi.gp.TableData;
import com.anyi.gp.util.StringTools;


/**
 * @author   leidaohong
 */
public class FilterWfDataByCondi{
  private Delta wfData;
  private String condition;
  private String[] andOr = {" and ", " or "};
  private String[] andOrSign = {"&&", "||"};
  private String[] compareSign = {"=", "<>", " not like ", " like ", " is "};

  public FilterWfDataByCondi(Delta wfData, String condition){
    this.wfData = wfData;
    this.condition = condition;
  }

  /**
   * ��������condition���˹���������wfData
   */
  public void filteredWfData(){
    if(condition == null || condition.trim().length() == 0){
      return;
    }
    String tempCondi = null;
    tempCondi = StringTools.replaceText(condition, andOr[0], andOrSign[0]);
    tempCondi = StringTools.replaceText(tempCondi, andOr[1], andOrSign[1]);
    tempCondi = StringTools.replaceText(tempCondi, "(", " ");
    tempCondi = StringTools.replaceText(tempCondi, ")", " ");

    Iterator IWfData = wfData.iterator();
    while(IWfData.hasNext()){
      TableData eachRowData = (TableData)IWfData.next();
      if(!verifyEachRowdDataByCondi(eachRowData, tempCondi)){
        IWfData.remove();
      }
    }
  }

  /**
   * �����ʽ�����ַ������еķ��Ų��
   * @param expression Ҫ��ֵı��ʽ
   * @param splitSign ��ַ���
   * @return
   */
  private List parseExp(String expression, String splitSign[]){
    List result = new ArrayList();
    expression = expression.trim();
    int expLen = expression.length();
    int splitSignLength = 0;
    int index = 0;
    String tempExp = null;

    while(index >= 0){
      int tempIndex = 0;
      index = expLen;
      for(int i = 0; i < splitSign.length; i++){
        tempIndex = expression.indexOf(splitSign[i]);
        if(tempIndex >= 0 && tempIndex < index){
          index = tempIndex;
          splitSignLength = splitSign[i].length();
        }
      }

      if(index == expLen){
        index = -1;
      }
      switch(index){
        case -1:
          tempExp = expression.trim();
          if(tempExp.length() > 0){
            result.add(tempExp);
          }
          break;

        case 0:
          tempExp = expression.substring(0, splitSignLength).trim();
          expression = expression.substring(splitSignLength).trim();
          if(tempExp.length() > 0){
            result.add(tempExp);
          }
          break;

        default:
          tempExp = expression.substring(0, index).trim();
          if(tempExp.length() > 0){
            result.add(tempExp);
          }
          tempExp = expression.substring(index, index + splitSignLength).trim();
          if(tempExp.length() > 0){
            result.add(tempExp);
          }
          expression = expression.substring(index + splitSignLength).trim();
          break;
      }
    }
    return result;
  }


  /**
   * ��֤һ�������Ƿ���������
   * @param eachRowData һ������
   * @param condition ����
   * @return
   */
  private boolean verifyEachRowdDataByCondi(TableData eachRowData, String condition){
    boolean result = false;
    boolean firstBool = false;
    boolean secondBool = false;
    int index = 0;
    List splitList = parseExp(condition, andOrSign);
    String linkSign = "@@";
    int splitListSize = splitList.size();
    while(index < splitListSize){
      String splitContent = (String)splitList.get(index);
      index++;
      if("&&".equals(splitContent)){
        if(linkSign.equals("&&")){
          if(result){
            firstBool = computeBool(firstBool, linkSign, secondBool);
            result = firstBool;
          }
        } else if(linkSign.equals("||")){
          if(result){
            return result;
          }
          firstBool = computeBool(firstBool, linkSign, secondBool);
          result = firstBool;
        }
        linkSign = "&&";
      } else if("||".equals(splitContent)){
        if(linkSign.equals("&&")){
          if(result){
            firstBool = computeBool(firstBool, linkSign, secondBool);
            result = firstBool;
          }
        } else if(linkSign.equals("||")){
          if(result){
            return result;
          }
          firstBool = computeBool(firstBool, linkSign, secondBool);
          result = firstBool;
        }
        linkSign = "||";
      } else if(linkSign.equals("@@")){
        firstBool = verifyEachRowdDataBySplitCondi(eachRowData, splitContent);
        result = firstBool;
      } else{
        secondBool = verifyEachRowdDataBySplitCondi(eachRowData, splitContent);
      }
    }
    result = computeBool(firstBool, linkSign, secondBool);
    return result;
  }

  /**
   * ��֤һ�������Ƿ������and����or���ӵ������в�ֵ�һ����
   * @param eachRowData һ������
   * @param splitCondi ��ֵ�����
   * @return
   */
  public boolean verifyEachRowdDataBySplitCondi(TableData eachRowData,
      String splitCondi){
    boolean result = true;
    if(splitCondi == null || splitCondi.trim().length() == 0){
      return result;
    }
    List splitList = parseExp(splitCondi, compareSign);
    if(splitList.size() == 0){
      return result;
    }
    if(splitList.size() == 3){
      String firstPart = (String)splitList.get(0);
      firstPart = getFieldValue(firstPart, eachRowData);
      String linkSign = (String)splitList.get(1);
      String secondPart = (String)splitList.get(2);
      result = verifyRowDataByDiffCompSign(firstPart, linkSign, secondPart);
    }
    return result;
  }

  /**
   * ���ڲ�ͬ�ıȽϷ��Ž����ж�
   * @param leftValue �ȽϷ�����ߵ�ֵ
   * @param linkSign �ȽϷ���
   * @param rightValue �ȽϷ����ұߵ�ֵ
   * @return
   */
  private boolean verifyRowDataByDiffCompSign(String leftValue, String linkSign,
      String rightValue){
    boolean result = false;
    rightValue = StringTools.replaceText(rightValue, "'", "");
    if(linkSign.equals(compareSign[0])){ //=
      result = leftValue.equals(rightValue);
    } else if(linkSign.equals(compareSign[1])){ //<>
      result = !(leftValue.equals(rightValue));
    } else if(linkSign.equals(compareSign[2].trim())){ // not like
      String cmpRightValue = rightValue.substring(1, rightValue.length() - 1);
      result = leftValue.indexOf(cmpRightValue) < 0;
    }

    else if(linkSign.equals(compareSign[3].trim())){ // like
      int rightValueSize = rightValue.length();
      String cmpRightValue = "";
      char[] rightValueArr = rightValue.toCharArray();
      if(rightValueArr[0] == '%' && rightValueArr[rightValueSize - 1] == '%'){ //%cpp%
        cmpRightValue = rightValue.substring(1, rightValueSize - 1);
        result = leftValue.indexOf(cmpRightValue) >= 0;
      } else if(rightValueArr[0] == '%' && rightValueArr[rightValueSize - 1] != '%'){ //%cpp
        cmpRightValue = rightValue.substring(1);
        result = leftValue.endsWith(cmpRightValue);
      } else if(rightValueArr[0] != '%' && rightValueArr[rightValueSize - 1] == '%'){ //cpp%
        cmpRightValue = rightValue.substring(0, rightValueSize - 1);
        result = leftValue.startsWith(cmpRightValue);
      }

    }

    else if(linkSign.equals(compareSign[4].trim())){ // is
      result = leftValue.equals("");
    }

    return result;
  }

  /**
   * ��һ�������еõ�ĳ���ֶε�ֵ
   * @param field �ֶ�
   * @param eachRowData һ������
   * @return
   */
  private String getFieldValue(String field, TableData eachRowData){
    String result = "";
    int dotIndex = field.indexOf(".");
    field = field.substring(dotIndex + 1);
    field = field.trim();
    result = (String)eachRowData.getField(field);
    if(result == null){
      result = "";
    }
    return result;
  }

  /**
   * ����and����or���ӵ�bool������ֵ�õ����
   * @param firstBool ���ӷ���ǰ���bool������ֵ;
   * @param linkOperator ���ӷ���,and����or��
   * @param secondBool ���ӷ��ź����bool������ֵ��
   * @return
   */
  private boolean computeBool(boolean firstBool, String linkOperator,
                              boolean secondBool){
    if("&&".equals(linkOperator)){
      firstBool = firstBool && secondBool;
    } else if("||".equals(linkOperator)){
      firstBool = firstBool || secondBool;
    }
    return firstBool;
  }

}