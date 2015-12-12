package com.anyi.gp.print.action;

import javax.servlet.http.HttpServletRequest;

import com.anyi.gp.BusinessException;
import com.anyi.gp.core.action.AjaxAction;
import com.anyi.gp.pub.ServiceFacade;
import com.anyi.gp.pub.SessionUtils;
import com.opensymphony.webwork.ServletActionContext;

/**
 * @author hmgkevin
 * @date 2008-03-17
 */
public class SavePrintTemplateAction extends AjaxAction {

	private static final long serialVersionUID = 1L;
	private ServiceFacade printParameter;

	public ServiceFacade getPrintParameter() {
		return printParameter;
	}

	public void setPrintParameter(ServiceFacade printParameter) {
		this.printParameter = printParameter;
	}

	public String doExecute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String data = request.getParameter("data");
		String componame = request.getParameter("componame");
		String svUserId = SessionUtils.getAttribute(request, "svUserID");
		/**
		 * 下面调用ServiceFacade类中的保存数据函数，将数据保存到数据库中
		 */
    try{
		String result = this.getPrintParameter().save(data, "insert", componame, svUserId);
		this.resultstring = this.wrapResultStr("true", result);
    }
    catch(BusinessException e){
      this.resultstring = this.wrapResultStr("false", e.getMessage());
    }
		return SUCCESS;
	}


}
