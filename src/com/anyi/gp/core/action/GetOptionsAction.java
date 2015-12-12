package com.anyi.gp.core.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.anyi.gp.Pub;
import com.anyi.gp.pub.GeneralFunc;

public class GetOptionsAction extends AjaxAction {

	/**
	 * 获得系统选项
	 * 
	 * @author guohui
	 */
	private static final long serialVersionUID = 1L;

	private String optIds;

	private String coCode;

	private String compoId;

	private String transType;

	private String type;

	// private static final String SQL_ID = "gmap-common.getOptions";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCoCode() {
		return coCode;
	}

	public void setCoCode(String coCode) {
		this.coCode = coCode;
	}

	public String getCompoId() {
		return compoId;
	}

	public void setCompoId(String compoId) {
		this.compoId = compoId;
	}

	public String getOptIds() {
		return optIds;
	}

	public void setOptIds(String optIds) {
		this.optIds = optIds;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String doExecute() throws Exception {
		// TCJLODO Auto-generated method stub
		if (optIds == null || optIds.length() == 0)
			Pub.makeRetString(false, "参数无效.");
		StringBuffer voBuf = new StringBuffer();
		if (type != null && type.equals("one")) {
			String result = GeneralFunc.getOneOption(optIds);
			voBuf.append("<row ");
			voBuf.append("OPT_VAL=\"").append(result).append(
					"\" ");
			voBuf.append(" />\n");
		} else {
			List optIdList = new ArrayList();
			String[] st = optIds.split(",");
			for (int i = 0; i < st.length; i++) {
				optIdList.add(st[i].trim());
			}
			List resultGet = (List) GeneralFunc.getOptions(optIdList, coCode,
					compoId, transType);
			for (Iterator iter = resultGet.iterator(); iter.hasNext();) {
				Map temp = (Map) iter.next();
				voBuf.append("<row ");
				voBuf.append("OPT_ID=\"").append(temp.get("OPT_ID")).append(
						"\" ");
				voBuf.append("OPT_VAL=\"").append(temp.get("OPT_VAL") == null ? "" : temp.get("OPT_VAL")).append(
						"\" ");
				voBuf.append(" />\n");
			}
		}
		this.resultstring = Pub.makeRetString(true, voBuf.toString());
		return SUCCESS;
	}

}
