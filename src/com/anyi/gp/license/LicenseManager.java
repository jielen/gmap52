 package com.anyi.gp.license;

import java.util.List;

import com.anyi.gp.BusinessException;

/**
 * 
 * 加密、注册管理类 采用硬件加密和软加密两种方式，如果存在加密狗则使用硬件加密，否则使用软加密 为了兼容集群，暂不实现缓存
 * 
 */
public class LicenseManager {

	private LicenseService service;

	public LicenseManager() {
	}

	public LicenseManager(LicenseService service) {
		this.service = service;
	}

	/**
	 * 检查是否注册 as_register表中存在记录，而且key相符合则返回true
	 * 
	 * @return
	 */
	public boolean hasRegistered() {
		License license = service.getLicense();
		if (license == null)
			return false;
		String sn = license.getSn();
		if (sn == null || "".equals(sn))
			return false;
		return license.isLicenseValidity();
	}

	/**
	 * 剩余的天数
	 * 
	 * @return
	 */
	public int getLeftDays() {
		License license = service.getLicense();
		if (license == null)
			return -1;
		return license.getLeftDays();
	}

	public String getCompanyName() {
		License license = service.getLicense();
		if (license == null)
			return null;
		return license.getDecodeCompanyName();
	}

	/**
	 * 是否为演示版
	 * 
	 * @return
	 */
	public boolean isDemo() {
		int dogstate = getDogState();
		switch (dogstate) {
			case 0:return !this.hasRegistered();//此时认为是软加密,应该是true or false?
			case 1:return true;
			case 2:return false;
			default:return true;
		}
		//return !hasSoftDog() || !this.hasRegistered();
	}

	/**
	 * 是否加密狗
	 */
	public int getDogState() {
		LicenseStatus licenseStatus = service.getLicenseStatus();
		if (licenseStatus == null)
			return 0;//软加密
		String dogSn = licenseStatus.getSn();
		if (dogSn == null || "".equals(dogSn)) return 1;//认为有狗，但无启动加密服务或插狗
		String sn = this.getSn();
		if (sn != null) {
			if (!dogSn.equals(sn))
				return 1;//注册，但sn不一致,认为有狗，但未注册
		}
		return 2;
	}

	/**
	 * 获取允许的产品数
	 * 
	 * @return
	 */
	public List getAllowedProducts() {
		License license = service.getLicense();
		if (license != null) {
			return license.getDecodeAllowedProducts();
		} else {
			return getProductFromAnyiserver();
		}
	}

	public String getSn() {
		String sn = null;
		License license = service.getLicense();
		if (license != null) {
			sn = license.getDecodeSn();
			sn = sn == null ? "" : sn;
		}
		return sn;
	}

	/**
	 * 取允许的单位数
	 * 
	 * @return
	 */
	public int getCompanyCount() {
		License license = service.getLicense();
		if (license == null)
			return 0;
		return license.getDecodeCompanyCount();
	}

	/**
	 * 取允许的账套数
	 * 
	 * @return
	 */
	public int getAccountCount() {
		License license = service.getLicense();
		if (license == null)
			return 0;
		return license.getDecodeAccountCount();
	}

	/**
	 * 上传license
	 * 
	 * @param str
	 * @throws BusinessException
	 */
	public void uploadLicense(String str) throws BusinessException {
		License license = service.uploadLicense(str);
	}

	/**
	 * 启用加密服务
	 * 
	 * @param host
	 * @param port
	 */
	public void startWebService(String host, int port) {
		LicenseStatus licenseStatus = new LicenseStatus();
		licenseStatus.setHost(host);
		licenseStatus.setPort(port);
		licenseStatus.setSn();
		String sn = licenseStatus.getSn();
		String registerSn = this.getSn();
		//注册中的sn和狗的sn不一致，不允许写记录
		if (registerSn != null) {
			if (!registerSn.equals(sn)) return ;
		}
		licenseStatus.setCompanyCount();
		licenseStatus.setAccountCount();
		licenseStatus.setAllowedProducts(service);
		service.saveLicenseStatus(licenseStatus);
	}

	public void clearLicenseStatus(String host, int port) {
		service.clearLicenseStatus(host, port);
	}

	public String getLincenseStatusInfo() {
		LicenseStatus licenseStatus = service.getLicenseStatus();
		if (licenseStatus != null)
			return licenseStatus.getLincenseStatusInfo();
		return "";
	}

	// 检查是否可以导出
	public boolean canExport() {
//		if (!hasSoftDog())
//			return false;
//		if (!hasRegistered() || getLeftDays() < 0)
//			return false;
//		return true;
		return true;
	}

	public boolean checkCompanyAccount() {
		//if (!hasSoftDog() && !hasRegistered())
			//return false;
		// if (hasSoftDog() && !hasRegistered()) return false;
		int dogstate = getDogState();
		//软未注册
		if (dogstate == 0 && !hasRegistered()) {
			return false;
		} else {
			if (!hasRegistered()) {
				return false;
			}
		}
		return true;
	}

	public boolean checkProduct() {
//		if (!hasSoftDog() && !hasRegistered())
//			return false;
//		return true;
		int dogstate = getDogState();
//		软未注册
		if (dogstate == 0 && !hasRegistered()) {
			return false;
		} else {
			if (!hasRegistered()) {
				return false;
			}
		}
		return true;
	}

	public String getLoginMessage() {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();
		if (dogstate == 0 && !register) {//软加密
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜";
		}
		if (dogstate == 1) {
			return "此软件为演示版，为保障你的合法权益，请使用正版软件";
		}
		if (!register) {
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜";
		}
		if (register && leftDays < 0) {
			return "软件注册期已过，请联系经营服务机构及时注册。";
		}
		if (register && leftDays >= 0 && leftDays < 31) {
			return "距注册到期日还有" + leftDays + "天，请联系经营机构及时注册。";
		}
		return "";
	}

	public String getTitleMessage(String user) {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();

		if (dogstate == 0 && !register) {//软加密
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜。";
		}
		if (dogstate == 1) {
			return "此软件为演示版，为保障你的合法权益，请使用正版软件！";
		}
		if (!register) {
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜。";
		}
		if (register && leftDays < 0) {
			return user + "（上次注册已过期，请联系经销服务机构及时注册）";
		}
		if (register && leftDays >= 0 && leftDays < 31) {
			return "授权给" + user + "（距本次注册到期还有" + leftDays + "天）。";
		}
		if (register && leftDays > 31) {
			return "授权给" + user;
		}
		return "";
	}

	public String getWaterMarkText() {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();

		if (dogstate == 0 && !register) {
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜。";
		}
		if (dogstate == 1) {
			return "此软件为演示版，为保障你的合法权益，请使用正版软件！";
		}
		if (!register) {
			return "软件未注册，为确保您能及时得到软件开发商的服务，请联系经销服务机构办理注册事宜。";
		}
		if (register && leftDays < 0) {
			return "上次注册已过期，请联系经销服务机构及时注册";
		}
		return "";
	}

	public String getWaterMarkImage() {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();
		
		if (dogstate == 0 && !register) {
			return "/img/printtpl/noRegister.jpg";
		}
		if (dogstate == 1) {
			return "/img/printtpl/demo.jpg";
		}
		if (dogstate == 2 && !register) {
			return "/img/printtpl/noRegister.jpg";
		}
		if (register && leftDays < 0) {
			return "/img/printtpl/regExpire.jpg";
		}
		return "";
	}

	public boolean canPassByca() {
		int companyCount = getCompanyCount();
		int accountCount = getAccountCount();
		if (companyCount == 0 || accountCount == 0) {
			String[] data = getCACountFromAnyiserver();
			if (data != null) {
				companyCount = Integer.parseInt(data[0]);
				accountCount = Integer.parseInt(data[1]);
			}
		}
		SystemStatus systemStatus = new SystemStatus();
		return companyCount >= systemStatus.getCompanyCount()
				&& accountCount >= systemStatus.getAccountCount();
	}

	public LicenseStatus getLicenseStatus() {
		return service.getLicenseStatus();
	}

	public String[] getCACountFromAnyiserver() {
		String[] result = service.getCACountFromAnyiserver();
		return result;
	}

	public List getProductFromAnyiserver() {
		List product = service.getProductFromAnyiserver();
		return product;
	}
}
