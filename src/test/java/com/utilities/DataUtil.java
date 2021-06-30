package com.utilities;

import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;

public class DataUtil {
	
	public static void checkExecution(String testSuiteName, String testCaseName, String dataRunMode, ExcelReader excel) {
		if(!isSuiteRunnable(testSuiteName)) {
			throw new SkipException("Skipping the test: "+testCaseName+" as the runmode of Test Suite: "+testSuiteName+" is NO");
		}
		if(!isTestRunnable(testCaseName, excel)) {
			throw new SkipException("Skipping the test: "+testCaseName+" as it's runmode is NO");
		}
		if(dataRunMode.equalsIgnoreCase(Constants.RUNMODE_NO)) {
			throw new SkipException("Skipping the test: "+testCaseName+" as the run mode for the data set is NO");
		}
	}

	public static boolean isSuiteRunnable(String suiteName) {
		ExcelReader excel=new ExcelReader(Constants.SUITE_XL_PATH);
		int rows=excel.getRowCount(Constants.SUITE_SHEET);
		for(int rowNum=2;rowNum<=rows;rowNum++) {
			String data=excel.getCellData(Constants.SUITE_SHEET, Constants.SUITENAME_COL, rowNum);
			if(data.equalsIgnoreCase(suiteName)) {
				String runMode=excel.getCellData(Constants.SUITE_SHEET, Constants.RUNMODE_COL, rowNum);
				if(runMode.equals(Constants.RUNMODE_YES)) 
					return true;
				else
					return false;
			}
		}
		return false;
	}
	public static boolean isTestRunnable(String testCaseName, ExcelReader excel) {
		int rows=excel.getRowCount(Constants.TESTCASE_SHEET);
		for(int rowNum=2;rowNum<=rows;rowNum++) {
			String data=excel.getCellData(Constants.TESTCASE_SHEET, Constants.TESTCASES_COL, rowNum);
			if(data.equalsIgnoreCase(testCaseName)) {
				String runMode=excel.getCellData(Constants.TESTCASE_SHEET, Constants.RUNMODE_COL, rowNum);
				if(runMode.equals(Constants.RUNMODE_YES)) 
					return true;
				else
					return false;
			}
		}
		return false;
	} 
	
	@DataProvider
	public static Object[][] getData(String testCase, ExcelReader excel){
		
		//ExcelReader excel=new ExcelReader(System.getProperty("user.dir")+"\\src\\test\\resources\\testData\\BankManagerSuite.xlsx");
		
		int rows=excel.getRowCount(Constants.DATA_SHEET);
		System.out.println("Row count is: "+rows);

		String testName=testCase;

		//Find test case start row
		int testCaseRowNum=1;
		for(testCaseRowNum=1; testCaseRowNum<=rows;testCaseRowNum++) {
			String testCaseName=excel.getCellData(Constants.DATA_SHEET, 0, testCaseRowNum);
			if (testCaseName.equalsIgnoreCase(testName)) {
				break;
			}
		}
		System.out.println("Test case: "+testName+" starts from row: "+testCaseRowNum);

		//checking total rows in testcase
		int dataStartRowNum=testCaseRowNum+2;
		int testRows=0;
		while(!excel.getCellData(Constants.DATA_SHEET, 0, dataStartRowNum+testRows).equals("")) {
			testRows++;
		}
		System.out.println("Test case: "+testName+" has data in total rows: "+testRows);

		//checking total columns in testcase
		int colStartNum=testCaseRowNum+1;
		int testCols=0;
		while(!excel.getCellData(Constants.DATA_SHEET, testCols, colStartNum).equals("")) {
			testCols++;
		}
		System.out.println("Test case: "+testName+" has total columns: "+testCols);

		//printing data from test case
		Object[][] data =new Object[testRows][1];
		System.out.println("Printing Test Data from testCase: "+testName);
		int i=0;
		for(int rnum=dataStartRowNum;rnum<dataStartRowNum+testRows;rnum++) {
			Hashtable<String,String> table=new Hashtable<String,String>();
			for(int cnum=0;cnum<testCols;cnum++) {
				String testData=excel.getCellData(Constants.DATA_SHEET, cnum, rnum);
				String colName=excel.getCellData(Constants.DATA_SHEET, cnum, colStartNum);
				table.put(colName, testData);
			}
			data[i][0]=table;
			i++;
		}
		return data;
	}
}



