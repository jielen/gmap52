package com.anyi.gp.meta;

import com.anyi.gp.context.ApplusContext;

public class MetaManager {
	 
  public static final CompoMeta getCompoMeta(String name){
  	AbstractMetaBuilder builder = (CompoMetaBuilder)ApplusContext.getBean("compoMetaBuilder");
  	CompoMeta meta  = (CompoMeta)builder.generateMeta(MetaSqlIdConst.compoMetaSQLID, name);
    if(meta != null){
      if(meta.getMasterTable() != null){
        TableMeta tableMeta = getTableMeta(meta.getMasterTable());
        meta.setTableMeta(tableMeta);
      }
    }
  	return meta;
  }
  
  public static final TableMeta getTableMeta(String name){
  	AbstractMetaBuilder builder = (TableMetaBuilder)ApplusContext.getBean("tableMetaBuilder");
  	return (TableMeta)builder.generateMeta(MetaSqlIdConst.TableMetaSQLID, name);
  }
  
  public static final TableMeta getTableMetaByCompoName(String compoName){
  	return getCompoMeta(compoName).getTableMeta();
  }
}