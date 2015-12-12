package com.anyi.gp.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.anyi.gp.license.LicenseManager;
import com.anyi.gp.license.LicenseStatus;
import com.anyi.gp.license.RegisterTools;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

public class DogCheckListener implements ServletContextListener {
	private static final Logger logger = Logger
			.getLogger(DogCheckListener.class);

	public static final String DOGSN = "dogsn";

	public void contextInitialized(ServletContextEvent event) {
		// 读取狗的信息
		try {
			LicenseManager licenseManager = (LicenseManager) ApplusContext
					.getBean("licenseManager");
			LicenseStatus status = licenseManager.getLicenseStatus();
			boolean isSoft = true;
			// status.setSn();
			if (status != null) {
				String host = status.getHost();
				int port = status.getPort();
				licenseManager.clearLicenseStatus(host, port);
				//System.out.println("it is clear");
				status.setSn();
				String sn = status.getSn();
				if (sn != null) {
					licenseManager.startWebService(host, port);
				}
				isSoft = false;
			}
			updateRegisterInfo(licenseManager, isSoft);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			//System.out.println(ex.getMessage());
		}

		logger.debug("\ninitialized.");
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		// LicenseManager licenseManager = (LicenseManager) ApplusContext
		// .getBean("licenseManager");
		// LicenseStatus status = licenseManager.getLicenseStatus();
		// if (status != null) {
		// String host = status.getHost();
		// int port = status.getPort();
		// licenseManager.clearLicenseStatus(host, port);
		// }
	}

	private void updateRegisterInfo(LicenseManager licenseManager,
			boolean isSoft) throws Exception {
		String sql1 = "UPDATE as_info t SET t.value=?";
		String sql2 = "UPDATE as_register  SET key=?";
		if(DAOFactory.getWhichFactory() == DAOFactory.MSSQL){
		   sql2 = "UPDATE as_register  SET [key]=?";
		}
		
		if (licenseManager.hasRegistered()) {
			String keyStr = RegisterTools.getKeyStringFromDB();
			keyStr = RegisterTools.decodeString(keyStr);
			String[] temp = keyStr.split(";");
			//未升级
			if (temp.length == 3) {
				String str = "";
				for (int i = 0; i < temp.length - 1; i++) {
					str += ";" + temp[i];
				}
				if (isSoft) {
					str += ";ENCODETYPE[1];" + temp[2];
				} else {
					str += ";ENCODETYPE[0];" + temp[2];
				}
				str = str.substring(1);
				str = RegisterTools.encodeString(str);
				DBHelper.executeUpdate(sql1, new String[] { str });
				DBHelper.executeUpdate(sql2, new String[] { str });
			}
		}
	}

}
