package com.anyi.gp.localresource;

public class Resource {
  public final static String TYPE_JS= "js";
  public final static String TYPE_JSE= "jse";
  public final static String TYPE_VBS= "vbs";
  public final static String TYPE_IMG= "image";
  
  private String name= "";
  private String url= "";
  private String type= "";
  
  private void initUrl(){
    url= name;
    if (TYPE_JS.equalsIgnoreCase(type)){
      url= "/"+ name.replaceAll("\\.", "/")+ ".js";
    }else if (TYPE_JSE.equalsIgnoreCase(type)){
      url= "/"+ name.replaceAll("\\.", "/")+ ".jse";
    }else if (TYPE_VBS.equalsIgnoreCase(type)){
      url= "/"+ name.replaceAll("\\.", "/")+ ".vbs";
    }
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
    initUrl();
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
}
