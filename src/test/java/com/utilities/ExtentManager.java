package com.utilities;

import java.util.Date;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	public static ExtentReports extent;
	public static ExtentSparkReporter reporter;
	public static ExtentReports getInstance() {
		if(null==extent) {
			Date d=new Date();
			String fileName="jenkins_"+d.toString().replace(":", "_").replace(" ", "_")+".html";
			String path=System.getProperty("user.dir")+"\\target\\surefire-reports\\reports\\"+fileName;
	        reporter = new ExtentSparkReporter(path);
	        reporter.config().setTheme(Theme.STANDARD);
	        reporter.config().setDocumentTitle("Automation Reports");
	        reporter.config().setEncoding("utf-8");
	        reporter.config().setReportName("Data driven grid framework automation test report");

	        extent = new ExtentReports();
			extent.attachReporter(reporter);
			extent.setSystemInfo("Automation Tester", "Geetinder");
			extent.setSystemInfo("Organization", "Accenture");
			extent.setSystemInfo("Build No.", "Data Driven Grid");
		}
		return extent;
	}
}
