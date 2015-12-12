package com.anyi.gp.taglib.components;

import java.util.List;

public interface Container extends Component {

  void setOwnerPage(Page ownerPage);

  Page getPage();
  
  List getEditBoxes();
  
  String getCompoName();
  
  String getTableName();
}
