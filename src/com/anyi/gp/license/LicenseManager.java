 package com.anyi.gp.license;

import java.util.List;

import com.anyi.gp.BusinessException;

/**
 * 
 * ���ܡ�ע������� ����Ӳ�����ܺ���������ַ�ʽ��������ڼ��ܹ���ʹ��Ӳ�����ܣ�����ʹ������� Ϊ�˼��ݼ�Ⱥ���ݲ�ʵ�ֻ���
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
	 * ����Ƿ�ע�� as_register���д��ڼ�¼������key������򷵻�true
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
	 * ʣ�������
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
	 * �Ƿ�Ϊ��ʾ��
	 * 
	 * @return
	 */
	public boolean isDemo() {
		int dogstate = getDogState();
		switch (dogstate) {
			case 0:return !this.hasRegistered();//��ʱ��Ϊ�������,Ӧ����true or false?
			case 1:return true;
			case 2:return false;
			default:return true;
		}
		//return !hasSoftDog() || !this.hasRegistered();
	}

	/**
	 * �Ƿ���ܹ�
	 */
	public int getDogState() {
		LicenseStatus licenseStatus = service.getLicenseStatus();
		if (licenseStatus == null)
			return 0;//�����
		String dogSn = licenseStatus.getSn();
		if (dogSn == null || "".equals(dogSn)) return 1;//��Ϊ�й��������������ܷ����幷
		String sn = this.getSn();
		if (sn != null) {
			if (!dogSn.equals(sn))
				return 1;//ע�ᣬ��sn��һ��,��Ϊ�й�����δע��
		}
		return 2;
	}

	/**
	 * ��ȡ����Ĳ�Ʒ��
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
	 * ȡ����ĵ�λ��
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
	 * ȡ�����������
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
	 * �ϴ�license
	 * 
	 * @param str
	 * @throws BusinessException
	 */
	public void uploadLicense(String str) throws BusinessException {
		License license = service.uploadLicense(str);
	}

	/**
	 * ���ü��ܷ���
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
		//ע���е�sn�͹���sn��һ�£�������д��¼
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

	// ����Ƿ���Ե���
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
		//��δע��
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
//		��δע��
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
		if (dogstate == 0 && !register) {//�����
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע������";
		}
		if (dogstate == 1) {
			return "�����Ϊ��ʾ�棬Ϊ������ĺϷ�Ȩ�棬��ʹ���������";
		}
		if (!register) {
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע������";
		}
		if (register && leftDays < 0) {
			return "���ע�����ѹ�������ϵ��Ӫ���������ʱע�ᡣ";
		}
		if (register && leftDays >= 0 && leftDays < 31) {
			return "��ע�ᵽ���ջ���" + leftDays + "�죬����ϵ��Ӫ������ʱע�ᡣ";
		}
		return "";
	}

	public String getTitleMessage(String user) {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();

		if (dogstate == 0 && !register) {//�����
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע�����ˡ�";
		}
		if (dogstate == 1) {
			return "�����Ϊ��ʾ�棬Ϊ������ĺϷ�Ȩ�棬��ʹ�����������";
		}
		if (!register) {
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע�����ˡ�";
		}
		if (register && leftDays < 0) {
			return user + "���ϴ�ע���ѹ��ڣ�����ϵ�������������ʱע�ᣩ";
		}
		if (register && leftDays >= 0 && leftDays < 31) {
			return "��Ȩ��" + user + "���౾��ע�ᵽ�ڻ���" + leftDays + "�죩��";
		}
		if (register && leftDays > 31) {
			return "��Ȩ��" + user;
		}
		return "";
	}

	public String getWaterMarkText() {
		int dogstate = getDogState();
		boolean register = hasRegistered();
		int leftDays = getLeftDays();

		if (dogstate == 0 && !register) {
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע�����ˡ�";
		}
		if (dogstate == 1) {
			return "�����Ϊ��ʾ�棬Ϊ������ĺϷ�Ȩ�棬��ʹ�����������";
		}
		if (!register) {
			return "���δע�ᣬΪȷ�����ܼ�ʱ�õ���������̵ķ�������ϵ���������������ע�����ˡ�";
		}
		if (register && leftDays < 0) {
			return "�ϴ�ע���ѹ��ڣ�����ϵ�������������ʱע��";
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
