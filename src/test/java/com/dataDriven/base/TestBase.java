package com.dataDriven.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.utilities.ExtentManager;

public class TestBase {
	/*
	 * WebDriver
	 * logs
	 * properties
	 * excel
	 * db
	 * mail
	 * extent reports
	 */
	public static ThreadLocal<RemoteWebDriver> dr=new ThreadLocal<RemoteWebDriver>();
	public RemoteWebDriver driver=null;
	public Properties OR=new Properties();
	public Properties Config =new Properties();
	public FileInputStream fis;
	public Logger logger=Logger.getLogger(TestBase.class.getName());
	public WebDriverWait wait;
	public ExtentReports rep=ExtentManager.getInstance();
	public ExtentTest test;
	public static ThreadLocal<ExtentTest> exTest=new ThreadLocal<ExtentTest>();
	
	//logging msgs with browser names
	public String browser;
	public void addLog(String message) {
		logger.info("Thread value is: "+getThreadValue(dr.get())+", Browser: "+browser+":- "+message);
	}
	
	//logging msgs with thread name too
	public String getThreadValue(Object value) {
		String text=value.toString();  //dr.get()= RemoteWebDriver: firefox on LINUX (a94dc348-9911-45ad-b1d9-f0512b72e1f4)
		String[] aText=text.split(" ");
		String n1Text=aText[aText.length-1].replace("(", "").replace(")", ""); // a94dc348-9911-45ad-b1d9-f0512b72e1f4
		String[] a1Text=n1Text.split("-");
		String n2Text=a1Text[a1Text.length-1]; //f0512b72e1f4
		return n2Text;
	}

	//screenshot code
	public static String screenshotPath;
	public static String screenshotName;
	public void captureScreenshot() {
		File scrFile=((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		Date d=new Date();
		screenshotName="jenkins_"+d.toString().replace(":", "_").replace(" ", "_")+".jpg";
		getExtentTest().log(Status.INFO,"Taking screenshot");
		addLog("Taking screenshot");
		try {
			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"\\target\\surefire-reports\\reports\\"+screenshotName));
			getExtentTest().log(Status.INFO, " Screenshot -> "+ test.addScreenCaptureFromPath(System.getProperty("user.dir")+"\\target\\surefire-reports\\reports\\"+screenshotName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUp() {
		Date d =new Date();
		System.setProperty("current.date", d.toString().replace(":", "_").replace(" ", "_"));
		PropertyConfigurator.configure(".\\src\\test\\resources\\properties\\log4j.properties");
		
		if(driver==null) {
			try {
				fis=new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\resources\\properties\\Config.properties");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				Config.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				fis=new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\resources\\properties\\OR.properties");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				OR.load(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//making driver and extent report logging thread safe
	public WebDriver getDriver() {
		return dr.get();
	}
	public void setWebDriver(RemoteWebDriver driver) {
		dr.set(driver);
	}
	public void setExtentTest(ExtentTest test) {
		exTest.set(test);
	}
	public ExtentTest getExtentTest() {
		return exTest.get();
	}

	public void openBrowser(String browser) throws MalformedURLException {
		DesiredCapabilities cap=null;
		if(browser.equalsIgnoreCase("firefox")) {
			cap=DesiredCapabilities.firefox();
			cap.setBrowserName("firefox");
			cap.setPlatform(Platform.ANY);
		} else if(browser.equalsIgnoreCase("chrome")) {
			cap=DesiredCapabilities.chrome();
			cap.setBrowserName("chrome");
			cap.setPlatform(Platform.ANY);
		} else if (browser.equalsIgnoreCase("ie")) {
			cap=DesiredCapabilities.internetExplorer();
			cap.setBrowserName("iexplore");
			cap.setPlatform(Platform.WINDOWS);
		}
		driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),cap);
		setWebDriver(driver);
		getDriver().manage().timeouts().implicitlyWait(Integer.parseInt(Config.getProperty("implicitWait")), TimeUnit.SECONDS);
		getDriver().manage().window().maximize();
		this.browser=browser;
		getExtentTest().log(Status.INFO, "Browser opened successfully "+browser);
		addLog("Browser opened successfully "+browser);
		/* //printing thread name
		//System.out.println(dr.get());   //RemoteWebDriver: firefox on LINUX (a94dc348-9911-45ad-b1d9-f0512b72e1f4)
		System.out.println("Thread value is: "+getThreadValue(dr.get()));   */
	}

	public void navigate(String url) {
		getDriver().get(Config.getProperty(url));
		getExtentTest().log(Status.INFO, "Navigating to "+Config.getProperty(url));
		addLog("Navigating to "+Config.getProperty(url));
	}

	public void reportPass(String msg) {
		getExtentTest().log(Status.PASS, msg);
	}

	public void reportFail(String msg) {
		getExtentTest().log(Status.FAIL, msg);
		captureScreenshot();
		Assert.fail(msg);
	}

	//common keywords implementation
	public void click(String locator) {
		try {
			if(locator.endsWith("_CSS")) {
				getDriver().findElement(By.cssSelector(OR.getProperty(locator))).click();
			} else if(locator.endsWith("_XPATH") ) {
				getDriver().findElement(By.xpath(OR.getProperty(locator))).click();
			} else if(locator.endsWith("_ID")) {
				getDriver().findElement(By.id(OR.getProperty(locator))).click();
			} 
			getExtentTest().log(Status.INFO, "Clicking on: "+locator);
			//logger.debug("Clicking on an element: "+locator);
			addLog("Clicking on an element: "+locator);
		} catch (Throwable t) {
			reportFail("Failing while clicking on an element: "+locator);
			addLog("Failing while clicking on element: "+locator);
		}
	}
	public void type(String locator, String value) {
		try {
			if(locator.endsWith("_CSS")) {
				getDriver().findElement(By.cssSelector(OR.getProperty(locator))).sendKeys(value);
			} else if(locator.endsWith("_XPATH")) {
				getDriver().findElement(By.xpath(OR.getProperty(locator))).sendKeys(value);
			} else if(locator.endsWith("_ID")) {
				getDriver().findElement(By.id(OR.getProperty(locator))).sendKeys(value);
			}
			getExtentTest().log(Status.INFO, "Typing in: "+locator+" entered value as: "+value);
			addLog("Typing in: "+locator+" entered value as: "+value);
		} catch (Throwable t) {
			reportFail("Failing while typing in an element: "+locator);
			addLog("Failing while typing in an element: "+locator);
		}

	} 

	static WebElement dropdown;
	public void select(String locator, String value) {
		try {
			if(locator.endsWith("_CSS")) {
				dropdown=getDriver().findElement(By.cssSelector(OR.getProperty(locator)));
			} else if(locator.endsWith("_XPATH")) {
				dropdown=getDriver().findElement(By.xpath(OR.getProperty(locator)));
			} else if(locator.endsWith("_ID")) {
				dropdown=getDriver().findElement(By.id(OR.getProperty(locator)));
			}
			Select select =new Select(dropdown);
			select.selectByVisibleText(value);
			getExtentTest().log(Status.INFO, "Selecting from dropdown: "+locator+" value as: "+value);
			addLog("Selecting from dropdown: "+locator+" value as: "+value);
		} catch (Throwable t) {
			reportFail("Failing while selecting an element: "+locator);
			addLog("Failing while selecting an element: "+locator);
		}
	} 

	public boolean isElementPresent(By by) {
		try {
			getDriver().findElement(by);
			return true;
		}catch(NoSuchElementException e) {
			return false;
		}
	}

}
