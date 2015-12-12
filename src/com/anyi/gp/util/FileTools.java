// $Id: FileTools.java,v 1.8 2008/04/28 07:07:31 liuxiaoyong Exp $
package com.anyi.gp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.anyi.gp.Pub;

public class FileTools {

  public final static String FLODER_UP_LOAD = "gp" + File.separator + "upload"
    + File.separator;

  /**
   * 保存 text file;
   *
   * @param sFileName
   * @param sText
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void writeTextFile(String sFileName, String sText)
    throws IOException, FileNotFoundException {
    if (sFileName == null || sFileName.length() < 2)
      return;
    if (sText == null)
      return;
    FileOutputStream voFOS = new FileOutputStream(sFileName);
    voFOS.write(sText.getBytes());
    voFOS.close();
    voFOS = null;
  }

  /**
   * 删除文件;
   *
   * @param sFileName
   * @return
   * @throws IOException
   */
  public static boolean deleteFile(String sFileName) throws IOException {
    if (sFileName == null)
      return true;
    File voFile = null;
    try {
      voFile = new File(sFileName);
      if (voFile.exists() == false)
        return true;
      return voFile.delete();
    } finally {
      if (voFile != null)
        voFile = null;
    }
  }

  /**
   * 创建文件夹;
   *
   * @param sDir
   * @return
   */
  public static String makeDir(String sDir) {
    File file = new File(sDir);
    if (!file.exists()) {
      if (file.mkdirs()) {
        return sDir;
      }
    }
    file = null;
    return sDir;
  }

  /**
   * 读取文本文件;
   *
   * @param sFileName
   * @return
   * @throws IOException
   */
  public static String readTextFile(String sFileName) throws IOException {
    if (sFileName == null)
      return "";
    FileInputStream voFIS = null;
    try {
      File voFile = new File(sFileName);
      if (voFile.exists() == false)
        return "";
      voFIS = new FileInputStream(voFile);
      if (voFIS == null)
        return "";
      byte data[] = new byte[(int) voFile.length()];
      voFIS.read(data);
      return new String(data);
    } catch (IOException e) {
      throw e;
    } finally {
      if (voFIS != null)
        voFIS.close();
    }
  }

  /**
   * 生成上载文件的文件名;
   *
   * @param originalFileName
   * @param type
   * @param id
   * @return String
   * @throws Exception
   */
  public static String makeUploadFileName(String originalFileName, String type)
    throws Exception {
    return makeUploadFileName(originalFileName, type, null);
  }

  public static String makeUploadFileName(String originalFileName, String type,
    String id) throws Exception {
    if (originalFileName == null)
      originalFileName = "";
    if (type == null)
      type = "";

    String vsFileId = null;
    try {
      if (id == null || id.trim().length() == 0) {
        id = Pub.getUID();
      }

      int viPos = originalFileName.lastIndexOf(".");
      String vsExtName = "";
      if (viPos > 0) {
        vsExtName = originalFileName.substring(viPos);
        vsExtName = StringTools.replaceAll(vsExtName, "\"", "");
        vsExtName = StringTools.replaceAll(vsExtName, "\r", "");
        vsExtName = StringTools.replaceAll(vsExtName, "\n", "");
      }
      String vsType = "";
      vsType = StringTools.replaceAll(type, "/", "%%");
      vsType = StringTools.replaceAll(vsType, "\r", "");
      vsType = StringTools.replaceAll(vsType, "\n", "");
      vsFileId = id + "{" + vsType + "}" + vsExtName;
      return vsFileId;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 判断指定的文件是否存在;
   * 
   * @param sFile
   * @return
   */
  public static boolean isExist(String sFileName) {
    File file = new File(sFileName);
    if (file.exists()) {
      file = null;
      return true;
    }
    return false;
  }

  public static File[] getListFiles(String dir) {
    File file = new File(dir);
    if (!file.exists())
      return null;
    return file.listFiles();
  }

  public static List getFileNames(String dir, final String regex,
    boolean caseSensitive) {
    List fileNames = new ArrayList();
    if (dir == null)
      return fileNames;
    File f = new File(dir);
    if (!f.exists() || f.isFile())
      return fileNames;
    File[] files = f.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.isDirectory()) {
        List l = getFileNames(file.getPath(), regex, caseSensitive);
        fileNames.addAll(l);
      } else {
        String path = processFileSeparator(file.getAbsolutePath());
        if (regex == null) {
          fileNames.add(path);
          continue;
        }
        Pattern p;
        if (!caseSensitive) {
          p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } else {
          p = Pattern.compile(regex);
        }
        if (p.matches(regex, path)) {
          fileNames.add(path);
        }
      }
    }
    return fileNames;
  }

  public static String processFileSeparator(String fileName) {
    return fileName.replace('\\', '/');
  }

}
