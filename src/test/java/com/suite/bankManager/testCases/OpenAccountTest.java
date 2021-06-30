package com.suite.bankManager.testCases;

import java.net.MalformedURLException;
import java.util.Hashtable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.dataDriven.base.TestBase;
import com.utilities.Constants;
import com.utilities.DataProviders;
import com.utilities.DataUtil;
import com.utilities.ExcelReader;

public class OpenAccountTest extends TestBase {
	
	@Test(dataProviderClass=DataProviders.class,dataProvider="bankManagerDP")
	public void openAccountTest(Hashtable<String,String> data) throws MalformedURLException {
		super.setUp();
		test=rep.createTest("OpenAccountTest"+"   "+data.get("browser")); //extent reports
		setExtentTest(test);
		System.out.println(data.get("Runmode")+"---"+data.get("customer")+"---"+data.get("currency"));
		ExcelReader excel=new ExcelReader(Constants.SUITE1_XL_PATH);
		DataUtil.checkExecution("BankManagerSuite", "OpenAccountTest", data.get("Runmode"), excel);
		openBrowser(data.get("browser"));
		navigate("testSiteUrl");
		click("bmlBtn_CSS");
		click("openAccount_CSS");
		select("customer_ID",data.get("customer"));
		select("currency_ID",data.get("currency"));
		click("process_CSS");
		reportPass("Open Account Test Passed");
	}
	
	@AfterMethod
	public void tearDown() {
		if(rep!=null) { 
			rep.flush();  //extent report flush
		}
		getDriver().quit();
	}

}
