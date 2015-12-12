package com.anyi.gp.meta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anyi.gp.Pub;

public class TableMetaBuilder extends AbstractMetaBuilder {

  private static Map tablePool = new HashMap();

  public synchronized Object generateMeta(String sqlId, Object parameterObject) {

    TableMeta table = (TableMeta) tablePool.get(parameterObject);
    if (table != null)
      return table;

    try {
      table = generateTableMeta(sqlId, parameterObject);
      if (table == null)
        return table;

      addFields(table);
      addForeigns(table);

      List childTableNames = table.getChildTableNames();
      for (int i = 0; i < childTableNames.size(); i++) {
        TableMeta childTableMeta = (TableMeta) generateMeta(sqlId, childTableNames
          .get(i));
        table.getChildren().put(childTableMeta.getName(), childTableMeta);
        childTableMeta.setParent(table);
        tablePool.put(childTableMeta.getName(), childTableMeta);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    tablePool.put(table.getName(), table);

    return table;
  }

  private synchronized TableMeta generateTableMeta(String id, Object parameterObject) {
    TableMeta tableMeta = new TableMeta();
    String masterTable = parameterObject.toString();
    try {
      List list = dao.queryForList(id, parameterObject);

      if (list.isEmpty()) {
        tableMeta.setName(masterTable);
        tableMeta.setTable(false);
      } else {
        for (int i = 0; i < list.size(); i++) {
          Map map = (Map) list.get(i);
          String tableId = map.get("TAB_ID").toString();
          boolean isTable = Pub.parseBool(map.get("IS_TABLE"));
          if (masterTable.equalsIgnoreCase(tableId)) {
            tableMeta.setName(tableId);
            tableMeta.setTable(isTable);
          } else {
            tableMeta.addChildTableName(tableId);
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tableMeta;
  }

  private List getAllFields(String tableId) throws SQLException {
    Map params = new HashMap();
    params.put("tabid", tableId);
    return dao.queryForList(MetaSqlIdConst.TableFieldSQLID, params);
  }

  private void addFields(TableMeta meta) throws SQLException {
    List fields = this.getAllFields(meta.getName());
    Iterator iterator = fields.iterator();
    while (iterator.hasNext()) {
      Field field = (Field) iterator.next();
      String dataItem = field.getName();
      meta.addField(dataItem, field);
    }
  }

  private List getAllForeigns(String tableId) throws SQLException {
    Map params = new HashMap();
    params.put("tabid", tableId);
    return dao.queryForList(MetaSqlIdConst.ForeignSQLID, params);
  }

  private void addForeigns(TableMeta meta) throws SQLException {
    List foreigns = this.getAllForeigns(meta.getName());
    Iterator iterator = foreigns.iterator();
    while (iterator.hasNext()) {
      Foreign foreign = (Foreign) iterator.next();
      foreign.initAllrefFields(meta);
      addEffectfields(foreign);
      meta.addForeign(foreign.getRefName(), foreign);
    }
  }

  private void addEffectfields(Foreign foreign) throws SQLException {
    //String sql = "select S_FIELD, D_FIELD from as_col_relation"
    //  + " where f_ref_name=? and tab_id = ?";
    Map params = new HashMap();
    params.put("f_ref_name", foreign.getRefName());
    params.put("tab_id", foreign.getTableName());
    List list = dao.queryForList(MetaSqlIdConst.asColRelation, params);
    for (int i = 0; i < list.size(); i++) {
      Map m = (Map) list.get(i);
      foreign.addEffectField((String) m.get("S_FIELD"), (String) m.get("D_FIELD"));
    }
  }

  public static TableMeta getDefaultTableMeta(String sTableName, List oFieldList) {
    TableMeta meta = new TableMeta();
    meta.setName(sTableName);
    for (int i = 0; i < oFieldList.size(); i++) {
      Field field = new Field();
      String name = oFieldList.get(i).toString();
      field.setName(name);
      meta.addField(name, field);
    }
    return meta;
  }

  public static void clearMetaPool() {
    tablePool.clear();
  }
}
