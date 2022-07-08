package com.utilities;

import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentDriver {

	static ExtentHtmlReporter htmlReport;

	public static ExtentReports getInstance(String path, String reportName, String documentTitle) {
		htmlReport = new ExtentHtmlReporter(path);
		htmlReport.config().setDocumentTitle(documentTitle); // Report Title
		htmlReport.config().setReportName(reportName); // Report Name
		htmlReport.config().setTheme(Theme.DARK);
		ExtentReports extent;
		extent = new ExtentReports();
		extent.attachReporter(htmlReport);
		extent.setSystemInfo("OS", "MAC");

		return extent;
	}

	public static void verifyResilt(ITestResult result, ExtentTest test) {
		if (result.getStatus() == ITestResult.FAILURE) {
			test.log(Status.FAIL, "Test Case Failed: " + result.getName()); // to add name in the report
			test.log(Status.FAIL, "Test Case Failed with " + result.getThrowable()); // to add exception in the report

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(Status.SKIP, "Test case Skipped is: " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(Status.PASS, "Test cae Passed is: " + result.getName());
		}
	}
}
