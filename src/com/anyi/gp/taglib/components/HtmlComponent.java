package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

public interface HtmlComponent extends Component {
  
  String getScriptVarName();
  
  String getCompoName();

  String getTableName();

  int getTabindex();

  String getIdsuffix();

  void setIdsuffix(String idsuffix);

  boolean isIsvisible();

  void writeHTML(Writer out) throws IOException;
}
