package com.anyi.gp.message.jms;

import java.util.HashMap;
import java.util.Map;

import com.anyi.gp.message.jms.util.SpringUtils;

public class SendTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TCJLODO Auto-generated method stub
		try {
			JmsMAO mao = (JmsMAO)SpringUtils.getBean("jmsMAO");
			Map map = new HashMap();
			map.put("instanceId", "111");
			map.put("templateId", "2504");
			map.put("compoId", "BI_ABI_XXX");
			map.put("wfData", "<entity name=\"\"><field name=\"WF_TEMPLATE_ID\" value=\"2504\" /><field name=\"WF_COMPANY_CODE\" value=\"003000\" /><field name=\"WF_ORG_CODE\" value=\"0001\" /><field name=\"WF_POSITION_ID\" value=\"111863853242200002\" /><field name=\"ND\" value=\"2008\" /><entity name=\"WF_VARIABLE\"><row><entity name=\"\"><field name=\"VariableName\" value=\"svEmpCode\" /><field name=\"VariableValue\" value=\"003000\" /></entity></row></entity>");
			mao.sendMapMessage("NewCommitQueue", map, null);						
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
