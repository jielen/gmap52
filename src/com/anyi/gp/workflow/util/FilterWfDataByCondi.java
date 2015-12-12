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
   * 根据条件condition过滤工作流数据wfData
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
   * 将表达式按照字符数组中的符号拆分
   * @param expression 要拆分的表达式
   * @param splitSign 拆分符号
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
   * 验证一行数据是否满足条件
   * @param eachRowData 一行数据
   * @param condition 条件
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
   * 验证一行数据是否满足从and或者or连接的条件中拆分的一部分
   * @param eachRowData 一行数据
   * @param splitCondi 拆分的条件
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
   * 对于不同的比较符号进行判断
   * @param leftValue 比较符号左边的值
   * @param linkSign 比较符号
   * @param rightValue 比较符号右边的值
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
   * 从一行数据中得到某个字段的值
   * @param field 字段
   * @param eachRowData 一行数据
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
   * 根据and或者or连接的bool变量的值得到结果
   * @param firstBool 连接符号前面的bool变量的值;
   * @param linkOperator 连接符号,and或者or；
   * @param secondBool 连接符号后面的bool变量的值；
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