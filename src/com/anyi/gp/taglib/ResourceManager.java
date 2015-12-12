package com.anyi.gp.taglib;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.anyi.gp.taglib.components.Page;
import com.anyi.gp.util.XMLTools;
import com.anyi.gp.context.ApplusContext;

public class ResourceManager {
  private final static Logger log = Logger.getLogger(ResourceManager.class);
  private final static String IMG_RESOURCE_FILE= "/conf/image_resource.xml";
  
  private static Map toolbarImgMap= null;
  
  public static Map getToolbarImgMap(){
    if (toolbarImgMap!= null) return toolbarImgMap;
    toolbarImgMap= new HashMap();
    InputStream in= readResource(IMG_RESOURCE_FILE);
    Document doc= null;
    try{
      doc= XMLTools.inputStreamToDocument(in);
    }catch(Exception e){
      throw new RuntimeException("\nResourceManager.getToolbarImgMap():\n解析资源文件出错. file name: "+ IMG_RESOURCE_FILE);
    }
    Node toolbarNode= null;
    try{
      toolbarNode= XPathAPI.selectSingleNode(doc.getDocumentElement(), "toolbar");
    }catch(TransformerException e){
      throw new RuntimeException("\nResourceManager.getToolbarImgMap():\n从资源文件中查找 toolbar 结点出错. file name: "+ IMG_RESOURCE_FILE);
    }
    XMLTools.trimChildNodes(toolbarNode);
    for (int i= 0; i< toolbarNode.getChildNodes().getLength(); i++){
      Node img= toolbarNode.getChildNodes().item(i);
      toolbarImgMap.put(XMLTools.getNodeAttr(img, "name"), Page.LOCAL_RESOURCE_PATH + XMLTools.getNodeAttr(img, "src"));
    }
    return toolbarImgMap;
  }

  public static InputStream readResource(String file) {
    InputStream in= null;
    try {
      in= ApplusContext.getServletContext().getResourceAsStream(file);
    } catch (Exception e) {
      String msg = "\nResourceManager.readResource():\n" + e.getMessage();
      log.debug(msg);
      throw new RuntimeException(msg);
    }
    if (in== null){
      throw new RuntimeException("\nResourceManager.readResource():\n没有发现资源文件. file name: "+ file);
    }
    return in;
  }
}
