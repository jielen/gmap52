/*$Id: About.java,v 1.2 2008/06/30 11:58:52 liuxiaoyong Exp $*/
package com.anyi.gp.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.CommonSqlIdConst;
import com.anyi.gp.pub.LangResource;
import com.anyi.gp.util.StringTools;

public class About {

  private final Logger log = Logger.getLogger(About.class);

  String verID = "版本标识:1.0";

  String verDate = "版本日期:2003-4-30";

  String rightCN = "版权所有:北京用友政务软件有限公司";

  String rightEN = "Copyright:Beijing UF Government Affair Software Co.,Ltd.";

  List products;

  public About() {
  }

  public void init() {
    Map warRelation = new HashMap();
    getVersionNum(warRelation);

    products = new ArrayList();
    BaseDao dao = (BaseDao) ApplusContext.getBean("baseDao");

    try {
      List resultList = dao.queryForList(CommonSqlIdConst.GET_PRODUCT_VER);
      LangResource res = LangResource.getInstance();
      String version = "";
      Map resMap = null;

      for (int i = 0; i <= resultList.size(); i++) {
        Product p = new Product();
        resMap = (Map) resultList.get(i);
        String val = (String) resMap.get("PRODUCT_CODE");
        if(val.equalsIgnoreCase("style") || val.equalsIgnoreCase("admin") || val.equalsIgnoreCase("WFDesigner")
          || (val.equalsIgnoreCase("MA") && ("SYS".equalsIgnoreCase((String)resMap.get("TRAD_CODE"))))){
          continue;
        }
        version = (String) warRelation.get(val);
        if (val.equalsIgnoreCase("AS")) {
          version = (String) warRelation.get("applus");
        }
        val = res.getLang(val);
        p.product = val;

        val = (String) resMap.get("TRAD_CODE");
        val = res.getLang(val);
        p.trade = val;

        val = (String) resMap.get("VERSION");
        if (version != null && version.length() > 0) {
          p.version = val + "." + version;
        } else {
          p.version = val;
        }
        products.add(p);
      }
    } catch (Exception ex) {
      log.info(ex);
    }
  }

  /**
   * 遍历路径application下的所有压缩文件,获取所有war包
   * 
   * @param warRelation
   *          Map
   * @throws RuntimeException
   */
  private void getVersionNum(Map warRelation) throws RuntimeException {
    String getAppServerPath = new About().getClass().getResource("").toString();
    // getAppServerPath=jar:file:/E:/SetupJBOSS/test0428/jboss-3.0.8/server/default/lib/AS.jar!/com/anyi/erp/page/
    // getAppServerPath =
    // "jar:file:/C:/bea/user_projects/domains/jbxtest/applications/AS.jar!/com/anyi/erp/page/";
    if (getAppServerPath.indexOf("/lib") > 0) {
      getAppServerPath = StringTools.replaceAll(getAppServerPath, "/lib", "/deploy");
    }
    int begPath = getAppServerPath.indexOf("file:") + 5;
    int endPath = getAppServerPath.indexOf("AS.") - 1;
    if (getAppServerPath.indexOf("AS.") == -1) {
      return;
    }
    getAppServerPath = getAppServerPath.substring(begPath, endPath);
    File f = new File(getAppServerPath);
    File file[];
    file = f.listFiles();
    String warName = "";
    for (int i = 0; i < file.length; i++) {
      if (file[i].isDirectory()) {
        continue;
      }
      warName = file[i].getName();
      if (warName.indexOf(".war") > 1) {
        getZipFileBuildNumber(warRelation, getAppServerPath, warName);
        continue;
      }
    }
  }

  /**
   * 遍历war包,获取其中的buildernumber.txt 从中查找到对应部件的版本号.放到hashmap中去.
   * 
   * @param warRelation
   *          Map
   * @param getAppServerPath
   *          String
   * @param warName
   *          String
   * @throws RuntimeException
   */
  private void getZipFileBuildNumber(Map warRelation, String getAppServerPath,
    String warName) throws RuntimeException {
    String verNum = "";
    String war = "";
    war = warName.substring(0, warName.indexOf(".war"));
    warName = getAppServerPath + "/" + warName;
    File filetem = new File(warName);
    FileInputStream fis = null;
    ZipInputStream zis = null;
    try {
      fis = new FileInputStream(filetem);
      zis = new ZipInputStream(fis);
      ZipEntry entry = null;
      do {
        entry = zis.getNextEntry();
        if (entry == null) {
          continue;
        }
        if (!entry.getName().equals("buildnumber.txt")) {
          continue;
        } // 获取txt中所有信息,组成字符串
        byte[] data = new byte[200];
        verNum = disposeBuildNumber(data);
        if (verNum.length() > 0) {
          warRelation.put(war, verNum);
        }
        zis.close();
        fis.close();
        break;
      } while (entry != null);
    } catch (IOException ex) {
      throw new RuntimeException("About类的获取版本号出错：" + ex.toString());
    } finally {
    }
  }

  /**
   * 处理获取的byte[],从中得到版本号.
   * 
   * @param data
   *          byte[]
   * @return String
   */
  private String disposeBuildNumber(byte[] data) {
    String verNum;
    String str = new String(data);
    String fileContent = "";
    for (int k = 0; k < str.length(); k++) {
      fileContent += str.charAt(k);
    }
    int verBegin = 0;
    int verEnd = 0;
    verBegin = fileContent.indexOf("number=") + 7;
    int numberLeng = fileContent.substring(verBegin).indexOf("\r\n");
    verEnd = verBegin + numberLeng;
    verNum = fileContent.substring(verBegin, verEnd);
    return verNum;
  }

  /**
   * @return   Returns the verID.
   * @uml.property   name="verID"
   */
  public String getVerID() {
    return verID;
  }

  public int getProductsNum() {
    return products.size();
  }

  /**
   * 
   * @param recordNo
   *          记录号
   * @param fieldNo
   *          字段号 0: product_code,1:trade_code,2:version
   * @return
   */
  public String getValue(int recordNo, int fieldNo) {
    Product p = (Product) products.get(recordNo);
    String result;
    switch (fieldNo) {
    case 0:
      result = p.product;
      break;
    case 1:
      result = p.trade;
      break;
    case 2:
      result = p.version;
      break;
    default:
      result = "record is overflow,not exits";
    }
    return result;
  }

  /**
   * @return   Returns the verDate.
   * @uml.property   name="verDate"
   */
  public String getVerDate() {
    return verDate;
  }

  /**
   * @return   Returns the rightCN.
   * @uml.property   name="rightCN"
   */
  public String getRightCN() {
    return rightCN;
  }

  /**
   * @return   Returns the rightEN.
   * @uml.property   name="rightEN"
   */
  public String getRightEN() {
    return rightEN;
  }

}

class Product {
  String product;

  String trade;

  String version;
}
