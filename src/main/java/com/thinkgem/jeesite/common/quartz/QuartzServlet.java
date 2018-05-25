package com.thinkgem.jeesite.common.quartz;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;



/**
 * Servlet implementation class QuartzServlet
 */
@WebServlet("/QuartzServlet")
public class QuartzServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public QuartzServlet(){
		System.out.println("---------QuartzServlet 初始化 ------------");
	}

	public void init(ServletConfig config) throws ServletException {
		QuartzUtil quartzUtil = new QuartzUtil();

		try {
			//quartzUtil.doJointWait();
			quartzUtil.doCreartCombinationOrder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
