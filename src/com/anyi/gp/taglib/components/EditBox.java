package com.anyi.gp.taglib.components;

import org.w3c.dom.Node;

public interface EditBox extends HtmlComponent {

  String getId();
  
  void init();
  
  void initByField(Node node);
  
  String getGroupId();

  String getFieldName();

  void setContainer(Container c);

  void setIsallowinput(boolean isallowinput);
  
  void setValueAsDefValue();
}
