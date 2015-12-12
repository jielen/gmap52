package com.anyi.gp.core.action;

import org.apache.log4j.Logger;

import com.anyi.gp.pub.GeneralFunc;

public class GetPeriodAction extends AjaxAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2059282177614156405L;
	
	private static final Logger log = Logger.getLogger(GetPeriodAction.class);
	
	private String iYea = null;
	
	private String iMont = null;
	
	private String iDa = null;
	  
	public String doExecute() throws Exception {
		if(null == iYea || null == iMont || null == iDa){
			this.resultstring = wrapResultStr("false", "传入参数不全");
			return SUCCESS;
		}
		String curPeriod = GeneralFunc.getPeriod(iYea, iMont, iDa);
		curPeriod = curPeriod.substring(0, curPeriod.indexOf("-"));
		this.resultstring = wrapResultStr("true", String.valueOf(curPeriod) );
		return SUCCESS;
	}

	public String getIDa() {
		return iDa;
	}

	public void setIDa(String da) {
		iDa = da;
	}

	public String getIMont() {
		return iMont;
	}

	public void setIMont(String mont) {
		iMont = mont;
	}

	public String getIYea() {
		return iYea;
	}

	public void setIYea(String yea) {
		iYea = yea;
	}

}
