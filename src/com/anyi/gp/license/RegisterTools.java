package com.anyi.gp.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;

import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

public class RegisterTools {

	public static final String LICENSE_KEY = "license_key_info";

	public static List getSystemProperties() {
		List result = new ArrayList();
		Properties props = System.getProperties();
		result.add(props.getProperty("os.name"));
		result.add(props.getProperty("os.arch"));
		result.add(props.getProperty("os.version"));
		return result;
	}

	public static Map getInetAddresses() {
		Map result = new HashMap();
		Enumeration netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				String displayName = ni.getDisplayName();
				String ipString = "";
				Enumeration ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					ipString += ((InetAddress) ips.nextElement())
							.getHostAddress()
							+ ",";
				}
				if (ipString != null && ipString.length() > 0)
					ipString = ipString.substring(0, ipString.length() - 1);

				result.put(displayName, ipString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static List getMacAddresses() {
		List result = new ArrayList();
		try {
			Properties props = System.getProperties();
			String command = "ipconfig -a";
			if (props.getProperty("os.name").toLowerCase()
					.startsWith("windows"))
				command = "ipconfig /all";

			Process process = Runtime.getRuntime().exec(command);
			InputStreamReader ir = new InputStreamReader(process
					.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			while ((line = input.readLine()) != null)
				if (line.indexOf("Physical Address") > 0) {
					String MACAddr = line.substring(line.indexOf("-") - 2);
					// System.out.println("MAC address = [" + MACAddr + "]");
					result.add(MACAddr);
				}
		} catch (java.io.IOException e) {
			e.printStackTrace();
			// System.err.println("IOException " + e.getMessage());
		}
		return result;
	}

	public static String getDBServerURL() {
    Connection conn = null;
    try {
      conn = DAOFactory.getInstance().getConnection();
      if (conn != null) {
        DatabaseMetaData meta = conn.getMetaData();
        return (meta.getURL() + ":" + meta.getUserName()).toUpperCase();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }finally{
      DBHelper.closeConnection(conn);
    }
    return "";
  }
    public static String getKeyString(String encodeType) {
        StringBuffer sb = new StringBuffer();
        sb.append("Host[" + getHostName() + "];");
        sb.append("Ip[");
        Set entrySet = getInetAddresses().entrySet();
        java.util.Map.Entry entry;
        for(Iterator iterator = entrySet.iterator(); iterator.hasNext(); sb.append(entry.getValue() + ",")) {
            entry = (java.util.Map.Entry)iterator.next();
        }

        if(!entrySet.isEmpty()) {
            sb = new StringBuffer(sb.substring(0, sb.length() - 1));
        }
        sb.append("];");
        sb.append("ENCODETYPE[" + encodeType + "];");
        sb.append("Mac[");
        List macList = getMacAddresses();
        for(int i = 0; i < macList.size(); i++) {
            if(i != 0) {
                sb.append(",");
            }
            sb.append(macList.get(i));
        }

        sb.append("]");
        return sb.toString();
    }

	public static String getKeyStringFromDB() {
		String sql = " select value from as_info where key = ? ";
		if(DAOFactory.getWhichFactory() == DAOFactory.MSSQL){
		  sql = " select value from as_info where [key] = ? ";
		}
		return (String) DBHelper.queryOneValue(sql,
				new Object[] { LICENSE_KEY });
	}

	public static String encodeString(String str) {
		String tempStr = "";
		try {
			String encodeStr = "$#TGDF*FAA&21we@VGXD532w23413!";
			if (str == null) {
				str = "";
			}

			int i = 0;
			int j = 0;
			for (i = 0; i < str.length(); i++) {
				if (j >= encodeStr.length()) {
					j = 0;
				}
				tempStr = tempStr
						+ (char) (str.charAt(i) ^ encodeStr.charAt(j));
				j++;
			}
			tempStr = new String(Hex.encodeHex(tempStr.getBytes("GBK")));
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return tempStr;
	}

	public static String decodeString(String encodedStr) {
		String encodeStr = "$#TGDF*FAA&21we@VGXD532w23413!";
		String tempStr = "";
		try {
			if (encodedStr == null) {
				encodedStr = "";
			}
			//encodedStr = new String(Hex.decodeHex(encodedStr.toCharArray()));
			Hex hex = new Hex();
			encodedStr = new String(hex.decode(encodedStr.getBytes("GBK")), "GBK");
			int i = 0;
			int j = 0;
			for (i = 0; i < encodedStr.length(); i++) {
				if (j >= encodeStr.length()) {
					j = 0;
				}
				char truePass = (char) ~(encodedStr.charAt(i) ^ ~encodeStr
						.charAt(j));
				tempStr = tempStr + truePass;
				j++;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return tempStr;
	}

//	public static void writeKeyToFile(String fileName) {
//		File tempFile = new File(fileName);
//		FileOutputStream os = null;
//		try {
//			os = new FileOutputStream(tempFile);
//			os.write(encodeString(getKeyString()).getBytes());
//
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (os != null)
//					os.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public static String readKeyFromFile(String fileName) {
		String result = "";
		File tempFile = new File(fileName);
		FileInputStream io = null;
		try {
			io = new FileInputStream(tempFile);
			byte[] buf = new byte[512];
			int size = io.read(buf);
			while (size > 0) {
				result += new String(buf, 0, size);
				size = io.read(buf);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (io != null)
					io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return decodeString(result);
	}

	public static Map getKeyValueFormLicen(String licen) {
		Map result = new HashMap();
		if (licen != null) {
			String[] entrys = licen.split(";");
			int length = entrys.length;
			// TCJLODO:ÅÐ¶Ï³¤¶ÈÎª13
			for (int i = 0; i < length - 3; i++) {
				String entry = entrys[i];
				int index = entry.indexOf("[");
				String key = entry.substring(0, index);
				String value = entry.substring(index + 1, entry.length() - 1);
				result.put(key, value);
			}
			String keyString = entrys[length - 3] + ";" + entrys[length - 2]
					+ ";" + entrys[length - 1];
			result.put("resKey", encodeString(keyString));
		}
		return result;
	}
	
    public static String resetKeyStrByEncodeType(String encodeType, String keyString) {
        String deString = decodeString(keyString);
        String infos[] = deString.split(";");
        infos[2] = "ENCODETYPE[" + encodeType + "]";
        String temp = "";
        for(int i = 0; i < 4; i++) {
            temp = temp + ";" + infos[i];
        }

        temp = temp.substring(1);
        return encodeString(temp);
    }
	
    public static String getEncodeType(String keyString) {
        String result = "";
        String clientInfo = decodeString(keyString);
        String infos[] = clientInfo.split(";");
        if(infos.length == 4) {
            result = infos[2];
            int index = result.indexOf("[");
            result = result.substring(index + 1, result.length() - 1);
        } else {
            result = "0";
        }
        return result;
    }
}
