package com.anyi.gp.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.anyi.gp.workflow.userInterface.WorkflowUserFilter;
import com.anyi.gp.workflow.util.WFUtil;

public class ConfigLoaderListener implements ServletContextListener {

	private static final Logger logger = Logger
			.getLogger(ConfigLoaderListener.class);

	private static final String ON_LINE_LIST_KEY = "0xFF";

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		initUserInterface(context);
		ApplusContext.setWebApplicationContext(WebApplicationContextUtils
				.getWebApplicationContext(event.getServletContext()));
		logger.debug("\ninitialized.");
	}

	private void initUserInterface(ServletContext context) {
		String usrFilter = context.getInitParameter("workflowUserFilter");
		if (usrFilter == null || usrFilter.trim().equals(""))
			return;
		String[] filters = usrFilter.split(",");
		for (int i = 0; i < filters.length; i++) {
			String filterName = filters[i];
			try {
				WorkflowUserFilter wuf = (WorkflowUserFilter) Class.forName(
						filterName).newInstance();
				WFUtil.userFileters.add(wuf);
			} catch (InstantiationException e) {
				// TCJLODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TCJLODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TCJLODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
	}
}
