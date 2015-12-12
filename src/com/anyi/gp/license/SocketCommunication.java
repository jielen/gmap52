package com.anyi.gp.license;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.anyi.gp.BusinessException;

public class SocketCommunication {
  
  private static final Logger log = Logger.getLogger(SocketCommunication.class);

  private String host = null;

  private int port = 0;

  private int appliedcount = 99999999;

  private Socket socket = null;

  private InputStreamReader in = null;

  private OutputStreamWriter out = null;

  private String readBuffer = null;

  private String productname = null;

  private String sessionKey = null;

  private static int allowedcount = 0;

  private final String keyBase = "abcdefghijklmn01234ABCDEFGHIJKLMN-=\\()_+|opqrstuvwxyz56789OPQRSTUVWXYZ{}:<>/?";

  private final int minKeyLen = 10;

  private final char blot2 = '=';

  private final char blot1 = '+';

  private final String singleBase = "0987654321ABCDEF";

  private final String charBase = "quiteCRAZY0369_?";

  private int readLen = 0;

  private char[] readStr = new char[1024];

  public static String SN = null;

  public void connect(String remotehost, int remoteport, String checkproductname) throws UnknownHostException, IOException{
    host = remotehost;
    port = remoteport;
    productname = checkproductname;
    try{
      doConnect();
    } catch (UnknownHostException e) {
      log.error(e);
      throw e;
    } catch (IOException e) {
      log.error(e);
      throw e;
    }finally{
      connectClose();
    }
  }

  private void doConnect() throws UnknownHostException, IOException {
    openSocket();
    socket.setSoTimeout(1000 * 5);
    send("LOGIN.2 " + appliedcount);
    for (int i = 0; i < 3; i++) {
      receive();
    }
  }

  private void connectClose() {
    try {
      if (out != null) {
        out.close();
      }
    } catch (IOException e) {
      log.error(e);
    }
    try {
      if (in != null) {
        in.close();
      }
    } catch (IOException e) {
      log.error(e);
    }
    try {
      if (socket != null) {
        if (!socket.isClosed())
          socket.close();
      }
    } catch (IOException e) {
      log.error(e);
    }
  }

  private void openSocket() throws UnknownHostException, IOException {
    allowedcount = 0;
    readBuffer = "";
    socket = new Socket(host, port);
    in = new InputStreamReader(socket.getInputStream());
    out = new OutputStreamWriter(socket.getOutputStream());

  }

  private void send(String text) throws IOException {
    String prefix = "[" + productname + "|" + "]";
    String str = encode(prefix + text);
    out.write(str);
    out.flush();
  }

  private String encode(String text) {
    String r, key1, key2;
    int i, blotPos;
    sessionKey = getRandomKey();
    r = encodeText(text, sessionKey);
    i = sessionKey.length() / 2;
    key1 = sessionKey.substring(0, i);
    key2 = sessionKey.substring(i);
    r = keyBase.charAt(sessionKey.length() - 1) + key2 + key1 + r;
    blotPos = (int) (Math.random() * minKeyLen);
    r = keyBase.charAt(blotPos) + r.substring(0, blotPos) + blot2
      + keyBase.charAt((int) (Math.random() * keyBase.length()))
      + r.substring(blotPos);
    blotPos = (int) (Math.random() * minKeyLen);
    r = keyBase.charAt(blotPos) + r.substring(0, blotPos) + blot1
      + keyBase.charAt((int) (Math.random() * keyBase.length()))
      + r.substring(blotPos);
    return r;
  }

  private String getRandomKey() {
    int i;
    long n = System.currentTimeMillis();
    n = n % 100;
    for (i = 0; i <= n; i++)
      Math.random();
    // 生成随机长度的随机字符串密匙。
    int keyLen = minKeyLen + (int) (Math.random() * (keyBase.length() - minKeyLen));
    char[] key = new char[keyLen];
    for (i = 0; i < keyLen; i++)
      key[i] = keyBase.charAt((int) (Math.random() * keyBase.length()));
    return new String(key);
  }

  private String encodeText(String text, String key) {
    char[] tmp0, tmp1, tmp2;
    tmp0 = (wideToSingle(text) + key).toCharArray();
    int n = tmp0.length;
    if (n > 0) {
      tmp1 = new char[n];
      rollText(tmp0, tmp1, n, key);
      rollText(tmp1, tmp0, n, key);
      rollText(tmp0, tmp1, n, key);
      tmp2 = new char[n * 2];
      convertText(tmp1, tmp2, n);
      return new String(tmp2);
    }
    return "";
  }

  private String wideToSingle(String text) {
    String r = "";
    char c;
    int i = 0;
    int n = text.length();
    while (i < n) {
      c = text.charAt(i);
      if (c > 0x7F) {
        r += "%" + singleBase.charAt((c & 0xF000) >>> 12)
          + singleBase.charAt((c & 0x0F00) >>> 8) + "%"
          + singleBase.charAt((c & 0x00F0) >>> 4)
          + singleBase.charAt((c & 0x000F) >>> 0);
        i++;
      } else {
        r += c;
        if (c == '%')
          r += c;
        i++;
      }
    }
    return r;
  }

  private void rollText(char[] in, char[] out, int count, String key) {
    int i = 0;
    System.arraycopy(in, 0, out, 0, count);
    for (i = 0; i <= count - 1; i++) {
      out[i] = (char) (out[i] ^ (i % 0xFF));
    }
    rollByKey(out, count, key);
    for (i = 1; i <= count - 1; i++) {
      out[i] = (char) (out[i] ^ out[i - 1]);
    }
    rollByKey(out, count, key);
    for (i = count - 2; i >= 0; i--) {
      out[i] = (char) (out[i] ^ out[i + 1]);
    }
    rollByKey(out, count, key);
  }

  private void convertText(char[] in, char[] out, int count) {
    int i = 0;
    char b0, b1, b2, b3;
    while (i < count) {
      b0 = (char) ((in[i] & 0x0C) >>> 2);
      b1 = (char) ((in[i] & 0xC0) >>> 4);
      b2 = (char) ((in[i] & 0x03) >>> 0);
      b3 = (char) ((in[i] & 0x30) >>> 2);
      out[i * 2 + 0] = hexToChar((char) (b1 | b0));
      out[i * 2 + 1] = hexToChar((char) (b3 | b2));
      i++;
    }
  }

  private void rollByKey(char[] out, int count, String key) {
    int n = key.length();
    if (n > 0) {
      for (int i = 0; i < count; i++)
        out[i] = (char) (out[i] ^ key.charAt(i % n));
    }
  }

  private char hexToChar(char b) {
    return charBase.charAt(b);
  }

  private void receive() throws IOException {
    readLen = in.read(readStr, 0, readStr.length);
    if (readLen > 0) {
      String str = new String(readStr, 0, readLen);
      parseText(str);
    }
  }

  private void parseText(String text) {
    int p1, p2, msgLen;
    readBuffer = readBuffer + text;
    while (readBuffer.length() > 0) {
      p1 = readBuffer.indexOf('[');
      p2 = readBuffer.indexOf(']');
      if (p1 >= 0) {
        if (p2 >= 2) {
          try {
            msgLen = Integer.parseInt(readBuffer.substring(p1 + 1, p2));
          } catch (NumberFormatException e) {
            msgLen = 0;
          }
          p1 = p2 + 1;
          p2 = p1 + msgLen;
          if (p2 <= readBuffer.length()) {
            if (msgLen > 0) {
              try {
                processMessage(readBuffer.substring(p1, p2));
              } catch (BusinessException e) {
                log.error(e);
              }
            }
            readBuffer = readBuffer.substring(p2);
            return;
          } else
            return;
        } else {
          try {
            if (Integer.parseInt(readBuffer.substring(p1 + 1)) > 8192)
              readBuffer = ""; // cuiliguo 2006.06.28
            // （暂定）内容长度超过8192字节的都是无效数据包。
          } catch (NumberFormatException e) {
            readBuffer = ""; // cuiliguo 2006.06.28
            // 不能正确解析内容长度的都是无效数据包。
          }
          return;
        }
      } else {
        readBuffer = "";
        return;
      }
    }
  }

  private void processMessage(String msg) throws BusinessException {
    msg = decode(msg);
    if (msg.length() > 1) {
      if (msg.charAt(0) == 'O' && msg.charAt(1) == ':') {
        if (msg.substring(2).startsWith("LOGIN.2 OK [") && msg.endsWith("]")) {
          int start = msg.indexOf("LOGIN.2 OK [");
          int end = msg.indexOf("]");
          SN = msg.substring(start + 12, end);
        } else {
          throw new BusinessException("无效的加密服务响应信息[O]。");
        }
      }
      if (msg.charAt(0) == 'V' && msg.charAt(1) == ':') {
        int p1 = msg.indexOf('>');
        int p2 = msg.indexOf('<');
        if (p1 >= 0 && p2 >= 2) {
          setInternalCount(Integer.parseInt(msg.substring(p1 + 1, p2)));
        } else
          log.error("无法取得产品代码 [" + productname + "] 的授权许可信息。");
      }
    }
  }

  private String decode(String text) {
    String r, s, key0, key1, key2;
    int i, blotPos, keyLen;
    r = "";
    s = text;
    if (s.length() > minKeyLen + 1 + 1 + 2) {
      blotPos = keyBase.indexOf(s.charAt(0));
      s = s.substring(1);
      if (s.charAt(blotPos) == blot1) {
        s = s.substring(0, blotPos) + s.substring(blotPos + 2);
        blotPos = keyBase.indexOf(s.charAt(0));
        s = s.substring(1);
        if (s.charAt(blotPos) == blot2) {
          s = s.substring(0, blotPos) + s.substring(blotPos + 2);
          keyLen = keyBase.indexOf(s.charAt(0)) + 1;
          if (keyLen >= minKeyLen) {
            key0 = s.substring(1, keyLen + 1);
            s = s.substring(keyLen + 1);
            // 重新拼接字符串密匙。
            i = keyLen - keyLen / 2;
            key2 = key0.substring(0, i);
            key1 = key0.substring(i);
            key0 = key1 + key2;
            if (sessionKey.equals(key0)) {
              r = decodeText(s, key0);
            }
          }
        }
      }
    }
    return r;
  }

  private String decodeText(String text, String key) {
    char[] tmp0, tmp1, tmp2;
    tmp2 = text.toCharArray();
    int n = tmp2.length / 2;
    if (n > 0) {
      tmp1 = new char[n];
      unconvertText(tmp2, tmp1, n * 2);
      tmp0 = new char[n];
      unrollText(tmp1, tmp0, n, key);
      unrollText(tmp0, tmp1, n, key);
      unrollText(tmp1, tmp0, n, key);
      if (n > key.length()) {
        String tmptext = new String(tmp0);
        String tmpkey = tmptext.substring(n - key.length());
        if (tmpkey.equals(key)) {
          tmp0 = tmptext.substring(0, n - key.length()).toCharArray();
          return singleToWide(new String(tmp0));
        } else
          return "";
      } else
        return "";
    } else
      return "";
  }

  private void unrollText(char[] in, char[] out, int count, String key) {
    int i = 0;
    System.arraycopy(in, 0, out, 0, count);
    unrollByKey(out, count, key);
    for (i = 0; i <= count - 2; i++) {
      out[i] = (char) (out[i] ^ out[i + 1]);
    }
    unrollByKey(out, count, key);
    for (i = count - 1; i >= 1; i--) {
      out[i] = (char) (out[i] ^ out[i - 1]);
    }
    unrollByKey(out, count, key);
    for (i = 0; i <= count - 1; i++) {
      out[i] = (char) (out[i] ^ (i % 0xFF));
    }
  }

  private void unconvertText(char[] in, char[] out, int count) {
    int i = 0;
    int n = count / 2;
    char b0, b1, b2, b3;
    while (i < n) {
      b0 = (char) ((charToHex(in[i * 2 + 1]) & 0x03) << 0);
      b1 = (char) ((charToHex(in[i * 2 + 0]) & 0x03) << 2);
      b2 = (char) ((charToHex(in[i * 2 + 1]) & 0x0C) << 2);
      b3 = (char) ((charToHex(in[i * 2 + 0]) & 0x0C) << 4);
      out[i] = (char) (b3 | b2 | b1 | b0);
      i++;
    }
  }

  private String singleToWide(String text) {
    String r = "";
    char c;
    int i = 0;
    int n = text.length();
    while (i < n) {
      c = text.charAt(i);
      if (c == '%') {
        if (i < n - 1) {
          if (text.charAt(i + 1) == '%') {
            r += "%";
            i += 2;
          } else if (i < n - 5 && text.charAt(i + 3) == '%') {
            int x = (singleBase.indexOf(text.charAt(i + 1)) << 12)
              | (singleBase.indexOf(text.charAt(i + 2)) << 8)
              | (singleBase.indexOf(text.charAt(i + 4)) << 4)
              | (singleBase.indexOf(text.charAt(i + 5)) << 0);
            r += (char) x;
            i += 6;
          } else
            i++;
        } else
          i++;
      } else {
        r = (String) r + c;
        i++;
      }
    }
    return r;
  }

  private void unrollByKey(char[] out, int count, String key) {
    int n = key.length();
    if (n > 0) {
      for (int i = count - 1; i >= 0; i--)
        out[i] = (char) (out[i] ^ key.charAt(i % n));
    }
  }

  private char charToHex(char b) {
    for (char i = 0; i < charBase.length(); i++) {
      if (b == charBase.charAt(i))
        return i;
    }
    return 0;
  }

  public int getInternalCount() {
    return (allowedcount > 0 ? (allowedcount - 77) / 11 : 0);
  }

  private void setInternalCount(int value) {
    allowedcount = (value > 0 ? value * 11 + 77 : 0);
  }

  public String directDecode(String text, String key) {
    return decodeText(text, key + "t%5y^6u&7i*8o(9p)0");
  }

}
