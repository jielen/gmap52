package com.kingdrive.workflow.util;

import java.util.ArrayList;
import java.util.List;

public final class TreeNode {

  private String id;

  private String name;

  private String tag;

  private String URL;

  private List children;

  public TreeNode(String id, String name) {
    this.id = id;
    this.name = name;
    this.tag = "";
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }

  public List getChildren() {
    return children;
  }

  public void add(TreeNode child) {
    if (children == null) {
      children = new ArrayList();
    }
    children.add(child);
  }

  public boolean hasChild() {
    return children != null && children.size() != 0;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String URL) {
    this.URL = URL;
  }
}
