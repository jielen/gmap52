/*
 * 创建日期 2006-12-18
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.anyi.gp.access;

import com.anyi.gp.util.StringTools;


public class SQLCluster {
    private String sql = "";

    private String countSql = "";

    private String columns = "";

    private String from = "";

    private String where = "";

    public void convertNull() {
      sql = StringTools.ifNull(sql,"");
      countSql = StringTools.ifNull(countSql,"");
      columns = StringTools.ifNull(columns,"");
      from = StringTools.ifNull(from,"");
      where = StringTools.ifNull(where,"");
    }

    public String getColumns() {
      return columns;
    }

    public void setColumns(String columns) {
      this.columns = columns;
    }

    public String getCountSql() {
      return countSql;
    }

    public void setCountSql(String countSql) {
      this.countSql = countSql;
    }

    public String getFrom() {
      return from;
    }

    public void setFrom(String from) {
      this.from = from;
    }

    public String getSql() {
      return sql;
    }

    public void setSql(String sql) {
      this.sql = sql;
    }

    public String getWhere() {
      return where;
    }

    public void setWhere(String where) {
      this.where = where;
    }

}
