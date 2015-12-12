package com.anyi.gp.license;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.anyi.gp.BusinessException;
import com.anyi.gp.Pub;
import com.anyi.gp.context.ApplusContext;
import com.anyi.gp.core.dao.BaseDao;
import com.anyi.gp.pub.DAOFactory;
import com.anyi.gp.pub.DBHelper;

import java.util.Arrays;

public class LicenseService {

	private static final String SELECT_REGISTER = " select * from as_register ";

	private static final String DELETE_REGISTER = " delete from as_register ";

	private static final String SELECT_SERVER = " select * from as_anyiserver ";

	private static final String DELETE_SERVER = " delete from as_anyiserver ";

	private static final String INSERT_SERVER = " insert into as_anyiserver(host, port, sn, company_count, account_count, products) values(?, ?, ?, ?, ?, ?) ";

	private BaseDao dao;

	public BaseDao getDao() {
		return dao;
	}

	public void setDao(BaseDao dao) {
		this.dao = dao;
	}

	/**
	 * 获取license许可证书
	 * 
	 * @return
	 */
	public License getLicense() {
		List result = dao.queryForListBySql(SELECT_REGISTER, null);
		if (result != null && !result.isEmpty()) {
			Map map = (Map) result.get(0);
			License license = new License();
			license.setAddress((String) map.get("ADDRESS"));
			license.setAgentName((String) map.get("AGENT_NAME"));
			license.setCompanyName((String) map.get("COMPANY_NAME"));
			license.setExpiredDate((String) map.get("EXPIRE_DATE"));
			license.setLinkMan((String) map.get("LINKMAN"));
			license.setLinkTel((String) map.get("LINK_TEL"));
			license.setPostCode((String) map.get("POSTCODE"));
			license.setRegisteDate((String) map.get("REGISTE_TIME"));
			license.setSn((String) map.get("SN"));
			license.setCompanyCount((String) map.get("CO_COUNT"));
			license.setAccountCount((String) map.get("ACC_COUNT"));
			license.setKey((String) map.get("KEY"));
			license.setAllowedProducts((String) map.get("PRODUCT"));

			return license;
		}
		return null;
	}

	/**
	 * 保存license
	 * 
	 * @param license
	 */
	public void saveLicense(License license) {
	  String INSERT_REGISTER = " insert into as_register(sn, company_name, address, postcode, linkman, "
      + " registe_time, link_tel, agent_name, expire_date, co_count, acc_count, product, key) "
      + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
	  if(DAOFactory.getWhichFactory() == DAOFactory.MSSQL){
	    INSERT_REGISTER = " insert into as_register(sn, company_name, address, postcode, linkman, "
	      + " registe_time, link_tel, agent_name, expire_date, co_count, acc_count, product, [key]) "
	      + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
	  }
	  
		dao.executeBySql(DELETE_REGISTER, null);
		dao.executeBySql(INSERT_REGISTER, new Object[] { license.getSn(),
				license.getCompanyName(), license.getAddress(),
				license.getPostCode(), license.getLinkMan(),
				license.getRegisteDate(), license.getLinkTel(),
				license.getAgentName(), license.getExpiredDate(),
				license.getCompanyCount(), license.getAccountCount(),
				license.getAllowedProducts(), license.getKey() });
	}

	public LicenseStatus getLicenseStatus() {
		List result = dao.queryForListBySql(SELECT_SERVER, null);
		if (result != null && !result.isEmpty()) {
			Map map = (Map) result.get(0);
			LicenseStatus licenseStatus = new LicenseStatus();
			licenseStatus.setHost((String) map.get("HOST"));
			licenseStatus.setPort(Pub.parseInt(map.get("PORT")));
			licenseStatus.setSn(RegisterTools.decodeString((String) map
					.get("SN")));
			licenseStatus.setCompanyCount(RegisterTools
					.decodeString((String) map.get("COMPANY_COUNT")));
			licenseStatus.setAccountCount(RegisterTools
					.decodeString((String) map.get("ACCOUNT_COUNT")));
			String strProducts = RegisterTools.decodeString((String) map
					.get("PRODUCTS"));
			List products = Arrays.asList(strProducts.split(","));
			licenseStatus.setAllowedProducts(products);
			return licenseStatus;
		}
		return null;
	}

	public void saveLicenseStatus(LicenseStatus licenseStatus) {
		dao.executeBySql(DELETE_SERVER, null);
		String sn = RegisterTools.encodeString(licenseStatus.getSn());
		String companyCount = RegisterTools.encodeString(licenseStatus
				.getCompanyCount());
		String accountCount = RegisterTools.encodeString(licenseStatus
				.getAccountCount());
		String products = "";
		List productList = licenseStatus.getAllowedProducts();
		for (int i = 0; i < productList.size(); i++) {
			products += productList.get(i) + ",";
		}
		if (products.length() > 0) {
			products = products.substring(0, products.length() - 1);
		}
		String encodeProduct = RegisterTools.encodeString(products);
		dao.executeBySql(INSERT_SERVER, new Object[] { licenseStatus.getHost(),
				licenseStatus.getPort() + "", sn, companyCount, accountCount,
				encodeProduct });
	}

	public void clearLicenseStatus(String host, int port) {
		dao.executeBySql(DELETE_SERVER, null);
		String sql = "insert into as_anyiserver(host, port) values(?, ?)";
		dao.executeBySql(sql, new String[] { host, port + "" });
	}

	/**
	 * 上传license
	 * 
	 * @param str
	 * @throws BusinessException
	 */
	public License uploadLicense(String str) throws BusinessException {
		if (str == null || str.trim().length() == 0) {
			throw new BusinessException("license文件内容不能为空");
		}

		str = ((SocketCommunication) ApplusContext
				.getBean("socketCommunication")).directDecode(str, null);
		String[] text = str.split(":");
		if (text.length != 12) {
			throw new BusinessException("加密文件出错！");
		}

		String keyString = RegisterTools.getKeyStringFromDB();
		keyString = keyString.trim();
		if (!keyString.equals(text[11])) {
			throw new BusinessException("此注册文件不能在本服务器使用！请重新生成密钥文件并注册！");
		}

		String encodeType = RegisterTools.getEncodeType(text[11]);

		if (text[0] == null || text[0].length() == 0) {
			throw new BusinessException("注册文件中的序列号为空！");
		}
		if (encodeType.equals("0")) {
			LicenseStatus status = getLicenseStatus();
			if (status != null) {
				status.setSn();
				String dogSn = status.getSn();
				if (!text[0].equals(dogSn)) {
					throw new BusinessException("注册文件中的序列号有误！");
				}
			} else {
				throw new BusinessException("请先启动加密服务！");
			}
		}

		String companyCount = text[8];
		String accountCount = text[9];
		// List products = Arrays.asList(text[10].split(","));

		License license = new License();
		license.setSn(RegisterTools.encodeString(text[0]));
		license.setCompanyName(RegisterTools.encodeString(text[1]));
		license.setAddress(RegisterTools.encodeString(text[2]));
		license.setPostCode(RegisterTools.encodeString(text[3]));
		license.setLinkMan(RegisterTools.encodeString(text[4]));
		license.setLinkTel(RegisterTools.encodeString(text[5]));
		license.setAgentName(RegisterTools.encodeString(text[6]));
		license.setExpiredDate(RegisterTools.encodeString(text[7]));
		license.setCompanyCount(RegisterTools.encodeString(companyCount));
		license.setAccountCount(RegisterTools.encodeString(accountCount));
		license.setAllowedProducts(RegisterTools.encodeString(text[10]));
		license.setKey(keyString);

		saveLicense(license);

		return license;
	}

	public List getAppNames() {
		String sql = "select distinct(product_code) as PRODUCT_CODE from as_product_ver";
		List appNames = new ArrayList();
		List result = dao.queryForListBySql(sql, null);
		if (result != null & !result.isEmpty()) {
			for (int i = 0; i < result.size(); i++)
				appNames.add(((Map) result.get(i)).get("PRODUCT_CODE"));
		}
		return appNames;
	}

	public String[] getCACountFromAnyiserver() {
		String[] result = new String[2];
		String sql = "SELECT company_count, account_count FROM as_anyiserver";
		try {
			Map data = dao.queryForMapBySql(sql, null);
			result[0] = RegisterTools.decodeString((String) data
					.get("COMPANY_COUNT"));
			result[1] = RegisterTools.decodeString((String) data
					.get("ACCOUNT_COUNT"));
			return result;
		} catch (Exception ex) {
			return null;
		}
	}

	public List getProductFromAnyiserver() {
		String sql = "SELECT products FROM as_anyiserver";
		List product = new ArrayList();
		try {
			Map data = dao.queryForMapBySql(sql, null);
			String strProduct = RegisterTools.decodeString((String) data
					.get("PRODUCTS"));
			product = Arrays.asList(strProduct.split(","));
		} catch (Exception ex) {
		}
		return product;
	}
	
	public void updateKeyStringToDB(String encodedKey){
	  String sql = " update as_info set value = ? where key = ? ";
    if (DAOFactory.getWhichFactory() == DAOFactory.MSSQL) {
      sql = " update as_info set value = ? as_info where [key] = ? ";
    }
    
    DBHelper.executeSQL(sql, new Object[]{encodedKey, RegisterTools.LICENSE_KEY});
	}
	
	public void autoUpgradeKeyInfo(License license){
	  String decodedKey = RegisterTools.decodeString(license.getKey());
	  if(decodedKey.indexOf("DBServerURL[") < 0){
	    decodedKey = "DBServerURL[" + RegisterTools.getDBServerURL() + "];" + decodedKey;
	    license.setKey(RegisterTools.encodeString(decodedKey));
	    saveLicense(license);
	    updateKeyStringToDB(license.getKey());
	  }
	}
}
