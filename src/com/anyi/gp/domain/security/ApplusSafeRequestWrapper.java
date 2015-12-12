package com.anyi.gp.domain.security;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ApplusSafeRequestWrapper extends HttpServletRequestWrapper {

  private Map pattributes = new HashMap();

  public ApplusSafeRequestWrapper(HttpServletRequest arg0) {
    super(arg0);
    this.pattributes.putAll(getParameterMap());
  }

  public String getParameter(String name) {
    Object v = pattributes.get(name);
    String val = "";
    if (v == null)
      val = null;
    else if (v instanceof String[]) {
      String[] strs = (String[]) v;
      for (int i = 0; i < strs.length; i++) {
        val += strs[i];
      }
    } else {
      val = v.toString();
    }
    //System.out.println("before  filter:"+val+ "; name="+name);
    if (val==null || val.length()==0)
      return val;
    val=delHtmlTag(val);
    //System.out.println("before XSS filter:"+val+ "; name="+name);
    if ("condition".equalsIgnoreCase(name))
      val = sqlInjectionCondition(val);
    else if (!"function".equalsIgnoreCase(name)
      || !"componame".equalsIgnoreCase(name)) {
      val = sqlInjectionOther(val);
    }
    //System.out.println("after SQL filter:"+val+ "; name="+name);
    return val;
  }

  public void setParameter(String name, String value) {
    this.pattributes.put(name, value);
  }

  private String delHtmlTag(String inputString) {
    String htmlStr = inputString;
    String textStr = "";
    java.util.regex.Pattern p_script;
    java.util.regex.Matcher m_script;
    java.util.regex.Pattern p_style;
    java.util.regex.Matcher m_style;
    java.util.regex.Pattern p_html;
    java.util.regex.Matcher m_html;

    try {
      String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
      String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
      String regEx_html = "<[^>]+>";
      p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
      m_script = p_script.matcher(htmlStr);
      htmlStr = m_script.replaceAll("");
      p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
      m_style = p_style.matcher(htmlStr);
      htmlStr = m_style.replaceAll("");

      p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
      m_html = p_html.matcher(htmlStr);
      if (!(((htmlStr.toUpperCase().indexOf("<DETAL>") > -1) && (htmlStr
        .toUpperCase().indexOf("</DETAL>") > 0)) || ((htmlStr.toUpperCase().indexOf(
        "<ENTITY") > -1) && (htmlStr.toUpperCase().indexOf("</ENTITY>") > 0))))
        htmlStr = m_html.replaceAll("");

      //  特殊代码过滤，如 接受的参数直接赋给 js 代码中的一个变量时，可以直接加入js的函数，这种会存在 "; 两个字符
      if (htmlStr.indexOf("\"") > 0) {
        String htmlstr1 = htmlStr.substring(htmlStr.indexOf("\"")+1).trim();
        if (htmlstr1.startsWith(";")) {
          htmlStr = htmlStr.substring(0, htmlStr.indexOf("\""));
        } else {
          if (htmlstr1.indexOf("\"") > 0) {
            String htmlstr2 = htmlstr1.substring(htmlStr.indexOf("\"")+1).trim();
            if (htmlstr2.startsWith(";")) {
              htmlStr = htmlStr.substring(0, htmlStr.indexOf(";")).trim();
              htmlStr = htmlStr.substring(0, htmlStr.length() - 1);
            }
          }
        }
      }
      textStr = htmlStr;

    } catch (Exception e) {
      System.err.println("Html2Text: " + e.getMessage());
    }
    return textStr;
  }
  
  private String sqlInjectionCondition(String SqlInjectionStr) {
    String textStr = "";
    if (SqlInjectionStr==null || SqlInjectionStr.length()==0)
      return SqlInjectionStr;
    if (SqlInjectionStr.indexOf("=") > 0) {
      String sqlleft = SqlInjectionStr.substring(0, SqlInjectionStr.indexOf("="))
        .trim();
      String sqlright = SqlInjectionStr.substring(SqlInjectionStr.indexOf("=") + 1)
        .trim();
      //&& sqlright.endsWith("'")
//      if (sqlright.startsWith("'") 
//        && (sqlleft.indexOf("'") < 0)) {
//        textStr = SqlInjectionStr;
        if (sqlright.toUpperCase().indexOf("OR ") > 1
          && sqlright.toUpperCase().indexOf("=") > 1) {
           sqlright = sqlright.substring(
            sqlright.toUpperCase().indexOf("OR ") + 3).trim();
          
          String sqlleftr=sqlright.substring(0, sqlright.indexOf("=")).trim();
          String sqlrightr = sqlright.substring(sqlright.indexOf("=") + 1).trim();
          if (sqlrightr.toUpperCase().indexOf("AND ") > 1) {
            sqlrightr = sqlrightr.substring(0,
              sqlrightr.toUpperCase().indexOf("AND ")).trim();
          }
          if (sqlrightr.toUpperCase().trim().equals(sqlleftr.toUpperCase().trim())) {
            textStr = "";
            return textStr;
          } else {
            if (sqlrightr.toUpperCase().trim()
              .indexOf(sqlleftr.toUpperCase().trim()) > 0
              && sqlrightr.toUpperCase().trim().indexOf("SELECT ") > -1) {
              sqlrightr = sqlrightr.substring(sqlrightr.toUpperCase().trim()
                .indexOf("FROM ") + 5);
              if (sqlrightr.toUpperCase().indexOf("WHERE ") > 0)
                sqlrightr = sqlright.substring(0,
                  sqlrightr.toUpperCase().indexOf("WHERE ")).trim();
              if (sqlrightr.toUpperCase().indexOf("SYS.") > -1
                || sqlrightr.toUpperCase().indexOf("DUAL") > -1
                || sqlrightr.toUpperCase().indexOf("USER_") > -1
                || sqlrightr.toUpperCase().indexOf("DBA_") > -1
                || sqlrightr.toUpperCase().indexOf("ALL_") > -1
                || sqlrightr.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                || sqlrightr.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                || sqlrightr.toUpperCase().indexOf("V$") > -1) {
                textStr = "";
                return textStr;
              } else
                textStr = SqlInjectionStr;
            }
          }

        }

//&& sqlleft.endsWith("'")
//      if (sqlleft.startsWith("'") 
//        && (sqlright.indexOf("'") < 0)) {
//        textStr = SqlInjectionStr;
        if (sqlleft.toUpperCase().indexOf("OR ") > 1
          && sqlleft.toUpperCase().indexOf("=") > 1) {
           sqlleft = sqlleft.substring(
            sqlleft.toUpperCase().indexOf("OR ") + 3).trim();
          String sqlleftl = sqlleft.substring(
            sqlleft.toUpperCase().indexOf("OR ") + 3, sqlleft.indexOf("=")).trim();
          String sqlleftr = sqlleft.substring(sqlleft.indexOf("=") + 1).trim();
          if (sqlleftr.toUpperCase().indexOf("AND ") > 1) {
            sqlleftr = sqlleftr.substring(0, sqlleftr.toUpperCase().indexOf("AND "))
              .trim();
          }
          if (sqlleftl.equals(sqlleftr))
            textStr = "";
          if (sqlleftr.toUpperCase().trim().equals(sqlleftl.toUpperCase().trim())) {
            textStr = "";
            return textStr;
          } else {
            if (sqlleftr.toUpperCase().trim().indexOf(sqlleftl.toUpperCase().trim()) > 0
              && sqlleftr.toUpperCase().trim().indexOf("SELECT ") > -1) {
              sqlleftr = sqlleftr.substring(sqlleftr.toUpperCase().trim().indexOf(
                "FROM ") + 5);
              if (sqlleftr.toUpperCase().indexOf("WHERE ") > 0)
                sqlleftr = sqlright.substring(0,
                  sqlleftr.toUpperCase().indexOf("WHERE ")).trim();
              if (sqlleftr.toUpperCase().indexOf("SYS.") > -1
                || sqlleftr.toUpperCase().indexOf("DUAL") > -1
                || sqlleftr.toUpperCase().indexOf("USER_") > -1
                || sqlleftr.toUpperCase().indexOf("DBA_") > -1
                || sqlleftr.toUpperCase().indexOf("ALL_") > -1
                || sqlleftr.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                || sqlleftr.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                || sqlleftr.toUpperCase().indexOf("V$") > -1) {
                textStr = "";
                return textStr;
              } else
                textStr = SqlInjectionStr;
            }
          }
//        }
      }
      //检查条件中是否含有;（分号），
      if (sqlright.indexOf(";") > 0) {
        sqlright = sqlright.substring(sqlright.indexOf(";") + 1).trim();
        if (sqlright.toUpperCase().startsWith("SELECT ")
          || sqlright.toUpperCase().startsWith("DROP ")
          || sqlright.toUpperCase().startsWith("CREATE ")
          || sqlright.toUpperCase().startsWith("INSERT ")
          || sqlright.toUpperCase().startsWith("UPDATE ")
          || sqlright.toUpperCase().startsWith("DELETE ")
          || sqlright.toUpperCase().startsWith("ALTER ")
          || sqlright.toUpperCase().startsWith("EXEC ")
          || sqlright.toUpperCase().startsWith("CALL ")) {
          textStr = "";
          return textStr;
        } else
          textStr = SqlInjectionStr;
      } else
        textStr = SqlInjectionStr;

      //检查含有 union 情况
      if (sqlright.trim().indexOf("'")>-1) {
        if (sqlright.toUpperCase().indexOf("UNION ") > 0
          && sqlright.toUpperCase().indexOf("SELECT ") > 0
          && sqlright.toUpperCase().indexOf("UNION ") < sqlright.toUpperCase()
            .indexOf("SELECT ")) {
          sqlright = sqlright.substring(sqlright.toUpperCase().indexOf("FROM ") + 5)
            .trim();
          if (sqlright == null) {
            textStr = SqlInjectionStr;
          } else {
            if (sqlright.toUpperCase().indexOf("WHERE ") > 0)
              sqlright = sqlright.substring(0,
                sqlright.toUpperCase().indexOf("WHERE ")).trim();
            if (sqlright.toUpperCase().indexOf("FROM ") > 0
              && sqlright.toUpperCase().indexOf("SELECT ") > 0
              && sqlright.toUpperCase().indexOf("SELECT ") < sqlright.toUpperCase()
                .indexOf("FROM ")) {
              //多个子查询 目前仅支持2个
              String sqlright1 = "";
              if (sqlright.indexOf(")") > 0)
                sqlright1 = sqlright.substring(sqlright.indexOf(")"));
              if (sqlright1.toUpperCase().indexOf("SYS.") > -1
                || sqlright1.toUpperCase().indexOf("DUAL") > -1
                || sqlright1.toUpperCase().indexOf("USER_") > -1
                || sqlright1.toUpperCase().indexOf("DBA_") > -1
                || sqlright1.toUpperCase().indexOf("ALL_") > -1
                || sqlright1.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                || sqlright1.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                || sqlright1.toUpperCase().indexOf("V$") > -1) {
                textStr = "";
                return textStr;
              } else {
                String sqlright2 = sqlright.substring(
                  sqlright.toUpperCase().indexOf("FROM ") + 5).trim();
                if (sqlright2.toUpperCase().indexOf("WHERE ") > 0)
                  sqlright2 = sqlright2.substring(0,
                    sqlright2.toUpperCase().indexOf("WHERE ")).trim();
                if (sqlright2.toUpperCase().indexOf("SYS.") > -1
                  || sqlright2.toUpperCase().indexOf("DUAL") > -1
                  || sqlright2.toUpperCase().indexOf("USER_") > -1
                  || sqlright2.toUpperCase().indexOf("DBA_") > -1
                  || sqlright2.toUpperCase().indexOf("ALL_") > -1
                  || sqlright2.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                  || sqlright2.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                  || sqlright2.toUpperCase().indexOf("V$") > -1) {
                  textStr = "";
                  return textStr;
                } else {
                  textStr = SqlInjectionStr;
                }
              }
            } else {
              if (sqlright.toUpperCase().indexOf("SYS.") > -1
                || sqlright.toUpperCase().indexOf("DUAL") > -1
                || sqlright.toUpperCase().indexOf("USER_") > -1
                || sqlright.toUpperCase().indexOf("DBA_") > -1
                || sqlright.toUpperCase().indexOf("ALL_") > -1
                || sqlright.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                || sqlright.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                || sqlright.toUpperCase().indexOf("V$") > -1) {
                textStr = "";
                return textStr;
              } else
                textStr = SqlInjectionStr;
            }
          }
        }

      }
    }
    return textStr;
  }

  private String sqlInjectionOther(String SqlInjectionStr) {
    //String SqlInjectionStr = inputString;
    if (SqlInjectionStr==null || SqlInjectionStr.length()==0)
      return SqlInjectionStr;
    String textStr = "";
    if (SqlInjectionStr.indexOf("'") > 1
      && SqlInjectionStr.toUpperCase().indexOf("OR ") > 1) {
      if (SqlInjectionStr.indexOf("=") > 0) {
        String sqlleft = SqlInjectionStr.substring(0, SqlInjectionStr.indexOf("="))
          .trim();
        String sqlright = SqlInjectionStr
          .substring(SqlInjectionStr.indexOf("=") + 1).trim();
        String sqlleftr = sqlleft;
        if (sqlleft.toUpperCase().indexOf("OR ") > 1)
          sqlleftr = sqlleft.substring(sqlleft.toUpperCase().indexOf("OR ") + 3).trim();
        String sqlrightr = sqlright.trim();

        if (sqlrightr.toUpperCase().indexOf("AND ") > 1) {
          sqlrightr = sqlrightr
            .substring(0, sqlrightr.toUpperCase().indexOf("AND ")).trim();
        }
        if ((sqlrightr+"'").toUpperCase().trim().equals(sqlleftr.toUpperCase().trim()) || (sqlrightr).toUpperCase().trim().equals(sqlleftr.toUpperCase().trim())) {
          textStr = "";
          return textStr;
        } else {
          if (sqlrightr.toUpperCase().trim().indexOf(sqlleftr.toUpperCase().trim()) > 0
            && sqlrightr.toUpperCase().trim().indexOf("SELECT ") > -1) {
            sqlrightr = sqlrightr.substring(sqlrightr.toUpperCase().trim().indexOf(
              "FROM ") + 5);
            if (sqlrightr.toUpperCase().indexOf("WHERE ") > 0)
              sqlrightr = sqlright.substring(0,
                sqlrightr.toUpperCase().indexOf("WHERE ")).trim();
            if (sqlrightr.toUpperCase().indexOf("SYS.") > -1
              || sqlrightr.toUpperCase().indexOf("DUAL") > -1
              || sqlrightr.toUpperCase().indexOf("USER_") > -1
              || sqlrightr.toUpperCase().indexOf("DBA_") > -1
              || sqlrightr.toUpperCase().indexOf("ALL_") > -1
              || sqlrightr.toUpperCase().indexOf("GLOBAL_NAME ") > -1
              || sqlrightr.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
              || sqlrightr.toUpperCase().indexOf("V$") > -1) {
              textStr = "";
              return textStr;
            } else
              textStr = SqlInjectionStr;
          }
        }
      }
    }
    //  检查条件中是否含有;（分号），
    if (SqlInjectionStr.indexOf(";") > 0) {
      String sqlright = SqlInjectionStr.substring(SqlInjectionStr.indexOf(";") + 1)
        .trim();
      if (sqlright.toUpperCase().startsWith("SELECT ")
        || sqlright.toUpperCase().startsWith("DROP ")
        || sqlright.toUpperCase().startsWith("CREATE ")
        || sqlright.toUpperCase().startsWith("INSERT ")
        || sqlright.toUpperCase().startsWith("UPDATE ")
        || sqlright.toUpperCase().startsWith("DELETE ")
        || sqlright.toUpperCase().startsWith("ALTER ")
        || sqlright.toUpperCase().startsWith("EXEC ")
        || sqlright.toUpperCase().startsWith("CALL ")) {
        textStr = "";
        return textStr;
      } else
        textStr = SqlInjectionStr;
    } else
      textStr = SqlInjectionStr;
//  检查含有 union 情况
    if (SqlInjectionStr.trim().indexOf("'")>-1) {
      if (SqlInjectionStr.toUpperCase().indexOf("UNION ") > 0
        && SqlInjectionStr.toUpperCase().indexOf("SELECT ") > 0
        && SqlInjectionStr.toUpperCase().indexOf("UNION ") < SqlInjectionStr.toUpperCase()
          .indexOf("SELECT ")) {
        String sqlright = SqlInjectionStr.substring(SqlInjectionStr.toUpperCase().indexOf("FROM ") + 5)
          .trim();
        if (sqlright == null) {
          textStr = SqlInjectionStr;
        } else {
          if (sqlright.toUpperCase().indexOf("WHERE ") > 0)
            sqlright = sqlright.substring(0,
              sqlright.toUpperCase().indexOf("WHERE ")).trim();
          if (sqlright.toUpperCase().indexOf("FROM ") > 0
            && sqlright.toUpperCase().indexOf("SELECT ") > 0
            && sqlright.toUpperCase().indexOf("SELECT ") < sqlright.toUpperCase()
              .indexOf("FROM ")) {
            //多个子查询 目前仅支持2个
            String sqlright1 = "";
            if (sqlright.indexOf(")") > 0)
              sqlright1 = sqlright.substring(sqlright.indexOf(")"));
            if (sqlright1.toUpperCase().indexOf("SYS.") > -1
              || sqlright1.toUpperCase().indexOf("DUAL") > -1
              || sqlright1.toUpperCase().indexOf("USER_") > -1
              || sqlright1.toUpperCase().indexOf("DBA_") > -1
              || sqlright1.toUpperCase().indexOf("ALL_") > -1
              || sqlright1.toUpperCase().indexOf("GLOBAL_NAME ") > -1
              || sqlright1.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
              || sqlright1.toUpperCase().indexOf("V$") > -1) {
              textStr = "";
              return textStr;
            } else {
              String sqlright2 = sqlright.substring(
                sqlright.toUpperCase().indexOf("FROM ") + 5).trim();
              if (sqlright2.toUpperCase().indexOf("WHERE ") > 0)
                sqlright2 = sqlright2.substring(0,
                  sqlright2.toUpperCase().indexOf("WHERE ")).trim();
              if (sqlright2.toUpperCase().indexOf("SYS.") > -1
                || sqlright2.toUpperCase().indexOf("DUAL") > -1
                || sqlright2.toUpperCase().indexOf("USER_") > -1
                || sqlright2.toUpperCase().indexOf("DBA_") > -1
                || sqlright2.toUpperCase().indexOf("ALL_") > -1
                || sqlright2.toUpperCase().indexOf("GLOBAL_NAME ") > -1
                || sqlright2.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
                || sqlright2.toUpperCase().indexOf("V$") > -1) {
                textStr = "";
                return textStr;
              } else {
                textStr = SqlInjectionStr;
              }
            }
          } else {
            if (sqlright.toUpperCase().indexOf("SYS.") > -1
              || sqlright.toUpperCase().indexOf("DUAL") > -1
              || sqlright.toUpperCase().indexOf("USER_") > -1
              || sqlright.toUpperCase().indexOf("DBA_") > -1
              || sqlright.toUpperCase().indexOf("ALL_") > -1
              || sqlright.toUpperCase().indexOf("GLOBAL_NAME ") > -1
              || sqlright.toUpperCase().indexOf("UTL_HTTP.REQUEST") > -1
              || sqlright.toUpperCase().indexOf("V$") > -1) {
              textStr = "";
              return textStr;
            } else
              textStr = SqlInjectionStr;
          }
        }
      }

    }
    return textStr;
  }
}