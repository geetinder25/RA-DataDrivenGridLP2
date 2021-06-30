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

public class AddCustomerTest extends TestBase {
	
	@Test(dataProviderClass=DataProviders.class,dataProvider="bankManagerDP")
	public void addCustomerTest(Hashtable<String,String> data) throws MalformedURLException {
		super.setUp();
		test=rep.createTest("AddCustomerTest"+"   "+data.get("browser")); //extent reports
		setExtentTest(test);
		System.out.println(data.get("Runmode")+"---"+data.get("firstname")+"---"+data.get("postcode"));
		ExcelReader excel=new ExcelReader(Constants.SUITE1_XL_PATH);
		DataUtil.checkExecution("BankManagerSuite", "AddCustomerTest", data.get("Runmode"), excel);
		openBrowser(data.get("browser"));
		navigate("testSiteUrl");
		click("bmlBtn_CSS");
		click("addCustBtn_CSS");
		type("firstName_CSS",data.get("firstname"));
		type("lastName_XPATH",data.get("lastname"));
		type("postCode_CSS",data.get("postcode"));
		click("submitCustBtn_CSS");
		reportPass("Add Customer Test Passed");
	}
	
	@AfterMethod
	public void tearDown() {
		if(rep!=null) { 
			rep.flush();  //extent report flush
		}
		getDriver().quit();
	}

}
