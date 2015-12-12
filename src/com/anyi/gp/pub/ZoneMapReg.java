// $Id: ZoneMapReg.java,v 1.1 2008/04/16 13:39:16 liuxiaoyong Exp $

package com.anyi.gp.pub;

import java.net.MalformedURLException;
import java.net.URL;

import com.anyi.gp.util.StringTools;

public class ZoneMapReg {

  static String REG_ROOT = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Domains";

  public String getZoneMap(String urlString) throws MalformedURLException {
    URL url = new URL(urlString);
    String host = url.getHost();
    String[] names = StringTools.splitA(host, '.');
    if (isIPAddress(names)) {
      return doIPZoneMap(host);
    }
    return doZoneMap(names, host);
  }

  private boolean isIPAddress(String names[]) {
    try{
      for(int i = 0; i < names.length; i++){
        Integer.parseInt(names[i]);
      }
    } catch(NumberFormatException e){
      return false;
    }
    return true;
  }

  private String doIPZoneMap(String host) {
    return "[HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\ZoneMap\\Ranges\\Range1]\r\n"
        + "\"http\"=dword:00000002\r\n" + "\":Range\"=\"" + host + "\"\r\n";
  }

  private String doZoneMap(String names[], String host) {
    if (names.length <= 2) {
      return "[" + REG_ROOT + "\\" + host + "]\r\n"
          + "\"http\"=dword:00000002\r\n";
    }
    int len = names.length;
    String surfix = names[len - 2] + "." + names[len - 1];
    String prefix = names[0];
    for (int i = 1; i < len - 2; i++) {
      prefix += "." + names[i];
    }
    String result = "[" + REG_ROOT + "\\" + surfix + "]\r\n";
    result += "[" + REG_ROOT + "\\" + surfix + "\\" + prefix + "]\r\n"
        + "\"http\"=dword:00000002\r\n";
    return result;
  }
}
