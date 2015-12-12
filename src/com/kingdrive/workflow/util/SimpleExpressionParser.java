package com.kingdrive.workflow.util;

import java.util.ArrayList;
import java.util.List;

import com.kingdrive.framework.util.Debug;

public class SimpleExpressionParser {

  private static final String ARITHMETIC[] = { "+", "-", "*", "/", "%" };

  private static final String COMPARISON[] = { ">=", "<=", "==", "!=", ">", "<" };
  
  private static final String STR_COMPARISON[] = { "==", "!=", "nlike", "like"};

  private static final String SHORT_CIRCUIT[] = { "&&", "||" };

  private static final char LEFT_PARENTHESIS = 40;

  private static final char RIGHT_PARENTHESIS = 41;

  private SimpleExpressionParser() {
  }

  private static double arithmeticCompute(double firstParameter, char operator,
      double secondParameter) {
    switch (operator) {
    case 43: // '+'
      firstParameter += secondParameter;
      break;

    case 45: // '-'
      firstParameter -= secondParameter;
      break;

    case 42: // '*'
      firstParameter *= secondParameter;
      break;

    case 47: // '/'
      firstParameter /= secondParameter;
      break;

    case 37: // '%'
      firstParameter %= secondParameter;
      break;
    }
    return firstParameter;
  }

  public static double arithmeticExpressionParse(String expression) {
    double value = 0.0D;
    int leftPos = 0;
    int rightPos = 0;
    if (expression == null || expression.trim().length() == 0)
      return value;
    while ((leftPos = expression.lastIndexOf(LEFT_PARENTHESIS)) > -1
        && (rightPos = expression.indexOf(RIGHT_PARENTHESIS, leftPos)) > -1) {
      String tempExpression = expression.substring(leftPos + 1, rightPos);
      expression = String.valueOf((new StringBuffer(expression.substring(0,
          leftPos))).append(simpleArithmeticExpressionParse(tempExpression))
          .append(expression.substring(rightPos + 1)));
    }
    value = simpleArithmeticExpressionParse(expression);
    return value;
  }

  private static boolean booleanCompute(boolean firstParameter,
      String operator, boolean secondParameter) {
    if ("&&".equals(operator))
      firstParameter = firstParameter && secondParameter;
    else if ("||".equals(operator))
      firstParameter = firstParameter || secondParameter;
    else if ("&".equals(operator))
      firstParameter &= secondParameter;
    else if ("|".equals(operator))
      firstParameter |= secondParameter;
    return firstParameter;
  }

  public static boolean compareExpressionParse(String expression) {
    boolean result = false;
    float flag;
    if (expression == null)
      return result;
    for (; expression.startsWith("(") && expression.endsWith(")"); expression = expression
        .substring(1, expression.length() - 1))
      ;
    if (expression.trim().length() == 0)
      return result;
    List list = glossaryParse(expression, COMPARISON);
    if (list.size() == 0)
      return result;
    if (list.size() == 3) {
      // if(isStringTypeExpression((String)list.get(0)))return
      
      String firstParameter = (String) list.get(0).toString();
      String operator = (String) list.get(1).toString();
      String secondParameter = (String) list.get(2).toString();
      if (">=".equals(operator)){
       flag = Float.parseFloat(firstParameter) - Float.parseFloat(secondParameter);//firstParameter.compareToIgnoreCase(secondParameter);
       if(flag>=0)
         result = true;
       else
         result = false;
      }
      else if (">".equals(operator)){
        flag = Float.parseFloat(firstParameter) - Float.parseFloat(secondParameter);//firstParameter.compareToIgnoreCase(secondParameter);
        if(flag>0)
          result = true;
        else
          result = false;
      }
      else if ("<".equals(operator)){
        flag = Float.parseFloat(firstParameter) - Float.parseFloat(secondParameter);//firstParameter.compareToIgnoreCase(secondParameter);
        if(flag<0)
          result = true;
        else
          result = false;        
      }
      else if ("<=".equals(operator)){
        flag = Float.parseFloat(firstParameter) - Float.parseFloat(secondParameter);//firstParameter.compareToIgnoreCase(secondParameter);
        if(flag<=0)
          result = true;
        else
          result = false;        
      }
      else if ("==".equals(operator)){
        result = firstParameter.equals(secondParameter);         
      }
      else if ("!=".equals(operator))
        result = !(firstParameter.equals(secondParameter));
    } else if ("true".equals(list.get(0)))
      result = true;
    return result;
  }

  private static List glossaryParse(String expression, String operators[]) {
    List list = null;
    int index = 0;
    String temp = null;
    int MAX_POS = expression.length();
    int operatorLength = 0;
    list = new ArrayList();
    expression = expression.trim();
    do {
      int tempIndex = 0;
      index = MAX_POS;
      for (int i = 0; i < operators.length; i++) {
        tempIndex = expression.indexOf(operators[i]);
        if (tempIndex >= 0 && tempIndex < index) {
          index = tempIndex;
          operatorLength = operators[i].length();
          break;
        }
      }

      if (index == MAX_POS)
        index = -1;
      switch (index) {
      case -1:
        temp = expression.trim();
        if (temp.length() > 0)
          list.add(temp);
        break;

      case 0: // '\0'
        temp = expression.substring(0, operatorLength).trim();
        expression = expression.substring(operatorLength).trim();
        if (temp.length() > 0)
          list.add(temp);
        break;

      default:
        temp = expression.substring(0, index).trim();
        if (temp.length() > 0)
          list.add(temp);
        temp = expression.substring(index, index + operatorLength).trim();
        if (temp.length() > 0)
          list.add(temp);
        expression = expression.substring(index + operatorLength).trim();
        break;
      }
    } while (index >= 0);
    return list;
  }

  public static void main(String args[]) {
    //Debug
    //    .println(shortCircuitExpressionParse("((((3 - 1) != 1&&false||true)))||false&&false"));
    //Debug.println(compareExpressionParse("(((3>1)))"));
    Debug.println(shortCircuitExpressionParse("('301' nlike 30106) || ('30106' == 30106)"));
    System.exit(0);
  }

  public static boolean shortCircuitExpressionParse(String expression) {
    boolean value = false;
    int leftPos = 0;
    int rightPos = 0;  
    String tempExp ;  
    if (expression == null || expression.trim().length() == 0)
      return value;
    while ((leftPos = expression.lastIndexOf(LEFT_PARENTHESIS)) > -1
        && (rightPos = expression.indexOf(RIGHT_PARENTHESIS, leftPos)) > -1) {
      int tempIndex = -1;
      boolean nonShortCircuitOperator = true;
      String tempExpression = expression.substring(leftPos + 1, rightPos);
      for (int i = 0; i < SHORT_CIRCUIT.length; i++) {
        tempIndex = tempExpression.indexOf(SHORT_CIRCUIT[i]);
        nonShortCircuitOperator &= tempIndex < 0;
      }

      if (nonShortCircuitOperator && tempExpression.indexOf("true") < 0
          && tempExpression.indexOf("false") < 0){
        tempExp = expression.substring(0,leftPos);
        boolean calc = false;
        if (isStringCompareExpression(tempExpression))
          calc = compareStringExpressionParse(tempExpression);
        else
          calc = compareExpressionParse(tempExpression);

        tempExp += calc + "";
        tempExp += expression.substring(rightPos + 1);
        expression = tempExp;
        /*expression = String.valueOf((new StringBuffer(expression.substring(0,
            leftPos))).append(simpleArithmeticExpressionParse(tempExpression))
            .append(expression.substring(rightPos + 1)));*/
      }else{
        tempExp = expression.substring(0,leftPos);
        tempExp += simpleShortCircuitExpressionParse(tempExpression);
        tempExp += expression.substring(rightPos + 1);
        expression = tempExp;
        /*expression = String.valueOf((new StringBuffer(expression.substring(0,
            leftPos)))
            .append(simpleShortCircuitExpressionParse(tempExpression)).append(
                expression.substring(rightPos + 1)));*/
      }
    }
    value = simpleShortCircuitExpressionParse(expression);
    return value;
  }

  private static double simpleArithmeticExpressionParse(String expression) {
    int index = 0;
    double value = 0.0D;
    double firstParameter = 0.0D;
    double secondParameter = 0.0D;
    double thirdParameter = 0.0D;
    List list = glossaryParse(expression, ARITHMETIC);
    char firstOperator = '@';
    char secondOperator = '@';
    do
      do {
        if (index < list.size()) {
          String temp = (String) list.get(index++);
          char tempOperator = temp.charAt(0);
          switch (tempOperator) {
          case 43: // '+'
            if (secondOperator != '@') {
              secondParameter = arithmeticCompute(secondParameter,
                  secondOperator, thirdParameter);
              thirdParameter = 0.0D;
              secondOperator = '@';
            }
            if (firstOperator != '@') {
              firstParameter = arithmeticCompute(firstParameter, firstOperator,
                  secondParameter);
              secondParameter = 0.0D;
            }
            firstOperator = '+';
            continue;

          case 45: // '-'
            if (secondOperator != '@') {
              secondParameter = arithmeticCompute(secondParameter,
                  secondOperator, thirdParameter);
              thirdParameter = 0.0D;
              secondOperator = '@';
            }
            if (firstOperator != '@') {
              firstParameter = arithmeticCompute(firstParameter, firstOperator,
                  secondParameter);
              secondParameter = 0.0D;
            }
            firstOperator = '-';
            continue;

          case 42: // '*'
            switch (firstOperator) {
            case 37: // '%'
            case 42: // '*'
            case 47: // '/'
              firstParameter = arithmeticCompute(firstParameter, firstOperator,
                  secondParameter);
              secondParameter = 0.0D;
              firstOperator = '*';
              break;

            case 43: // '+'
            case 45: // '-'
              if (secondOperator != '@') {
                secondParameter = arithmeticCompute(secondParameter,
                    secondOperator, thirdParameter);
                thirdParameter = 0.0D;
              }
              secondOperator = '*';
              break;

            case 38: // '&'
            case 39: // '\''
            case LEFT_PARENTHESIS: // '('
            case RIGHT_PARENTHESIS: // ')'
            case 44: // ','
            case 46: // '.'
            default:
              firstOperator = '*';
              break;
            }
            break;

          case 47: // '/'
            switch (firstOperator) {
            case 37: // '%'
            case 42: // '*'
            case 47: // '/'
              firstParameter = arithmeticCompute(firstParameter, firstOperator,
                  secondParameter);
              secondParameter = 0.0D;
              firstOperator = '/';
              break;

            case 43: // '+'
            case 45: // '-'
              if (secondOperator != '@') {
                secondParameter = arithmeticCompute(secondParameter,
                    secondOperator, thirdParameter);
                thirdParameter = 0.0D;
              }
              secondOperator = '/';
              break;

            case 38: // '&'
            case 39: // '\''
            case LEFT_PARENTHESIS: // '('
            case RIGHT_PARENTHESIS: // ')'
            case 44: // ','
            case 46: // '.'
            default:
              firstOperator = '/';
              break;
            }
            break;

          case 37: // '%'
            switch (firstOperator) {
            case 37: // '%'
            case 42: // '*'
            case 47: // '/'
              firstParameter = arithmeticCompute(firstParameter, firstOperator,
                  secondParameter);
              secondParameter = 0.0D;
              firstOperator = '%';
              break;

            case 43: // '+'
            case 45: // '-'
              if (secondOperator != '@') {
                secondParameter = arithmeticCompute(secondParameter,
                    secondOperator, thirdParameter);
                thirdParameter = 0.0D;
              }
              secondOperator = '%';
              break;

            case 38: // '&'
            case 39: // '\''
            case LEFT_PARENTHESIS: // '('
            case RIGHT_PARENTHESIS: // ')'
            case 44: // ','
            case 46: // '.'
            default:
              firstOperator = '%';
              break;
            }
            break;

          case 38: // '&'
          case 39: // '\''
          case LEFT_PARENTHESIS: // '('
          case RIGHT_PARENTHESIS: // ')'
          case 44: // ','
          case 46: // '.'
          default:
            if (firstOperator != '@') {
              if (secondOperator != '@')
                thirdParameter = Double.parseDouble(temp);
              else
                secondParameter = Double.parseDouble(temp);
            } else {
              firstParameter = Double.parseDouble(temp);
            }
            break;
          }
        } else {
          value = arithmeticCompute(secondParameter, secondOperator,
              thirdParameter);
          value = arithmeticCompute(firstParameter, firstOperator, value);
          return value;
        }
        break;
      } while (true); while (true);
  }

  private static boolean simpleShortCircuitExpressionParse(String expression) {
    boolean result = false;
    boolean firstParameter = false;
    boolean secondParameter = false;
    int index = 0;
    List list = glossaryParse(expression, SHORT_CIRCUIT);
    String firstOperator = "@@";
    while (index < list.size()) {
      String temp = (String) list.get(index++);
      if ("&&".equals(temp)) {
        if (firstOperator.equals("&&")) {
          if (result) {
            firstParameter = booleanCompute(firstParameter, firstOperator,
                secondParameter);
            result = firstParameter;
          }
        } else if (firstOperator.equals("||")) {
          if (result)
            return result;
          firstParameter = booleanCompute(firstParameter, firstOperator,
              secondParameter);
          result = firstParameter;
        }
        firstOperator = "&&";
      } else if ("||".equals(temp)) {
        if (firstOperator.equals("&&")) {
          if (result) {
            firstParameter = booleanCompute(firstParameter, firstOperator,
                secondParameter);
            result = firstParameter;
          }
        } else if (firstOperator.equals("||")) {
          if (result)
            return result;
          firstParameter = booleanCompute(firstParameter, firstOperator,
              secondParameter);
          result = firstParameter;
        }
        firstOperator = "||";
      } else if (firstOperator.equals("@@")) {
        if (isStringCompareExpression(temp))
          firstParameter = compareStringExpressionParse(temp);
        else
          firstParameter = compareExpressionParse(temp);

        result = firstParameter;
      } else {
        secondParameter = compareExpressionParse(temp);
      }
    }
    result = booleanCompute(firstParameter, firstOperator, secondParameter);
    return result;
  }

  private static boolean compareStringExpressionParse(String expression) {
    boolean result = false;
    List list = glossaryParse(expression, STR_COMPARISON);
    if (list.size() == 0)
      return result;
    if (list.size() == 3) {
      String tmp = (String) list.get(0);
      tmp = tmp.replaceAll("\"","");
      tmp = tmp.replaceAll("'","");
      list.set(0,tmp);
      if(null==list.get(2)){
        result = false;        
      }else if (((String) list.get(1)).equals("==")){
        result = ((String) list.get(0)).equals(list.get(2));
      }else if (((String) list.get(1)).equals("!=")){
        result = !((String) list.get(0)).equals(list.get(2));
      }else if (((String) list.get(1)).equals("nlike")){
        result = ! disposeLike((String) list.get(2), (String)list.get(0));   
      }else if (((String) list.get(1)).equals("like")){
        result = disposeLike((String) list.get(2), (String)list.get(0));              
      }else{        
        throw new RuntimeException("字符串类型的表达式只支持==,!=,like,nlike操作符！");
      }
    }
    return result;
  }
  private static boolean disposeLike(String source, String strLike){
    boolean result = false;
    if(source!=null && strLike!=null){
      int i = strLike.indexOf("%");
      System.out.println(i+"");
      if(i==-1){////such as: ABC
        result = source.indexOf(strLike)>-1; 
      }else if(i==0 && strLike.length()>1){//such as: %ABC
        int j = source.indexOf(strLike.substring(1,strLike.length()));
        result = j>-1 && (j==(source.length()-strLike.length()+1)); 
      }else if(i==strLike.length()-1 && strLike.length()>1){//such as: ABC%
        result = source.indexOf(strLike.substring(0, strLike.length()-1))==0; 
      }//else false
    }
    return result;
  }
  private static boolean isStringCompareExpression(String expression) {
    // TCJLODO Auto-generated method stub
    return expression.startsWith("'") || expression.startsWith("\"");
  }

}
