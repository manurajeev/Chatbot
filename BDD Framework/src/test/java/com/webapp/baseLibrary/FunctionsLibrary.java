package com.webapp.baseLibrary;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.thoughtworks.selenium.SeleniumException;
import com.webapp.executioner.ExecutionerClass;
//import com.webapp.executioner.ExecutionerClass1;
import com.webapp.stepDefinition.CommonStepDefinitions;
import com.webapp.utilities.EncryptFileData;
import com.webapp.utilities.GridReporter;

public class FunctionsLibrary {
	public static String strAbsolutepath = new File("").getAbsolutePath();
	public static String strVBScriptsPath = strAbsolutepath + "/VBScripts/";

	static Properties object, browserLoad = null;
	static WebDriverWait wait;
	private static Map<String, String> envVariableMap = new HashMap();
	public static WebDriver driver = null;

	public static Map<String, String> handlesWithId = new HashMap<>();
	String sysPropFromFile = "Config/Sys.properties";
	int getWaitTime = 0;
	int elementCheckCount = 3;

	public FunctionsLibrary() {
		if (driver == null) {
			Properties config;
			try {
				config = new ExecutionerClass().setEnv();
				initializeBrowser(config.getProperty("browser"));
				getWaitTime = getFluentWaitTime();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String objectFileName = "src/test/resources/config/object.properties";
		object = loadPropertiesFile(objectFileName);
	}

	public String tc_id = CommonStepDefinitions.tc_id;
	public String scenarioName = CommonStepDefinitions.scenarioName;
	public String dataFileName = CommonStepDefinitions.dataFileName;
	public String strReportFilename = "";
	static GridReporter reporter = CommonStepDefinitions.getReporter();

	public String getReportFileName() {
		return strReportFilename;
	}

	public static void initializeBrowser(String browser) {
		browser = browser.toLowerCase().trim();
		switch (browser) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;
		case "firefox":
			driver = new FirefoxDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;
		/*
		 * case "firefox": File pathToBinary = new
		 * File("C:\\Program Files\\Mozilla Firefox\\firefox.exe"); FirefoxBinary
		 * ffBinary = new FirefoxBinary(pathToBinary); FirefoxProfile firefoxProfile =
		 * new FirefoxProfile(); driver = new FirefoxDriver(ffBinary,firefoxProfile);
		 * break;
		 */
		case "ie":
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.BROWSER_NAME, "ie");
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			capabilities.setCapability("requireWindowFocus", true);
			System.setProperty("webdriver.ie.driver", "./src/test/resources/drivers/IEDriverServer.exe");
			driver = new InternetExplorerDriver(capabilities);
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;

		default:
			driver = new FirefoxDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;
		}
		wait = new WebDriverWait(driver, 120);
	}

	/**
	 * To load properties from application.properties file
	 *
	 * @param propFilePath
	 * @return
	 */
	public Properties loadPropertiesFile(String propFilePath) {
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream fis = new FileInputStream(propFilePath);
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
		return properties;
	}

	public String[] readProperties(String locatorKey) {
		String[] locatorMethodName = null;
		try {
			locatorKey = locatorKey.replace(" ", "").replace(":", "");
			String objectValue = object.getProperty(locatorKey);
			locatorMethodName = objectValue.split("#");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Read Property File and Fetch Locator Key " + locatorKey,
					locatorKey, e.getMessage(), "Fail", strReportFilename);
		}
		return locatorMethodName;
	}

	public WebElement getWebElement(String locatorKey) {
		WebElement element = null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				element = driver.findElement(By.id(locatorValue));
				break;
			case "name":
				element = driver.findElement(By.name(locatorValue));
				break;
			case "class":
				element = driver.findElement(By.className(locatorValue));
				break;
			case "linkText":
				element = driver.findElement(By.linkText(locatorValue));
				break;
			case "partiallinkText":
				element = driver.findElement(By.partialLinkText(locatorValue));
				break;
			case "tagname":
				element = driver.findElement(By.tagName(locatorValue));
				break;
			case "css":
				element = driver.findElement(By.cssSelector(locatorValue));
				break;
			case "xpath":
				element = driver.findElement(By.xpath(locatorValue));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("GetWebElement: Element Not Present");
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
		return element;
	}

	public void waitForElementUsingPresence(String locatorKey) {
		WebElement element = null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
				break;
			case "name":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.name(locatorValue)));
				break;
			case "class":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.className(locatorValue)));
				break;
			case "linkText":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(locatorValue)));
				break;
			case "partiallinkText":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(locatorValue)));
				break;
			case "tagname":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(locatorValue)));
				break;
			case "css":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locatorValue)));
				break;
			case "xpath":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
	}

	public void waitForElementToBeClickable(String locatorKey) {
		WebElement element = null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				wait.until(ExpectedConditions.elementToBeClickable(By.id(locatorValue)));
				break;
			case "name":
				wait.until(ExpectedConditions.elementToBeClickable(By.name(locatorValue)));
				break;
			case "class":
				wait.until(ExpectedConditions.elementToBeClickable(By.className(locatorValue)));
				break;
			case "linkText":
				wait.until(ExpectedConditions.elementToBeClickable(By.linkText(locatorValue)));
				break;
			case "partiallinkText":
				wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(locatorValue)));
				break;
			case "tagname":
				wait.until(ExpectedConditions.elementToBeClickable(By.tagName(locatorValue)));
				break;
			case "css":
				wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(locatorValue)));
				break;
			case "xpath":
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
	}

	public void waitForTextToBePresentInElement(String locatorKey, String strExpectedText) {
		try {
			WebElement element = getElementFluentWait(locatorKey);

			wait.until(ExpectedConditions.textToBePresentInElement(element, strExpectedText));
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Wait for the Text to be Present in the element",
					strExpectedText, e.getMessage(), "Fail", strReportFilename);
		}
	}

	public WebElement getWebElementWithWait(String locatorKey) {

		WebElement element = null;
		String locatorMethod = null;
		String locatorValue = null;

		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locatorValue)));
				element = driver.findElement(By.id((locatorValue)));
				break;
			case "name":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(locatorValue)));
				element = driver.findElement(By.name((locatorValue)));
				break;
			case "class":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(locatorValue)));
				element = driver.findElement(By.className((locatorValue)));
				break;
			case "linkText":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(locatorValue)));
				element = driver.findElement(By.linkText((locatorValue)));
				break;
			case "partiallinkText":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(locatorValue)));
				element = driver.findElement(By.partialLinkText((locatorValue)));
				break;
			case "tagname":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(locatorValue)));
				element = driver.findElement(By.tagName((locatorValue)));
				break;
			case "css":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locatorValue)));
				element = driver.findElement(By.cssSelector((locatorValue)));
				break;
			case "xpath":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locatorValue)));
				element = driver.findElement(By.xpath((locatorValue)));
				break;

			default:
				break;
			}
		} catch (NoSuchWindowException e) {
			System.out.println("Window Already closed and elment is not visible further ...");
		} catch (Exception e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
			System.out.println(java.time.LocalDateTime.now() + "-:: " + e.getStackTrace());
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
		return element;
	}

	public List<WebElement> getWebElements(String locatorKey) {
		List<WebElement> element = null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				element = driver.findElements(By.id((locatorValue)));
				break;
			case "name":
				element = driver.findElements(By.name((locatorValue)));
				break;
			case "class":
				element = driver.findElements(By.className((locatorValue)));
				break;
			case "linkText":
				element = driver.findElements(By.linkText((locatorValue)));
				break;
			case "partiallinkText":
				element = driver.findElements(By.partialLinkText((locatorValue)));
				break;
			case "tagname":
				element = driver.findElements(By.tagName((locatorValue)));
				break;
			case "css":
				element = driver.findElements(By.cssSelector((locatorValue)));
				break;
			case "xpath":
				element = driver.findElements(By.xpath((locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
		return element;
	}

	public List<WebElement> getWebElements(String locatorMethod, String locatorValue) {
		List<WebElement> element = null;
		try {
			switch (locatorMethod) {
			case "id":
				element = driver.findElements(By.id((locatorValue)));
				break;
			case "name":
				element = driver.findElements(By.name((locatorValue)));
				break;
			case "class":
				element = driver.findElements(By.className((locatorValue)));
				break;
			case "linkText":
				element = driver.findElements(By.linkText((locatorValue)));
				break;
			case "partiallinkText":
				element = driver.findElements(By.partialLinkText((locatorValue)));
				break;
			case "tagname":
				element = driver.findElements(By.tagName((locatorValue)));
				break;
			case "css":
				element = driver.findElements(By.cssSelector((locatorValue)));
				break;
			case "xpath":
				element = driver.findElements(By.xpath((locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
		return element;
	}

	public void waitForElementUsingVisibility(String locatorKey) {
		WebElement element = null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(locatorKey);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + locatorKey,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}
		try {
			switch (locatorMethod) {
			case "id":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locatorValue)));
				break;
			case "name":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(locatorValue)));
				break;
			case "class":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(locatorValue)));
				break;
			case "linkText":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(locatorValue)));
				break;
			case "partiallinkText":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(locatorValue)));
				break;
			case "tagname":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(locatorValue)));
				break;
			case "css":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locatorValue)));
				break;
			case "xpath":
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
	}

	/**
	 *
	 * @param locatorMethod
	 * @param locatorValue
	 */
	public WebElement getWebElementWithWait(String locatorMethod, String locatorValue) {
		WebElement element = null;

		try {
			switch (locatorMethod) {
			case "id":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
				break;
			case "name":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.name(locatorValue)));
				break;
			case "class":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.className(locatorValue)));
				break;
			case "linkText":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(locatorValue)));
				break;
			case "partiallinkText":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(locatorValue)));
				break;
			case "tagname":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(locatorValue)));
				break;
			case "css":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locatorValue)));
				break;
			case "xpath":
				element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}

		return element;
	}

	/**
	 * Enter the text
	 */
	public void clearText(String locatorKey) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			waitForElementUsingPresence(locatorKey);
			element.clear();
			System.out.println(
					java.time.LocalDateTime.now() + "::Text for Element " + locatorKey + " Cleared Successfully ..!!");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Clear the text value in text field", "Clear the text value",
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Enter the text
	 */
	public void enterText(String locatorKey, String data) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			waitForElementUsingPresence(locatorKey);
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].style.backgroundColor = 'yellow'", element);
			element.sendKeys(data);
			js.executeScript("arguments[0].style.backgroundColor = 'white'", element);
			reporter.writeStepResult(tc_id, scenarioName, "Enter Value in text field", data,
					"Value " + data + " entered successfully", "Pass", strReportFilename);
			System.out.println(java.time.LocalDateTime.now() + ":: data  " + data
					+ " entered Successfully in text field " + locatorKey + ".");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Enter Value in text field", data,
					"Unable to enter value " + data, "Fail", strReportFilename);
		}
	}

	/**
	 * Enter the text and Enter Tab
	 */
	public void enterTextwithTab(String locatorKey, String data) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			waitForElementUsingPresence(locatorKey);
			element.sendKeys(data, Keys.TAB);
			reporter.writeStepResult(tc_id, scenarioName, "Enter Value in text field", data,
					"Value " + data + " entered successfully", "Pass", strReportFilename);
			System.out.println(java.time.LocalDateTime.now() + ":: data  " + data
					+ " entered Successfully in text field " + locatorKey + ".");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Enter Value in text field", data,
					"Unable to enter value " + data, "Fail", strReportFilename);
		}
	}

	/**
	 * Click an element
	 *
	 * @throws InterruptedException
	 */
	public void clickAnElement(String locatorKey, String strButtonLabel) {
		WebElement element = null;
		try {
			if (object.getProperty(locatorKey) != null && object.getProperty(locatorKey).contains("#")) {
				element = getElementFluentWait(locatorKey);
			} else {
				element = getElementFluentWait(locatorKey, strButtonLabel);
			}
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			System.out
					.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " clicked Successfully ..!!");
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
		} catch (Exception e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].click();", element);

			} catch (Exception e1) {
				reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
						"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);

			}

		}
	}

	public void clickAnElement(WebElement element, String strButtonLabel) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
			System.out.println(
					java.time.LocalDateTime.now() + ":: Element " + strButtonLabel + " clicked Successfully ..!!");
		} catch (Exception e) {
			try {
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("arguments[0].click();", element);
				reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
						"Clicked " + strButtonLabel + " button successfully Using JS", "Pass", strReportFilename);
			} catch (Exception e1) {
				reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
						"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);

			}

		}
	}

	/**
	 * Handle alert
	 */
	public void handleAlert(String action, String strExpectedText) {
		Alert alert = null;
		String strActualText = null;
		try {
			alert = FunctionsLibrary.driver.switchTo().alert();
			strActualText = alert.getText();
			System.out.println(strActualText);
			if (action.equalsIgnoreCase("accept")) {
				if (strActualText.equals(strExpectedText)) {
					System.out.println("Expected alert text is equal to actual text");
					alert.accept();
					reporter.writeStepResult(tc_id, scenarioName, "Verify alert pop up text",
							"Expected: " + strExpectedText, "the " + strExpectedText + " alert present", "Pass",
							strReportFilename);
				} else {
					alert.accept();
					reporter.writeStepResult(tc_id, scenarioName, "Verify alert pop up text",
							"Expected: " + strExpectedText,
							"Expected alert text is not present (Actual: " + strActualText + ")", "Fail",
							strReportFilename);
				}
			}
			if (action.equalsIgnoreCase("dismiss")) {
				if (strActualText.equals(strExpectedText)) {
					alert.dismiss();
					reporter.writeStepResult(tc_id, scenarioName, "Verify alert pop up text",
							"Expected: " + strExpectedText, "the " + strExpectedText + " alert present", "Pass",
							strReportFilename);
				} else {
					alert.dismiss();
					reporter.writeStepResult(tc_id, scenarioName, "Verify alert pop up text",
							"Expected: " + strExpectedText,
							"Expected alert text is not present (Actual: " + strActualText + ")", "Fail",
							strReportFilename);
				}
			}
		} catch (Exception e1) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify alert pop up text", "Expected: " + strExpectedText,
					"Expected alert text is not present (Actual: " + strActualText + ")", "Fail", strReportFilename);
			System.out.println("Exception occurred -- " + e1.getMessage());
			System.out.println();
		}
	}

	/**
	 * get the text
	 */
	public String getText(String locatorKey) {
		WebElement element = getElementFluentWait(locatorKey);
		try {
			element.getText();
			reporter.writeStepResult(tc_id, scenarioName, "Get the text from the web page",
					"User should be able to get text from web page",
					"The text got from webpage is " + element.getText(), "Pass", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Get the text from the web page",
					"User should be able to get text from web page",
					"Unable to get the text from the web page" + e.getMessage(), "Fail", strReportFilename);
		}
		return element.getText();
	}

	/**
	 * Click an element and Press Enter
	 */
	public void clickAndEnterAnElement(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			element.sendKeys(Keys.ENTER);
			System.out
					.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " clicked Successfully ..!!");
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);
		}
	}

	/**
	 * Click an element using JavascriptExector with Handling Alert
	 */
	public void clickAnElementAndHandleAlert(String condition, String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			System.out
					.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " clicked Successfully ..!!");
			switchTOAlert(condition);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);
		}
	}

	/**
	 * Click an element using JavascriptExector
	 */
	public void clickAnElementUsingJavaScript(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));

			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", element);

			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
			System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey
					+ " clicked Successfully Using JavaScript..!!");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);
		}
	}

	public void clickAnElementUsingJavaScript(WebElement element, String strButtonLabel) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element));

			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].click();", element);

			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
			System.out.println(java.time.LocalDateTime.now() + ":: Element " + strButtonLabel
					+ " clicked Successfully Using JavaScript..!!");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);
		}
	}

	/**
	 * Scroll to an Element using JavascriptExector
	 */
	public void scrollToAnElementUsingJavaScript(String locatorKey) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			waitForElementUsingPresence(locatorKey);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Scroll to an Element", locatorKey,
					"Not able to Scroll to an element" + e.getMessage(), "Fail", strReportFilename);
		}
	}

	public void scrollToAnElementUsingJavaScript(String locatorKey, String strExpectedText) {
		try {
			WebElement element = getWebElementWithWait(locatorKey);
			waitForTextToBePresentInElement(locatorKey, strExpectedText);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Scroll to an Element", locatorKey,
					"Not able to Scroll to an element" + e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Switch to the frame
	 *
	 * @throws InterruptedException
	 */
	public void switchToFrame(String locatorKey) {
		WebElement frame = getWebElement(locatorKey);
		try {
			waitForElementUsingPresence(locatorKey);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frame));
			// driver.switchTo().frame(frame);
			System.out.println(
					java.time.LocalDateTime.now() + ":: Switched to Frame " + locatorKey + " Successfully ..!!");
			// reporter.writeStepResult(tc_id, scenarioName, "Select Frame : " + locatorKey,
			// locatorKey,
			// "Frame " + locatorKey + " selected successfully", "Pass", strReportFilename);
		} catch (WebDriverException web1) {
			reporter.writeStepResult(tc_id, scenarioName, "Select Frame : " + locatorKey, "" + locatorKey,
					"Unable to select frame due to Webdriver exception", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Select Frame : " + locatorKey, "" + locatorKey,
					"Not able to select frame " + locatorKey, "Fail", strReportFilename);
		}
	}

	/**
	 * Switch to default frame
	 */
	public void deSelectFrame() {
		try {
			driver.switchTo().defaultContent();
			// reporter.writeStepResult(tc_id, scenarioName, "DeSelect Frame", "Frame should
			// be deselected",
			// "Frame deselected successfully", "Pass", strReportFilename);
			System.out.println(java.time.LocalDateTime.now() + ":: Frame Deselected Successfully ..!!");
		} catch (SeleniumException web1) {
			reporter.writeStepResult(tc_id, scenarioName, "Select Main page", "Frame should be deselected",
					web1.getMessage(), "Fail", strReportFilename);
		} catch (Exception e1) {
			reporter.writeStepResult(tc_id, scenarioName, "Select Main page", "Frame should be deselected",
					"Not able to select Main page", "Fail", strReportFilename);
		}
	}

	/**
	 * Verify element is present
	 *
	 * @throws InterruptedException
	 */
	public boolean verifyElementPresent(String locatorKey) {
		try {
			List<WebElement> elements = getWebElements(locatorKey);
			if(elements.size() > 0) {
				if (elements.get(0).isDisplayed()) {
					System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is Present ..!!");
					return true;
				} else {
					System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is not Present ..!!");
					return false;
				}
			}else
			{
				System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is not Present ..!!");
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Verify element is present
	 */
	public void verifyElementPresentWithReport(String locatorKey) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			boolean exists;
			if (element.isDisplayed()) {
				System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is Present ..!!");
				exists = true;
			} else {
				System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is not Present ..!!");
				exists = false;
			}

			if (exists)
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is present on the page",
						locatorKey, "The element " + locatorKey + " is present on the page", "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is present on the page",
						locatorKey, "The element " + locatorKey + " is not present on the page", "Fail",
						strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is present on the page",
					locatorKey, e.getMessage(), "Fail", strReportFilename);
		}
	}

	public void verifyElementPresentWithReport(WebElement element, String label) {
		try {
			// WebElement element = getElementFluentWait(locatorKey);
			boolean exists;
			if (element.isDisplayed()) {
				System.out
						.println(java.time.LocalDateTime.now() + ":: Element with Label " + label + " is Present ..!!");
				exists = true;
			} else {
				System.out.println(
						java.time.LocalDateTime.now() + ":: Element with Label " + label + " is not Present ..!!");
				exists = false;
			}

			if (exists)
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is present on the page", label,
						"The element " + label + " is present on the page", "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is present on the page", label,
						"The element " + label + " is not present on the page", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is present on the page", label,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify element text is present
	 */
	public void verifyValuePresentInTheTextBox(String Label, String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getAttribute("value").trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, e.getMessage(), "Fail", strReportFilename);
		}
		if (strActualText.equals(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, strActualText, "Fail", strReportFilename);
	}

	/**
	 * Verify element is not present
	 */
	public void verifyElementNotPresent(String locatorKey) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			boolean exists;
			if (element.isDisplayed()) {
				System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is Present ..!!");
				exists = true;
			} else {
				System.out.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " is not Present ..!!");
				exists = false;
			}

			if (!exists)
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is not present on the page",
						locatorKey, "The element " + locatorKey + " is not present on the page", "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is not present on the page",
						locatorKey, "The element " + locatorKey + " is present on the page", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + locatorKey + " is not present on the page",
					locatorKey, e.getMessage(), "Fail", strReportFilename);
		}
	}

	public void verifyElementNotPresent(WebElement element, String label) {
		try {
			// WebElement element = getElementFluentWait(locatorKey);
			boolean exists;
			if (element.isDisplayed()) {
				System.out
						.println(java.time.LocalDateTime.now() + ":: Element with label " + label + " is Present ..!!");
				exists = true;
			} else {
				System.out.println(
						java.time.LocalDateTime.now() + ":: Element with label " + label + " is not Present ..!!");
				exists = false;
			}

			if (!exists)
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is not present on the page", label,
						"The element " + label + " is not present on the page", "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is not present on the page", label,
						"The element " + label + " is present on the page", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + label + " is not present on the page", label,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify element text is present
	 */
	public void verifyElementTextPresent(String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			if (verifyElementPresent(locatorKey))
				strActualText = element.getText().trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present on the page", strExpectedText,
					e.getMessage(), "Fail", strReportFilename);
		}
		System.out.println("***" + strExpectedText + "***");
		System.out.println("***" + strActualText + "***");
		if (strActualText.equals(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element",
					"Expected: " + strExpectedText, "Actual: " + strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Fail", strReportFilename);
	}

	/**
	 * Verify element text is contains
	 */
	public void verifyElementTextContains(String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			if (verifyElementPresent(locatorKey))
				strActualText = element.getText().trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present on the page", strExpectedText,
					e.getMessage(), "Fail", strReportFilename);
		}
		System.out.println("***" + strExpectedText + "***");
		System.out.println("***" + strActualText + "***");
		if (strActualText.contains(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Fail", strReportFilename);
	}

	/**
	 * Verify element text is present using contains
	 */
	public void verifyElementTextPresentContains(String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			waitForElementUsingVisibility(locatorKey);
			element = getElementFluentWait(locatorKey);

			if (verifyElementPresent(locatorKey))
				wait.until(ExpectedConditions.textToBePresentInElement(element, strExpectedText));
			strActualText = element.getText().trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present on the page", "" + element,
					element + " is not present on the page", "Fail", strReportFilename);
		}

		if (strActualText.contains(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Fail", strReportFilename);
	}

	/**
	 * Verify element text is present for multiple lines
	 */
	public void verifyElementTextPresentMultipleLines(String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getText().trim().replace("\n", " ");
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present on the page", strExpectedText,
					e.getMessage(), "Fail", strReportFilename);
		}
		System.out.println("***" + strExpectedText);
		System.out.println("***" + strActualText);
		if (strActualText.equals(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Fail", strReportFilename);
	}

	/**
	 * Verify color of an element
	 */
	public void verifyColorOfAnElement(String Label, String ExpectedColor, String locatorKey) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			String ActualColor = element.getCssValue("color");
			String hex = Color.fromString(ActualColor).asHex();

			switch (hex) {
			case "#ff0000":
				ActualColor = "RED";
				break;
			case "#008000":
				ActualColor = "GREEN";
				break;
			case "#0000FF":
				ActualColor = "BLUE";
				break;

			default:
				break;
			}
			if (ExpectedColor.equalsIgnoreCase(ActualColor))
				reporter.writeStepResult(tc_id, scenarioName, "Verify the color of the element for " + Label,
						ExpectedColor, ActualColor, "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify the color of the element for " + Label,
						ExpectedColor, ActualColor, "Fail", strReportFilename);

		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify the color of the element for" + Label, ExpectedColor,
					e.getMessage(), "Pass", strReportFilename);
		}
	}

	/**
	 * Click RadioButton or CheckBox
	 */
	public void changeRadioButtonStatus(String locatorKey, String data) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			if (data.equalsIgnoreCase("deselect") && element.isSelected()) {
				element.click();
			}
			if (data.equalsIgnoreCase("select") && !element.isSelected()) {
				element.click();
			}
			reporter.writeStepResult(tc_id, scenarioName, "change radio button status",
					"Radio button should be : " + data + "ed", "Radio button is : " + data + "ed", "Pass",
					strReportFilename);
		} catch (WebDriverException w1) {
			reporter.writeStepResult(tc_id, scenarioName, "change radio button status", data,
					"Not able to perform expected action", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "change radio button status", data,
					"Not able to perform expected action", "Fail", strReportFilename);
		}
	}

	/**
	 * Click RadioButton or CheckBox
	 */
	public void clickRadioButtonOrCheckBox(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			if (!element.isSelected()) {
				element.click();
				reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
						"Clicked " + strButtonLabel + " successfully", "Pass", strReportFilename);
			} else
				reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
						"The " + strButtonLabel + " is already selected", "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel, e.getMessage(),
					"Fail", strReportFilename);
		}
	}

	/**
	 * Verify RadioButton or CheckBox is selected
	 */
	public void verifyRadioButtonOrCheckBoxIsSelected(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			if (element.isSelected()) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + strButtonLabel + " is Selected",
						strButtonLabel, "The " + strButtonLabel + " is selected on the page", "Pass",
						strReportFilename);
			} else
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + strButtonLabel + " is Selected",
						strButtonLabel, "Not able to select the " + strButtonLabel, "Fail", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + strButtonLabel + " is Selected", strButtonLabel,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Select drop down value
	 *
	 * @throws InterruptedException
	 */
	public void selectDropdown(String locatorKey, String Option, String data) {
		WebElement element = getElementFluentWait(locatorKey);
		Select sel = new Select(element);
		try {
			if (Option.equalsIgnoreCase("VisibleText")) {
				sel.selectByVisibleText(data);
			} else if (Option.equalsIgnoreCase("Value")) {
				sel.selectByValue(data);
			} else if (Option.equalsIgnoreCase("Index")) {
				int index = Integer.parseInt(data);
				sel.selectByIndex(index);
			}
			reporter.writeStepResult(tc_id, scenarioName, "Select value from " + locatorKey + " Listbox", data,
					"Expected value " + data + " is selected in the listbox", "Pass", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Select value from " + locatorKey + " Listbox", data,
					"Expected value " + data + " is not present in the listbox", "Fail", strReportFilename);
		}
	}

	/**
	 * To Clear the text field
	 */
	public void clearTextField(String locatorKey) {
		WebElement element = getElementFluentWait(locatorKey);
		element.clear();
	}

	/**
	 * fetch Selected Drop Down Value
	 */
	public String fetchSelectedDropDownValue(String locatorKey) {
		String defaultValue = null;
		try {
			WebElement element = getElementFluentWait(locatorKey);
			Select sel = new Select(element);
			defaultValue = sel.getFirstSelectedOption().getText();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"fetch default DropDown value from " + locatorKey + " Listbox", defaultValue, e.getMessage(),
					"Fail", strReportFilename);
		}
		return defaultValue;
	}

	/**
	 * Verify Selected Drop Down Value
	 */
	public void verifySelectedDropDownValue(String locatorKey, String ExpectedValue) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			Select sel = new Select(element);
			String ActualValue = sel.getFirstSelectedOption().getText();
			if (ExpectedValue.equals(ActualValue)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify DropDown value from " + locatorKey + " Listbox",
						ExpectedValue, ActualValue, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify DropDown value from " + locatorKey + " Listbox",
						ExpectedValue, ActualValue, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify DropDown value from " + locatorKey + " Listbox",
					ExpectedValue, e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * fetch Drop Down Values
	 */
	public List<String> fetchDropDownValues(String locatorKey) {
		WebElement element = getElementFluentWait(locatorKey);
		Select sel = new Select(element);
		List<String> ActualValues = new ArrayList<String>();
		try {
			List<WebElement> options = sel.getOptions();
			for (WebElement option : options) {
				ActualValues.add(option.getText());
			}
		} catch (Exception e) {
		}
		return ActualValues;
	}

	/**
	 * Verify Drop Down Values
	 */
	public void VerifyDropDownValues(String Label, List<String> ExpectedValues, List<String> ActualValues) {
		try {
			if (ExpectedValues.size() == ActualValues.size()) {
				for (int i = 0; i < ExpectedValues.size(); i++) {
					if (ExpectedValues.get(i).equals(ActualValues.get(i)))
						reporter.writeStepResult(tc_id, scenarioName,
								"Verify DropDown value from " + Label + " Listbox", ExpectedValues.get(i),
								ActualValues.get(i), "Pass", strReportFilename);
					else
						reporter.writeStepResult(tc_id, scenarioName,
								"Verify DropDown value from " + Label + " Listbox", ExpectedValues.get(i),
								ActualValues.get(i), "Fail", strReportFilename);
				}
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify DropDown value from " + Label + " Listbox",
					"" + ExpectedValues, e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Row Label for Column Label with Value
	 */
	public void verifyRowLabelforColumnLabelWithValue(String RowLabel, String ColumnLabel, String ExpectedValue,
			String ActualValue) {
		try {
			if (ExpectedValue.equals(ActualValue)) {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + RowLabel + " for " + ColumnLabel + " with Expected Value", ExpectedValue,
						ActualValue, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + RowLabel + " for " + ColumnLabel + " with Expected Value", ExpectedValue,
						ActualValue, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + RowLabel + " for " + ColumnLabel + " with Expected Value", ExpectedValue,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Table Header Values
	 */
	public void verifyTableHeaderValues(String TableName, String ExpectedValue, String ActualValue) {
		try {
			if (ExpectedValue.equals(ActualValue)) {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify Header " + ExpectedValue + " is present in the Table " + TableName, ExpectedValue,
						ActualValue, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify Header " + ExpectedValue + " is present in the Table " + TableName, ExpectedValue,
						ActualValue, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify Header " + ExpectedValue + " is present in the Table " + TableName, ExpectedValue,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Number of Rows
	 */
	public void verifyNumberOfRows(String locatorKey, int ExpectedNumberofRows) {
		try {
			List<WebElement> element = getWebElements(locatorKey);
			int ActualNumberOfRows = element.size();
			if (ExpectedNumberofRows == ActualNumberOfRows) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify Number of Rows Present in the Table",
						"" + ExpectedNumberofRows, "" + ActualNumberOfRows, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify Number of Rows Present in the Table",
						"" + ExpectedNumberofRows, "" + ActualNumberOfRows, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Number of Rows Present in the Table",
					"" + ExpectedNumberofRows, e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Number of Rows
	 */
	public void verifyNumberOfRowsGreaterThan(String locatorKey, int ExpectedNumberofRows) {
		try {
			List<WebElement> element = getWebElements(locatorKey);
			int ActualNumberOfRows = element.size();
			if (ActualNumberOfRows >= ExpectedNumberofRows) {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify Number of Rows greater than " + ExpectedNumberofRows + " in the Table",
						"The Number of Rows in the Table should be greater than " + ExpectedNumberofRows,
						"Total " + ActualNumberOfRows + " number of rows are present in the Table", "Pass",
						strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify Number of Rows greater than " + ExpectedNumberofRows + " in the Table",
						"The Number of Rows in the Table should be greater than " + ExpectedNumberofRows,
						"Total " + ActualNumberOfRows + " number of rows are present in the Table", "Fail",
						strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Number of Rows Present in the Table",
					"" + ExpectedNumberofRows, e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Switch to Alert
	 */
	public void switchTOAlert(String condition) {
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			if (condition.equalsIgnoreCase("Accept"))
				alert.accept();
			else
				alert.dismiss();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Handle the Alert Present on the page", condition,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * fetch column names from DB query
	 */
	public String[] fetchColumnNamesFromDBQuery(String query) {
		String[] ColumnNames = null;
		int selectIndex = query.toUpperCase().lastIndexOf("SELECT");
		int fromIndex = query.toUpperCase().lastIndexOf("FROM");
		String columnNameValue = query.toUpperCase().substring(selectIndex + 6, fromIndex - 1)
				.replaceAll("(\\r|\\n|\\r\\n|\\s+)+", "").trim();
		ColumnNames = columnNameValue.split(",");
		for (int j = 0; j < ColumnNames.length; j++) {
			if (ColumnNames[j].contains("SUBSTRING ")) {
				if (ColumnNames[j].contains("AS ")) {
					int rawColName = ColumnNames[j].indexOf("AS");
					String rawColNameVal = ColumnNames[j].substring(rawColName + 2);
				}
			}
			if (ColumnNames[j].contains(".")) {
				ColumnNames[j] = ColumnNames[j].replaceAll("^[a-zA-Z0-9]+[.]", "").trim();
			}
		}
		return ColumnNames;
	}

	/**
	 * fetch DataBase Values
	 */
	public List<Map<String, String>> fetchDatabaseValuesInMap(String query) {
		Properties config = null;
		try {
			config = new ExecutionerClass().setEnv();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		try {
			final String Driver = config.getProperty("DB_DriverClass");
			Class.forName(Driver);
		} catch (ClassNotFoundException e1) {
			System.out.println("Unable to load the driver class! " + e1);
			reporter.writeStepResult(tc_id, scenarioName, "Fetch the Values from Database", query, e1.getMessage(),
					"Fail", strReportFilename);
			e1.printStackTrace();
		}
		Connection dbConnection = null;
		try {
			final String DBUrl = config.getProperty("DB_Url");
			final String DBName = config.getProperty("DB_DBName");
			/*
			 * "databaseName=HCSdb;integratedSecurity=true;";
			 */
			String connectionUrl = DBUrl + DBName;

			dbConnection = DriverManager.getConnection(connectionUrl);
			System.out.println("Database Connection Successfully established");
		} catch (SQLException e) {
			System.out.println("Couldn’t get connection! " + e);
			reporter.writeStepResult(tc_id, scenarioName, "Fetch the Values from Database", query, e.getMessage(),
					"Fail", strReportFilename);
		}
		int selectIndex = query.toUpperCase().lastIndexOf("SELECT");
		int fromIndex = query.toUpperCase().lastIndexOf("FROM");
		String columnNames = query.toUpperCase().substring(selectIndex + 6, fromIndex - 1)
				.replaceAll("(\\r|\\n|\\r\\n|\\s+)+", "").trim();

		String[] rawColumnNames = null;
		rawColumnNames = columnNames.split(",");
		for (int j = 0; j < rawColumnNames.length; j++) {
			if (rawColumnNames[j].contains("SUBSTRING ")) {
				if (rawColumnNames[j].contains("AS ")) {
					int rawColName = rawColumnNames[j].indexOf("AS");
					String rawColNameVal = rawColumnNames[j].substring(rawColName + 2);
				}
			}
			if (rawColumnNames[j].contains(".")) {
				rawColumnNames[j] = rawColumnNames[j].replaceAll("^[a-zA-Z0-9]+[.]", "").trim();
			}
		}
		String strActualValue = null;
		List<Map<String, String>> dbResultSet = new ArrayList<>();
		if (dbConnection != null) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = dbConnection.createStatement();
				rs = stmt.executeQuery(query);

				while (rs.next()) {
					Map<String, String> tableData = new HashMap<String, String>();
					for (int p = 0; p < rawColumnNames.length; p++) {
						try {
							if (rs.getString(rawColumnNames[p]).equals(null))
								System.out.println("The DB Value for " + rawColumnNames[p] + " is null");
							else
								strActualValue = rs.getString(rawColumnNames[p]).trim();

						} catch (NullPointerException e) {
							strActualValue = "";
						}
						System.out.println(rawColumnNames[p] + "===> " + strActualValue);
						tableData.put(rawColumnNames[p], strActualValue);
					}
					dbResultSet.add(tableData);
				}
				return dbResultSet;
			} catch (Exception e) {
				reporter.writeStepResult(tc_id, scenarioName, "Fetch the Values from Database", query, e.getMessage(),
						"Fail", strReportFilename);
			} finally {
				try {
					rs.close();
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch the Values from Database", query, "" + dbConnection,
					"Fail", strReportFilename);
		}
		return dbResultSet;
	}

	/**
	 * fetch DataBase Values
	 */
	public void VerifyDatabaseValues(String Label, String Field, String ExpectedValue, String ActualValue) {
		try {
			if (ExpectedValue.equals(ActualValue)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + Field + " Database Value for " + Label,
						ExpectedValue, ActualValue, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify " + Field + " Database Value for" + Label,
						ExpectedValue, ActualValue, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Field + " Database Value for " + Label,
					ExpectedValue, e.getMessage(), "Fail", strReportFilename);
		}
	}

	public static String getEnvironmentVariable(String strVariableName) {
		return envVariableMap.get(strVariableName);
	}

	public static void setEnvironmentVariable(String strVariableName, String strValue) {
		envVariableMap.put(strVariableName, strValue);
	}

	/**
	 * Verify Checkbox using symbols
	 */
	public void VerifyCheckBoxUsingSymbols(String locatorKey, String ExpectedValue) {
		try {
			WebElement element = getWebElement(locatorKey);
			String symbol = element.getText();
			char[] a = symbol.toCharArray();
			String actualHex = "";
			for (int i = 0; i < a.length; i++) {
				String hexSymbol = Integer.toHexString((int) a[i]);
				actualHex = actualHex + hexSymbol;
			}
			String ActualValue = null;

			switch (actualHex) {
			case "2610":
				ActualValue = "UnChecked";
				break;
			case "2611":
				ActualValue = "Checked";
				break;
			case "2612":
				ActualValue = "Crossed";
				break;
			default:
				break;
			}

			if (ExpectedValue.equals(ActualValue)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify CheckBox Using Symbols", ExpectedValue,
						ActualValue, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify CheckBox Using Symbols", ExpectedValue,
						ActualValue, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify CheckBox Using Symbols", ExpectedValue,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Page Title
	 */
	public void verifyPageTitle(String ClosedPage, String ExpectedTitle) {
		try {
			String ActualTitle = driver.getTitle();
			if (driver.getWindowHandles().size() == 1) {
				if (ExpectedTitle.equals(ActualTitle)) {
					reporter.writeStepResult(tc_id, scenarioName,
							"Verify page Title after " + ClosedPage + " is closed", ExpectedTitle, ActualTitle, "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult(tc_id, scenarioName,
							"Verify page Title after " + ClosedPage + " is closed", ExpectedTitle, ActualTitle, "Fail",
							strReportFilename);
				}
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify page Title after " + ClosedPage + " is closed",
						ExpectedTitle, "Not able to close the " + ClosedPage, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify page Title after " + ClosedPage + " is closed",
					ExpectedTitle, e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Verify Page Url
	 */
	public void verifyPageURL(String PageName, String ExpectedURL) {
		try {
			String ActualURL = driver.getCurrentUrl();
			if (ExpectedURL.equals(ActualURL)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify the URL for the page " + PageName, ExpectedURL,
						ActualURL, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify the URL for the page " + PageName, ExpectedURL,
						ActualURL, "Fail", strReportFilename);
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify the URL for the page " + PageName, ExpectedURL,
					e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Close the Window
	 */
	public void closeTheWindow() {
		try {
			driver.close();
			reporter.writeStepResult(tc_id, scenarioName, "Close the Curent Window",
					"The Current Window should be closed", "The Current Window is closed", "Pass", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Close the Curent Window",
					"The Current Window should be closed", e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Switch to New window
	 */
	public void switchToNewWindow() {
		try {
			Thread.sleep(1000);
			Set<String> allHandles = driver.getWindowHandles();
			String parentWindowHandle = allHandles.iterator().next();
			System.out.println("***********" + allHandles.size());
			for (String currHandle : allHandles) {
				if (currHandle != parentWindowHandle) {
					driver.switchTo().window(currHandle);
				}
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Switch To New Window",
					"The driver should be switched to New Window", e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Switch to parent window
	 */
	public void switchToParentWindow() {
		try {
			Set<String> allHandles = driver.getWindowHandles();
			String parentWindowHandle = allHandles.iterator().next();

			for (String currHandle : allHandles) {
				if (currHandle == parentWindowHandle) {
					driver.switchTo().window(currHandle);
				}
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Switch To Parent Window",
					"The driver should be switched to Parent Window", e.getMessage(), "Fail", strReportFilename);
		}
	}

	public void switchBetweenWindows() {
		Set<String> handles = driver.getWindowHandles();
		String firstWinHandle = driver.getWindowHandle();

		handles.remove(firstWinHandle);
		String winHandle = handles.iterator().next();
		if (winHandle != firstWinHandle) {
			String secondWinHandle = winHandle;

			driver.switchTo().window(secondWinHandle);
		}
	}

	public void switchToTheParentWindow() {
		Set<String> handles = driver.getWindowHandles();

		String firstWinHandle = driver.getWindowHandle();
		handles.remove(firstWinHandle);
		driver.close();
		String winHandle = handles.iterator().next();

		if (winHandle != firstWinHandle) {
			String secondWinHandle = winHandle;

			driver.switchTo().window(secondWinHandle);
		}
	}

	/**
	 * To Save PDF
	 */

	public void savePDF() {
		Robot robot;
		try {
			robot = new Robot();
			globalWait(15);

			robot.mouseMove(936, 208);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			// Shortcut to save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			// robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_S);
			// robot.keyRelease(KeyEvent.VK_SHIFT);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			String ProjectPath = new File("").getAbsolutePath();
			String PDFFolder = ProjectPath + "\\Files\\PDF\\";
			int x = (int) (Math.random() * 999999) + 1126;
			String PDFName = "hpstExhibitE" + x + ".pdf";
			String PDFPath = PDFFolder + PDFName;
			setEnvironmentVariable("PDFurl", PDFPath);

			// Store the path of PDF to clipboard
			StringSelection selection = new StringSelection(PDFPath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			globalWait(3);
			// Paste the path of PDF from clipboard and save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			globalWait(1);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			globalWait(1);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			driver.switchTo().defaultContent();

			driver.close();
			globalWait(3);
			SwitchUsingTitle("DisplayDoc.aspx");
			driver.close();

			SwitchUsingTitle("");

			driver.switchTo().defaultContent();

		} catch (AWTException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		} catch (Exception e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
	}

	/**
	 * Press tab
	 */
	public void pressTab() {
		try {
			Robot rb = new Robot();
			rb.keyPress(KeyEvent.VK_SHIFT);
			rb.keyPress(KeyEvent.VK_TAB);

			rb.keyRelease(KeyEvent.VK_TAB);
			rb.keyRelease(KeyEvent.VK_SHIFT);
		} catch (AWTException e) {
			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
	}

	/**
	 * Select drop down by visible text
	 */
	public void selectDropdownByVisibleText(String data, String LocatorKey) {
		WebElement element = null;
		try {
			element = getElementFluentWait(LocatorKey);
			if (this.driver == null)
				return;
			Select sel = new Select(element);
			sel.selectByVisibleText(data);
		} catch (Exception e) {

			try {
				System.out.println(java.time.LocalDateTime.now() + "-:: " + " Unable to enter the dropdown value '"
						+ data + "' Selected for field '" + LocatorKey + " Re-Entering Again");
				globalWait();
				wait.until(ExpectedConditions.elementToBeClickable(element));
				System.out.println("text to be present in element worked !!");

				Select sel = new Select(element);
				sel.selectByVisibleText(data);
			} catch (Exception e1) {
				System.out.println(java.time.LocalDateTime.now() + "-:: "
						+ "====================== :: Handled Full Stack Trace Information :: ==========================");
				System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(e1));
				System.out.println(java.time.LocalDateTime.now() + "-:: "
						+ "====================== :: ============================================ :: ==========================");

				System.out.println(java.time.LocalDate.now() + ":: unable to selcted dropdown value '" + data
						+ "'  for field '" + LocatorKey + "'");

				reporter.writeStepResult(tc_id, scenarioName,
						"Select the " + LocatorKey + "Menu with Visible Text Value " + data + " in the webpage",
						"The Locator and data Option not available in the page", e.getMessage(), "Fail",
						strReportFilename);
			}
		}
	}

	/**
	 * Select drop down by index
	 */
	public void selectDropdownByIndex(WebElement element, int index) {
		try {
			Select sel = new Select(element);
			sel.selectByIndex(index);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Select the " + element + "Menu with Inde Value " + index + " in the webpage",
					"The Locator and index not available in the page", e.getMessage(), "Fail", strReportFilename);
		}
	}

	/**
	 * Select drop down by value
	 */
	public void selectDropdownByValue(WebElement element, String data) {
		try {
			Select sel = new Select(element);
			sel.selectByValue(data);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Select the " + element + "Menu with Dropdown Value " + data + " in the webpage",
					"The Locator and DropDown Option not available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
	}

	/**
	 * Verify drop down
	 */
	public boolean verifyDropdownvalues(WebElement element, String expectedvalue) {
		boolean exists = false;
		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		for (WebElement we : options) {
			if (we.getText().equals(expectedvalue)) {
				we.click();
				exists = true;
			}
		}
		if (exists) {
			System.out.println("Pass");
			return true;
		} else {
			System.out.println("Fail");
			return false;
		}
	}

	/**
	 * Verify Element in sales channel summary report in Reporting page
	 */
	public boolean verifyAlertPresent() {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		if (wait.until(ExpectedConditions.alertIsPresent()) == null)
			return false;
		else {
			Alert alert = driver.switchTo().alert();
			String text = alert.getText();
			return true;
		}
	}

	/**
	 * To wait for a page to load until it gets in ready state
	 */
	public void waitForPageLoad() {
		new WebDriverWait(driver, 100).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver input) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		});
	}

	/**
	 * To wait for specific element on the page for defined period[explicit wait]
	 *
	 * @param element
	 * @param seconds
	 * @return
	 */
	public boolean waitForElement(WebElement element, int seconds) {

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(seconds, TimeUnit.SECONDS)
				.pollingEvery((seconds / 5), TimeUnit.SECONDS);
		return wait.until(ExpectedConditions.visibilityOf(element)) != null;
	}

	// WebTops windows handles
	public void switchToWindows(String windowTitle) throws InterruptedException {
		Thread.sleep(7000);
		int currentWindowSize = handlesWithId.size() + 1;
		int iteration = 0;
		while (!handlesWithId.containsKey(windowTitle) && iteration < 5) {
			if (currentWindowSize == driver.getWindowHandles().size())
				break;
			else
				globalWait();
			iteration++;
		}

		try {
			driver.switchTo().window(getWindowHandleMap(windowTitle).get(windowTitle));

			System.out.println(driver.getTitle() + "==>" + driver.getCurrentUrl());
		} catch (Exception e) {
			System.out.println("Window not siwtched to : " + windowTitle);
			// CommonStepDefinitions.executeScenario="No";
			reporter.writeStepResult(tc_id, scenarioName, "Switch to window " + windowTitle,
					"Unable to switch to window " + windowTitle, e.getMessage(), "Fail", strReportFilename);

			e.printStackTrace();
		}

	}

	// Switch To the Generate and Print document window
	public void switchToWindowRec() {
		String winhandleBefore = driver.getWindowHandle();
		String HomePageUrl = driver.getCurrentUrl();
		setEnvironmentVariable("Windows", winhandleBefore);
		Set<String> winHandles = driver.getWindowHandles();
		System.out.println(winHandles.size());
		if (winHandles.size() > 1) {
			for (String loopWindow : winHandles) {
				driver.switchTo().window(loopWindow);
				String winTitle = driver.switchTo().window(loopWindow).getTitle();
				if (winTitle.contains("DisplayDoc.aspx")) {
					System.out.println(driver.getCurrentUrl());
					break;
				}
			}
		}
	}

	public Map<String, String> getWindowHandleMap(String windowTitle) {

		Set<String> handles = driver.getWindowHandles();
		/*
		 * System.out.println(handles); System.out.println(handles.size());
		 */
		List<String> handlesList = new ArrayList<String>(handles);
		for (int i = 0; i < handles.size(); i++) {

			if (!handlesWithId.containsValue(handlesList.get(i)))
				handlesWithId.put(windowTitle, handlesList.get(i));
		}
		System.out.println("All Windows" + handlesWithId);
		return handlesWithId;
	}

	public void elementWaituntilinvisible(String strButtonLabel) {

		try {

			WebElement element = getWebElementWithWait(strButtonLabel);
			boolean result = false;
			do {
				try {
					globalWait();
					result = element.isDisplayed();
				} catch (StaleElementReferenceException e) {
					System.out.println("Please Wait Dialog disappeared");
				}

			} while (result);

			return;

		} catch (Exception e) {
			System.out.println("Element is not present in DOM!!");
			reporter.writeStepResult(tc_id, scenarioName,
					"Wait for Element " + strButtonLabel + " to be visible on the webpage",
					"WebElement " + strButtonLabel + " is not visible on Webpage", e.getMessage(), "Fail",
					strReportFilename);
		}

	}

	public WebElement getElementFluentWait(String elementAddress) {
		if (this.driver == null)
			return null;
		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(elementAddress);
			locatorMethod = locatorMethodName[0].toLowerCase();
			locatorValue = locatorMethodName[1].trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + elementAddress,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}

		final String eleAddress = locatorValue;
		WebDriver driver = this.driver;
		final String locator = locatorMethod;
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		WebElement returnEle = null;
		wait.pollingEvery(5, TimeUnit.SECONDS);
		wait.withTimeout(getWaitTime, TimeUnit.SECONDS);
		WebElement element = null;
		int elementCheck = 1;
		while (element == null && elementCheck <= elementCheckCount) {
			try {
				element = wait.until(new Function<WebDriver, WebElement>() {

					public WebElement apply(WebDriver driver) {

						WebElement ele = null;

						switch (locator) {
						case "id":
							ele = driver.findElement(By.id(eleAddress));
							break;
						case "name":
							ele = driver.findElement(By.name(eleAddress));
							break;
						case "class":
							ele = driver.findElement(By.className(eleAddress));
							break;
						case "linkText":
							ele = driver.findElement(By.linkText(eleAddress));
							break;
						case "partiallinkText":
							ele = driver.findElement(By.partialLinkText(eleAddress));
							break;
						case "tagname":
							ele = driver.findElement(By.tagName(eleAddress));
							break;
						case "css":
							ele = driver.findElement(By.cssSelector(eleAddress));
							break;
						case "xpath":
							ele = driver.findElement(By.xpath(eleAddress));
							break;

						default:
							break;
						}
						CommonStepDefinitions.exceptioncounter = 0;
						highlightElement(ele);

						if (ele != null) {

							// System.out.println("Element located in DOM successfully.. with FluentWait");

							return ele;

						} else {
							System.out.println("Element not located in DOM for " + eleAddress
									+ " with FluentWait, Returning null value");

							return null;
						}
					}

				});
				// System.out.println("returning " + element + " for " + eleAddress);
			} catch (UnreachableBrowserException br) {
				reporter.writeStepResult(tc_id, scenarioName, "Browser is Unreachable", "--", br.getMessage(), "Fail",
						strReportFilename);
				driver = null;
				CommonStepDefinitions.executeScenario = false;
			} catch (TimeoutException te) {
				// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element not found "
				// + element + " for " +
				// eleAddress);
				// te.getStackTrace();
				ExceptionUtils.getFullStackTrace(te);
				if (elementCheck == elementCheckCount) {
					// tExceptionUtils.getFullStackTrace(e);;
					CommonStepDefinitions.exceptioncounter++;
					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page", "Element By "
									+ locatorMethod + " with Value " + eleAddress + " is Not Available in the webpage",
							"Fail", strReportFilename);
				}
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(te));
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page",
							"The Element is not available and Execution is Not Completed for this TestCase",
							"Not Completed", strReportFilename);
					/*
					 * reporter.writeStepResult(tc_id, scenarioName,
					 * "Re-login the user due to the failure",
					 * "The Locator Method and Value should be available in the page",
					 * "Close the driver instance and re-login the application.", "Not Completed",
					 * strReportFilename);
					 */
					CommonStepDefinitions.executeScenario = false;
					// this.driver.quit();
					// this.driver=null;
					System.out.println(
							java.time.LocalDateTime.now() + "-:: " + "Driver instance closed successfully ...!!!");
				}

			} catch (Exception e) {
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: ============================================ :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(e));
					e.printStackTrace();
					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page",
							"Page is not fully loaded hence unable to fetch the elmement details ", "Fail",
							strReportFilename);
				}
				globalWait();
			}

			elementCheck++;
		}
		return element;
	}

	public WebElement getElementFluentWait(String locatorMethod, String elementAddress) {
		if (this.driver == null)
			return null;
		final String eleAddress = elementAddress;
		WebDriver driver = this.driver;
		final String locator = locatorMethod;
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		WebElement returnEle = null;
		wait.pollingEvery(5, TimeUnit.SECONDS);
		wait.withTimeout(getWaitTime, TimeUnit.SECONDS);
		WebElement element = null;
		int elementCheck = 1;

		while (element == null && elementCheck <= elementCheckCount) {
			try {

				element = wait.until(new Function<WebDriver, WebElement>() {

					public WebElement apply(WebDriver driver) {

						WebElement ele = null;

						switch (locator) {
						case "id":
							ele = driver.findElement(By.id(eleAddress));

							break;
						case "name":
							ele = driver.findElement(By.name(eleAddress));
							break;
						case "class":
							ele = driver.findElement(By.className(eleAddress));
							break;
						case "linkText":
							ele = driver.findElement(By.linkText(eleAddress));
							break;
						case "partiallinkText":
							ele = driver.findElement(By.partialLinkText(eleAddress));
							break;
						case "tagname":
							ele = driver.findElement(By.tagName(eleAddress));
							break;
						case "css":
							ele = driver.findElement(By.cssSelector(eleAddress));
							break;
						case "xpath":
							ele = driver.findElement(By.xpath(eleAddress));
							break;

						default:
							break;
						}
						CommonStepDefinitions.exceptioncounter = 0;
						highlightElement(ele);

						if (ele != null) {

							// System.out.println("Element located in DOM successfully.. with FluentWait");

							return ele;

						} else {
							System.out.println("Element not located in DOM for " + eleAddress
									+ " with FluentWait, Returning null value");

							return null;

						}

					}

				});
				// System.out.println("returning " + element + " for " + eleAddress);
			} catch (UnreachableBrowserException br) {
				reporter.writeStepResult(tc_id, scenarioName, "Browser is Unreachable", "--", br.getMessage(), "Fail",
						strReportFilename);
				driver = null;
				CommonStepDefinitions.executeScenario = false;
			} catch (TimeoutException te) {
				// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element not found "
				// + element + " for " +
				// eleAddress);
				// te.getStackTrace();
				ExceptionUtils.getFullStackTrace(te);
				if (elementCheck == elementCheckCount) {
					// tExceptionUtils.getFullStackTrace(e);;
					CommonStepDefinitions.exceptioncounter++;
					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page", "Element By "
									+ locatorMethod + " with Value " + eleAddress + " is Not Available in the webpage",
							"Fail", strReportFilename);
				}
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(te));
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page",
							"The Element is not available and Execution is Not Completed for this TestCase",
							"Not Completed", strReportFilename);
					/*
					 * reporter.writeStepResult(tc_id, scenarioName,
					 * "Re-login the user due to the failure",
					 * "The Locator Method and Value should be available in the page",
					 * "Close the driver instance and re-login the application.", "Not Completed",
					 * strReportFilename);
					 */
					CommonStepDefinitions.executeScenario = false;
					// this.driver.quit();
					// this.driver=null;
					System.out.println(
							java.time.LocalDateTime.now() + "-:: " + "Driver instance closed successfully ...!!!");
				}

			} catch (Exception e) {
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: ============================================ :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(e));
					e.printStackTrace();
					reporter.writeStepResult(tc_id, scenarioName,
							"Find Element By " + locatorMethod + " with Value " + eleAddress + " in the webpage",
							"The Locator Method and Value should be available in the page",
							"Page is not fully loaded hence unable to fetch the elmement details ", "Fail",
							strReportFilename);
				}
				globalWait();
			}

			elementCheck++;
		}
		return element;
	}

	/**
	 * Highlight to an Element using JavascriptExector
	 */
	public static void highlightElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].style.border='2px solid blue'", element);
	}

	public int getFluentWaitTime() {
		String selectedEnvironment = loadPropertiesFile(sysPropFromFile).getProperty("Execution_Environment");
		String fluentWaitTime = "120";
		switch (selectedEnvironment) {
		case "SandBox":
			fluentWaitTime = loadPropertiesFile(sysPropFromFile).getProperty("SandBoxEnvWaitTime");
			break;
		case "QA":
			fluentWaitTime = loadPropertiesFile(sysPropFromFile).getProperty("QAEnvWaitTime");
			break;
		case "Dev":
			fluentWaitTime = loadPropertiesFile(sysPropFromFile).getProperty("DevEnvWaitTime");
			break;
		case "Production":
			fluentWaitTime = loadPropertiesFile(sysPropFromFile).getProperty("ProductionEnvWaitTime");
			break;
		default:
			break;
		}
		System.out.println("Selected Env is : " + selectedEnvironment + " and wait time is: " + fluentWaitTime);
		// String fluentWaitTime =
		// loadPropertiesFile(sysPropFromFile).getProperty("FluentWaitTime");
		// System.out.println("Global Fluent Wait.......................");
		return Integer.parseInt(fluentWaitTime);
	}

	public void globalWait(String... waitTime) {

		String globalWaitTime = "1";
		if (waitTime.length > 0) {
			globalWaitTime = waitTime[0];
		}
		globalWaitTime = loadPropertiesFile(sysPropFromFile).getProperty("GlobalWaitTime");
		int inputTime = Integer.parseInt(globalWaitTime) * 1000;
		try {
			Thread.sleep(inputTime);
			// System.out.println("Global Wait.......................");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// return inputTime;

	}

	public void refreshPage() {
		driver.navigate().refresh();
		waitForPageLoad();
	}

	/**
	 * Verify Unchecked Checkbox
	 */
	public void VerifyUncheckedCheckBox(String locatorKey) {
		try {
			WebElement element = getWebElement(locatorKey);
			wait.until(ExpectedConditions.visibilityOf(element));
			if (!element.isSelected()) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify CheckBox is Checked or UnChecked", locatorKey,
						"UnChecked", "Pass", strReportFilename);
				System.out.println(locatorKey + " Checkbox is Unchecked");
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify CheckBox is Checked or UnChecked", locatorKey,
						"Checked", "Fail", strReportFilename);
				System.out.println(locatorKey + " Checkbox is Checked");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void closeAllChildWindow(String homeWindow) {

		Set<String> allWindows = driver.getWindowHandles();

		// Use Iterator to iterate over windows
		Iterator<String> windowIterator = allWindows.iterator();

		// Verify next window is available
		while (windowIterator.hasNext()) {
			// Store the Recruiter window id
			String childWindow = windowIterator.next();

			// Here we will compare if parent window is not equal to child
			// window
			if (!homeWindow.equals(childWindow)) {
				driver.switchTo().window(childWindow);
				driver.close();
			}

		}
		driver.switchTo().window(homeWindow);
	}

	public void enterFromDate(String locatorKey) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
		Date currentDate = new Date();

		System.out.println(dateFormat.format(currentDate));

		// convert date to calendar
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		// manipulate date

		c.add(Calendar.DATE, -1); // same with c.add(Calendar.DAY_OF_MONTH, 1);

		// convert calendar to date
		Date currentDateMinus = c.getTime();

		System.out.println(dateFormat.format(currentDateMinus));

		String monthBackDate = dateFormat.format(currentDateMinus);
		System.out.println(monthBackDate);
		String[] splitFullDate = monthBackDate.split(" ");
		String[] splitDate = splitFullDate[0].split("-");
		String[] splitTime = splitFullDate[1].split(":");
		for (int j = 0; j < splitDate.length; j++) {
			System.out.println("splitDate =" + splitDate[j]);

		}
		for (int i = 0; i < splitTime.length; i++) {
			System.out.println("splitTime =" + splitTime[i]);
		}

		String mon = splitDate[1];
		String yy = splitDate[0];
		String dd = splitDate[2];
		String hour = splitTime[0];
		String minute = splitTime[1];
		String sec = splitTime[2];
		if (dd.substring(0, 1).contains("0"))
			dd = dd.replace("0", "");
		if (minute.substring(0, 1).contains("0"))
			minute = minute.replace("0", "");
		if (sec.substring(0, 1).contains("0"))
			sec = sec.replace("0", "");
		String[] locatorMethodName = readProperties(locatorKey);
		String locatorMethod = locatorMethodName[0].toLowerCase();
		String locatorValue = null;
		if (locatorKey.equals("selectMon")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", mon);
			clickAnElement("selectMon", locatorValue, "Month");
		}
		if (locatorKey.equals("selectYear")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", yy);
			clickAnElement("selectYear", locatorValue, "Year");
		}
		if (locatorKey.equals("selectDay")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", dd);
			clickAnElement("selectDay", locatorValue, "Day");
		}
		if (locatorKey.equals("selectHour")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", hour);
			clickAnElement("selectHour", locatorValue, "Hour");
		}
		if (locatorKey.equals("selectMin")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", minute);
			clickAnElement("selectMin", locatorValue, "Min");
		}
		if (locatorKey.equals("selectSec")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", sec);
			clickAnElement("selectSec", locatorValue, "Sec");
		}

	}

	public void enterFromDateAndTodateInternalTracing(String locatorKey, String date) throws java.text.ParseException {

		Date date123 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
		String returndate = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss").format(date123);

		String monthBackDate = returndate;
		String[] splitFullDate = monthBackDate.split(" ");
		String[] splitDate = splitFullDate[0].split("-");
		String[] splitTime = splitFullDate[1].split(":");
		for (int j = 0; j < splitDate.length; j++) {
			System.out.println("splitDate =" + splitDate[j]);

		}
		for (int i = 0; i < splitTime.length; i++) {
			System.out.println("splitTime =" + splitTime[i]);
		}

		String mon = splitDate[1];
		String yy = splitDate[0];
		String dd = splitDate[2];
		String hour = splitTime[0];
		String minute = splitTime[1];
		String sec = splitTime[2];
		if (dd.substring(0, 1).contains("0"))
			dd = dd.replace("0", "");
		if (minute.substring(0, 1).contains("0"))
			minute = minute.replace("0", "");
		if (sec.substring(0, 1).contains("0"))
			sec = sec.replace("0", "");
		String[] locatorMethodName = readProperties(locatorKey);
		String locatorMethod = locatorMethodName[0].toLowerCase();
		String locatorValue = null;
		if (locatorKey.equals("selectMon")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", mon);
			clickAnElement("selectMon", locatorValue, "Month");
		}
		if (locatorKey.equals("selectYear")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", yy);
			clickAnElement("selectYear", locatorValue, "Year");
		}
		if (locatorKey.equals("selectDay")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", dd);
			clickAnElement("selectDay", locatorValue, "Day");
		}
		if (locatorKey.equals("selectHour")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", hour);
			clickAnElement("selectHour", locatorValue, "Hour");
		}
		if (locatorKey.equals("selectMin")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", minute);
			clickAnElement("selectMin", locatorValue, "Min");
		}
		if (locatorKey.equals("selectSec")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", sec);
			clickAnElement("selectSec", locatorValue, "Sec");
		}

	}

	public void clickAnElement(String locatorKey, String locatorValue, String strButtonLabel) {
		try {
			WebElement element = driver.findElement(By.xpath(locatorValue));
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Clicked " + strButtonLabel + " button successfully", "Pass", strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Click on " + strButtonLabel, strButtonLabel,
					"Not able to click on  button " + strButtonLabel, "Fail", strReportFilename);
		}
	}

	public void clickAnElementWithOutReport(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getWebElement(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			System.out
					.println(java.time.LocalDateTime.now() + ":: Element " + locatorKey + " clicked Successfully ..!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearTextWithOutReport(String locatorKey) {
		try {
			WebElement element = getWebElement(locatorKey);
			waitForElementUsingPresence(locatorKey);
			element.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enterTextWithOutReport(String locatorKey, String data) {
		try {
			WebElement element = getWebElement(locatorKey);
			waitForElementUsingPresence(locatorKey);
			element.sendKeys(data);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void verifyElementPresent(String label, String locatorKey) {
		try {
			WebElement element = getWebElement(locatorKey);
			boolean exists;
			if (element.isDisplayed())
				exists = true;
			else
				exists = false;

			if (exists)
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorKey + " " + label + " is present on the page", locatorKey,
						"The element " + locatorKey + " " + label + " is present on the page", "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorKey + " " + label + " is present on the page", locatorKey,
						"The element " + locatorKey + " " + label + " is not present on the page", "Fail",
						strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + locatorKey + " " + label + " is present on the page", locatorKey, e.getMessage(),
					"Fail", strReportFilename);
		}
	}

	public boolean verifyElementNotPresent(String label, String locatorKey) {
		List<WebElement> element = null;
		boolean exists = false;
		try {
			element = getWebElements(locatorKey);
			if (!(element.size() == 0))
				exists = true;
			else
				exists = false;

			if (!exists)
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorKey + " " + label + " is not present on the page", locatorKey,
						"The element " + locatorKey + " " + label + " is not present on the page", "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorKey + " " + label + " is not present on the page", locatorKey,
						"The element " + locatorKey + " " + label + " is present on the page", "Fail",
						strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + locatorKey + " " + label + " is not present on the page", locatorKey, e.getMessage(),
					"Fail", strReportFilename);
		}
		return !exists;
	}

	public boolean verifyElementNotPresentBySize(String label, String locatorMethod, String locatorValue) {
		List<WebElement> element = null;
		boolean exists = false;
		try {
			element = getWebElements(locatorMethod, locatorValue);
			if (!(element.size() == 0))
				exists = true;
			else
				exists = false;

			if (!exists)
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorValue + " " + label + " is not present on the page", locatorValue,
						"The element " + locatorValue + " " + label + " is not present on the page", "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName,
						"Verify " + locatorValue + " " + label + " is not present on the page", locatorValue,
						"The element " + locatorValue + " " + label + " is present on the page", "Fail",
						strReportFilename);
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + locatorValue + " " + label + " is not present on the page", locatorValue,
					e.getMessage(), "Fail", strReportFilename);
		}
		return !exists;
	}

	public void maskingAndUnMasking(String rowData) {

		String username = getElementText("Footer_username");

		if (username.equalsIgnoreCase("qatest10")) {
			// List<List<String>> data= arg1.raw();

			if (rowData.startsWith("I1")) {
				String strfileVal1 = rowData.substring(4, 23);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(5, strfileVal1.length() - 4);

				if (!(strfileVal1.contains("*"))) {
					reporter.writeStepResult("I1", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1, "Pan " + strfileVal1 + " is not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("I1", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1, "Pan " + strfileVal1 + " is masked", "Fail",
							strReportFilename);
				}

			}
			if (rowData.startsWith("30")) {
				String strfileVal1 = rowData.substring(3, 22);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String strfileVal2 = rowData.substring(22, 26);
				strfileVal2 = strfileVal2.replaceAll("\\s+", "");

				if (!(strfileVal1.contains("*") && strfileVal2.contains("*"))) {
					reporter.writeStepResult("30", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("30", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Fail",
							strReportFilename);
				}

			}
			if (rowData.startsWith("TAB")) {
				String strfileVal1 = rowData.substring(48, 67);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String strfileVal2 = rowData.substring(67, 71);
				strfileVal2 = strfileVal2.replaceAll("\\s+", "");
				if (!(strfileVal1.contains("*") && strfileVal2.contains("*"))) {
					reporter.writeStepResult("TAB", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("TAB", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Fail",
							strReportFilename);
				}

			}
			if (rowData.startsWith("05")) {
				String strfileVal1 = rowData.substring(2, 21);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				if (!(strfileVal1.contains("*"))) {
					reporter.writeStepResult("05", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1, "Pan " + strfileVal1 + " is not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("05", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1, "Pan " + strfileVal1 + " is masked", "Fail",
							strReportFilename);
				}

			}

			if (rowData.startsWith("Finance record")) {
				String strfileVal1 = rowData.substring(11, 27);
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);

				String strfileVal2 = rowData.substring(39, 43);
				if (!(strfileVal1.contains("*") && strfileVal2.contains("*"))) {
					reporter.writeStepResult("Finance record", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("Finance record", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Fail",
							strReportFilename);
				}

			}

			// <AcctNum>
			if (rowData.contains("<AcctNum>")) {
				String[] strfileVal1 = rowData.split("<AcctNum>");
				String[] cardNum = strfileVal1[1].split("</AcctNum>");
				String maskingPart = cardNum[0].substring(6, cardNum[0].length() - 4);
				String[] strfileVal2 = rowData.split("<CardExp>");
				String[] expire = strfileVal2[1].split("</CardExp>");

				if (!(cardNum[0].contains("*") && expire[0].contains("*"))) {
					reporter.writeStepResult("AcctNum & CardExp", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + cardNum[0] + " Expire date :" + expire[0],
							"Pan " + cardNum[0] + " Expire date :" + expire[0] + " are not masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("AcctNum & CardExp", scenarioName, "Verify pan is not maskesd",
							"Pan Number : " + cardNum[0] + " Expire date :" + expire[0],
							"Pan " + cardNum[0] + " Expire date :" + expire[0] + " are masked", "Fail",
							strReportFilename);
				}

			}

		}

		if (username.equalsIgnoreCase("qatest9")) {
			// List<List<String>> data= arg1.raw();

			if (rowData.startsWith("I1")) {
				String strfileVal1 = rowData.substring(4, 23);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String unMaskingFront = strfileVal1.substring(0, 6);
				String unMaskingLast = strfileVal1.substring(strfileVal1.length() - 4, strfileVal1.length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || (concat.contains("*"))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					reporter.writeStepResult("I1", scenarioName, "Verify pan is masked", "Pan Number : " + strfileVal1,
							"Pan " + strfileVal1 + " is masked", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult("I1", scenarioName, "Verify pan is masked", "Pan Number : " + strfileVal1,
							"Pan " + strfileVal1 + " is not masked", "Fail", strReportFilename);
				}
			}

			if (rowData.startsWith("30")) {
				String strfileVal1 = rowData.substring(3, 22);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String strfileVal2 = rowData.substring(22, 26);
				strfileVal2 = strfileVal2.replaceAll("\\s+", "");
				String unMaskingFront = strfileVal1.substring(0, 6);
				String unMaskingLast = strfileVal1.substring(strfileVal1.length() - 4, strfileVal1.length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || concat.contains("*") || !strfileVal2.equals("****")) {
						flag = false;
						break;
					}

				}
				if (flag) {
					reporter.writeStepResult("30", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("30", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Fail",
							strReportFilename);
				}

			}
			if (rowData.startsWith("TAB")) {
				String strfileVal1 = rowData.substring(48, 67);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String strfileVal2 = rowData.substring(67, 71);
				strfileVal2 = strfileVal2.replaceAll("\\s+", "");
				String unMaskingFront = strfileVal1.substring(0, 6);
				String unMaskingLast = strfileVal1.substring(strfileVal1.length() - 4, strfileVal1.length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || concat.contains("*") || !strfileVal2.equals("****")) {
						flag = false;
						break;
					}

				}
				if (flag) {
					reporter.writeStepResult("TAB", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("TAB", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Fail",
							strReportFilename);
				}

			}
			if (rowData.startsWith("05")) {
				String strfileVal1 = rowData.substring(2, 21);
				strfileVal1 = strfileVal1.replaceAll("\\s+", "");
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);
				String unMaskingFront = strfileVal1.substring(0, 6);
				String unMaskingLast = strfileVal1.substring(strfileVal1.length() - 4, strfileVal1.length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || (concat.contains("*"))) {
						flag = false;
						break;
					}

				}
				if (flag) {
					reporter.writeStepResult("05", scenarioName, "Verify pan is masked", "Pan Number : " + strfileVal1,
							"Pan " + strfileVal1 + " is masked", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult("05", scenarioName, "Verify pan is masked", "Pan Number : " + strfileVal1,
							"Pan " + strfileVal1 + " is not masked", "Fail", strReportFilename);
				}

			}

			if (rowData.startsWith("Finance record")) {
				String strfileVal1 = rowData.substring(11, 27);
				String maskingPart = strfileVal1.substring(6, strfileVal1.length() - 4);

				String strfileVal2 = rowData.substring(39, 43);
				String unMaskingFront = strfileVal1.substring(0, 6);
				String unMaskingLast = strfileVal1.substring(strfileVal1.length() - 4, strfileVal1.length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || concat.contains("*") || !strfileVal2.equals("****")) {
						flag = false;
						break;
					}

				}
				if (flag) {
					reporter.writeStepResult("Finance record", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("Finance record", scenarioName, "Verify pan is masked",
							"Pan Number : " + strfileVal1 + " Expire date :" + strfileVal2,
							"Pan " + strfileVal1 + " Expire date :" + strfileVal2 + " are not masked", "Fail",
							strReportFilename);
				}

			}

			// <AcctNum>
			if (rowData.contains("<AcctNum>")) {
				String[] strfileVal1 = rowData.split("<AcctNum>");
				String[] cardNum = strfileVal1[1].split("</AcctNum>");
				String maskingPart = cardNum[0].substring(6, cardNum[0].length() - 4);
				String[] strfileVal2 = rowData.split("<CardExp>");
				String[] expire = strfileVal2[1].split("</CardExp>");
				String unMaskingFront = cardNum[0].substring(0, 6);
				String unMaskingLast = cardNum[0].substring(cardNum[0].length() - 4, cardNum[0].length());
				String concat = unMaskingFront + unMaskingLast;
				boolean flag = true;
				for (int i = 0; i < maskingPart.length() - 1; i++) {
					if ((!(maskingPart.charAt(i) == '*')) || concat.contains("*") || !expire[0].equals("****")) {
						flag = false;
						break;
					}

				}
				if (flag) {
					reporter.writeStepResult("AcctNum & CardExp", scenarioName, "Verify pan is masked",
							"Pan Number : " + cardNum[0] + " Expire date :" + expire[0],
							"Pan " + cardNum[0] + " Expire date :" + expire[0] + " are masked", "Pass",
							strReportFilename);
				} else {
					reporter.writeStepResult("AcctNum & CardExp", scenarioName, "Verify pan is masked",
							"Pan Number : " + cardNum[0] + " Expire date :" + expire[0],
							"Pan " + cardNum[0] + " Expire date :" + expire[0] + " are not masked", "Fail",
							strReportFilename);
				}

			}

		}
	}

	public String getElementText(String locatorKey) {
		WebElement element = getWebElement(locatorKey);
		waitForElementUsingPresence(locatorKey);
		String actualText = element.getText().trim();
		// System.out.println(actualText);
		return actualText;
	}

	public String getElementTextValue(String locatorKey) {
		WebElement element = getElementFluentWait(locatorKey);
		waitForElementUsingPresence(locatorKey);
		String actualText = element.getAttribute("value");
		System.out.println(actualText);
		/*
		 * if(actualText==null){ actualText=""; }
		 */
		return actualText;
	}

	public void enterLoginText(String locatorKey, String data) {
		try {
			WebElement element = getWebElement(locatorKey);
			waitForElementUsingPresence(locatorKey);
			element.sendKeys(data);
			// reporter.writeStepResult(tc_id, scenarioName, "Enter Value in
			// text field", data, "Value " + data + " entered successfully",
			// "Pass", strReportFilename);
		} catch (Exception e) {
			// reporter.writeStepResult(tc_id, scenarioName, "Enter Value in
			// text field", data, "Unable to enter value " + data, "Fail",
			// strReportFilename);
		}
	}

	public void clickLoginElement(String locatorKey, String strButtonLabel) {
		try {
			WebElement element = getWebElement(locatorKey);
			wait.until(ExpectedConditions.elementToBeClickable(element));
			element.click();
			// reporter.writeStepResult(tc_id, scenarioName, "Click on " +
			// strButtonLabel, strButtonLabel, "Clicked " + strButtonLabel + "
			// button successfully", "Pass", strReportFilename);
		} catch (Exception e) {
			// reporter.writeStepResult(tc_id, scenarioName, "Click on " +
			// strButtonLabel, strButtonLabel, "Not able to click on button " +
			// strButtonLabel, "Fail", strReportFilename);
		}
	}

	public void enterToDate(String locatorKey) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
		Date currentDate = new Date();

		System.out.println(dateFormat.format(currentDate));

		// convert date to calendar
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);

		// manipulate date

		c.add(Calendar.DATE, 0); // same with c.add(Calendar.DAY_OF_MONTH, 1);

		// convert calendar to date
		Date currentDateMinus = c.getTime();

		System.out.println(dateFormat.format(currentDateMinus));

		String monthBackDate = dateFormat.format(currentDateMinus);
		System.out.println(monthBackDate);
		String[] splitFullDate = monthBackDate.split(" ");
		String[] splitDate = splitFullDate[0].split("-");
		String[] splitTime = splitFullDate[1].split(":");
		for (int j = 0; j < splitDate.length; j++) {
			System.out.println("splitDate =" + splitDate[j]);

		}
		for (int i = 0; i < splitTime.length; i++) {
			System.out.println("splitTime =" + splitTime[i]);
		}
		String Tomon = splitDate[1];
		String Toyy = splitDate[0];
		String Todd = splitDate[2];
		int tohour = Integer.parseInt(splitTime[0]) - 1;
		String Tohour = String.valueOf(tohour);
		String Tominute = splitTime[1];
		String Tosec = splitTime[2];
		if (Todd.substring(0, 1).contains("0"))
			Todd = Todd.replace("0", "");
		if (Tominute.substring(0, 1).contains("0"))
			Tominute = Tominute.replace("0", "");
		if (Tosec.substring(0, 1).contains("0"))
			Tosec = Tosec.replace("0", "");
		String[] locatorMethodName = readProperties(locatorKey);
		String locatorMethod = locatorMethodName[0].toLowerCase();
		String locatorValue = null;
		if (locatorKey.equals("selectMon")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Tomon);
			clickAnElement("selectMon", locatorValue, "Month");
		}
		if (locatorKey.equals("selectYear")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Toyy);
			clickAnElement("selectYear", locatorValue, "Year");
		}
		if (locatorKey.equals("selectDay")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Todd);
			clickAnElement("selectDay", locatorValue, "Day");
		}
		if (locatorKey.equals("selectHour")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Tohour);
			clickAnElement("selectHour", locatorValue, "Hour");
		}
		if (locatorKey.equals("selectMin")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Tominute);
			clickAnElement("selectMin", locatorValue, "Min");
		}
		if (locatorKey.equals("selectSec")) {
			locatorValue = locatorMethodName[1].trim().replace("<>", Tosec);
			clickAnElement("selectSec", locatorValue, "Sec");
		}

	}

	public List<Map<String, String>> getCollectionFromTableForBigBatch(String xpathForTable, String[] columnName,
			RemoteWebDriver webDriver, String xpathForRowResult1, String xpathForRowResult2) {
		// StepExecutor stepExecutor = new StepExecutor(reporter);
		List<Map<String, String>> allTableDetails = new ArrayList();
		String[] Stringresult = xpathForRowResult1.split("#");
		xpathForRowResult1 = Stringresult[1];
		System.out.println();

		int totalNumOfRows = 0;
		if (xpathForTable.contains(".//*[@id='searchResult_BB']/thead/tr/th")) {
			String xpathForTableRows = "//*[@id='g2InboungF-ileRecordDetailsForm']/table[2]/tbody/tr/td[3]";
			String ele = webDriver.findElementByXPath(xpathForTableRows).getText().trim();
			ele = ele.substring(ele.indexOf("-") + 1, ele.length());
			System.out.println(ele);
			totalNumOfRows = Integer.parseInt(ele);
		} else if (xpathForTable.contains(".//*[@id='searchResult_INTERNAL_TRACE']/thead/tr/th")) {
			String xpathForTableRows = "//*[@id='RecorDetailsResultContainer']/table[2]/tbody/tr/td[3]";
			String ele = webDriver.findElementByXPath(xpathForTableRows).getText().trim();
			ele = ele.substring(ele.indexOf("-") + 1, ele.length());
			System.out.println(ele);
			totalNumOfRows = Integer.parseInt(ele);
		}

		totalNumOfRows = webDriver.findElementsByXPath(xpathForRowResult1).size();

		System.out.println();
		for (int j = 1; j <= totalNumOfRows; j++) {
			LinkedHashMap<String, String> tableFromUI = new LinkedHashMap<>();

			for (int i = 1; i <= columnName.length; i++) {

				String appendColName = "[contains(text(),'" + columnName[i - 1] + "')]";

				String xpathHeader = xpathForTable + appendColName;
				int colIndex = getColumnIndexFromTableForBigBatch(xpathForTable, columnName[i - 1], webDriver);
				if (xpathForTable.equalsIgnoreCase(".//*[@id='searchResult_BB']/thead/tr/th")
						|| xpathForTable.equalsIgnoreCase("//table[@id='searchResult']/thead/tr/th")
						|| xpathForTable.equalsIgnoreCase("//table[@id='searchResult3']/thead/tr/th")
						|| xpathForTable.equalsIgnoreCase(".//*[@id='searchResult_INTERNAL_TRACE']/thead/tr/th"))
					colIndex = colIndex;
				else
					colIndex = colIndex + 1;
				// List<WebElement> listOfElement =
				// webDriver.findElementsByXPath(xpathHeader);
				String abc = xpathForRowResult1 + "[" + j + "]/td[" + colIndex + "]";
				WebElement e = getWebElementByXpath(abc.trim(), webDriver);
				String rowValueForHeader = e.getText();
				System.out.println(rowValueForHeader);

				tableFromUI.put(columnName[i - 1], rowValueForHeader);

			}

			allTableDetails.add(tableFromUI);
		}
		return allTableDetails;
	}

	public int getColumnIndexFromTableForBigBatch(String xpathForTable, String columnName, RemoteWebDriver webDriver) {
		String[] Stringresult = xpathForTable.split("#");
		xpathForTable = Stringresult[1];
		List<WebElement> tableHeaders = webDriver.findElementsByXPath(xpathForTable);
		String xpathForColName = xpathForTable;

		int count = 1;
		for (int i = 2; i <= tableHeaders.size(); i++) {
			String headerName = webDriver.findElementByXPath(("(" + xpathForColName + ")[" + i + "]")).getText();
			if (headerName.equals(columnName)) {
				return count;
			}
			count++;
		}
		return count;
	}

	public WebElement getWebElementByXpath(String xpath, RemoteWebDriver webDriver) {
		WebElement element = null;
		try {
			element = webDriver.findElementByXPath(xpath);

			return element;
		} catch (Exception e) {
			System.out.println("Element not found for " + xpath);
		}
		return element;
	}

	public List<Map<String, String>> getCollectionOfUIData(String xpathForTable, String[] columnName,
			RemoteWebDriver webDriver, String xpathForRowResult1) {
		List<Map<String, String>> allTableDetails = new ArrayList();
		String[] Stringresult = xpathForTable.split("#");
		xpathForTable = Stringresult[1];
		System.out.println();

		LinkedHashMap<String, String> tableFromUI = new LinkedHashMap<>();

		for (int i = 1; i <= columnName.length; i++) {

			String appendColName = "[contains(text(),'" + columnName[i - 1] + "')]/following-sibling::span";

			String xpathHeader = xpathForTable + appendColName;

			String xpath = xpathForTable + "[contains(text(),'" + columnName[i - 1] + "')]";

			String rowValueForHeader = "";

			if (FunctionsLibrary.driver.findElements(By.xpath(xpathHeader)).size() == 0) {

				rowValueForHeader = "";

			} else {
				WebElement e = getWebElementByXpath(xpathHeader.trim(), webDriver);
				rowValueForHeader = e.getText();
				System.out.println(rowValueForHeader);
			}

			tableFromUI.put(columnName[i - 1], rowValueForHeader);
			allTableDetails.add(tableFromUI);
		}

		return allTableDetails;
	}

	public Map<String, String> jsonMapValues() throws Throwable {
		Map<String, String> inputExpMap = new HashMap<String, String>();
		String headerNamesSelectedTran = new ExecutionerClass().setEnv().getProperty("selectedTransactionNm");
		// String headerNamesSelectedTran = new
		// ApplicationPropertiesInitializer().getApplicationDataObject().getProperty("selectedTransactionNm");
		String[] headerNamesSelectedTrnsaction = headerNamesSelectedTran.split("\\|");
		for (int i = 0; i < headerNamesSelectedTrnsaction.length; i++) {
			String arg2 = parseJson(headerNamesSelectedTrnsaction[i], tc_id, "Input");
			inputExpMap.put(headerNamesSelectedTrnsaction[i], arg2.replace("[/[/]\"]+", ""));
		}
		return inputExpMap;
	}

	public Map<String, String> jsonMapValues(String keyName) throws Throwable {
		Map<String, String> inputExpMap = new HashMap<String, String>();
		String headerNamesSelectedTran = new ExecutionerClass().setEnv().getProperty(keyName.replace(" ", "_"));
		// String headerNamesSelectedTran = new
		// ApplicationPropertiesInitializer().getApplicationDataObject().getProperty(keyName.replace("
		// ", "_"));
		String[] headerNamesSelectedTrnsaction = headerNamesSelectedTran.split("\\|");
		for (int i = 0; i < headerNamesSelectedTrnsaction.length; i++) {
			String arg2 = parseJson(headerNamesSelectedTrnsaction[i], tc_id, keyName);
			inputExpMap.put(headerNamesSelectedTrnsaction[i], arg2.replaceAll("[\\[\\]\"]+", ""));
		}
		return inputExpMap;
	}

	/* Parse Json */
	public String parseJson(String data, String testCaseID, String ipop) {

		String value = "";
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("Data/NewContract.json"));
			System.out.println();
			JSONObject lev1 = (JSONObject) obj;
			Object jObj = lev1.get(testCaseID);
			if (jObj instanceof Map) {
				Map map = (Map) jObj;

				if (ipop.equals("Input")) {
					Object in = map.get("Input");
					Map map1 = (Map) in;
					value = map1.get(data).toString();
				} else if (ipop.equals("Output")) {
					Object Out = map.get("Output");
					Map map1 = (Map) Out;
					value = map1.get(data).toString();
				} else {
					try {
						Object in = map.get(ipop);
						Map map1 = (Map) in;
						value = map1.get(data).toString();
					} catch (Exception e) {
						System.out.println("Not found for :" + ipop + "  " + data);
					}
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		} catch (IOException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		} catch (ParseException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
		return value;
	}

	public Map<String, String> getDataMessagePayLoad(String msgPayload) throws Throwable {
		Map<String, String> mapUIacqresmsgPay = new HashMap<String, String>();
		String[] acqresmsgPay = msgPayload.split("\\n");
		for (String acqrespmsg : acqresmsgPay) {
			if (acqrespmsg.contains(":")) {
				String expacqrespmsg[] = acqrespmsg.split("\\:");
				mapUIacqresmsgPay.put(expacqrespmsg[0].trim(), expacqrespmsg[1].replaceAll("[\\[\\]]+", "").trim());

			} else {
				String expacqrespmsg[] = acqrespmsg.split("\\s+");
				mapUIacqresmsgPay.put(expacqrespmsg[0].trim(), expacqrespmsg[2].replaceAll("[\\[\\]]+", "").trim());
			}
		}
		return mapUIacqresmsgPay;

	}

	/**
	 * Parse the test data
	 */
	public Map<String, String> parse(String testCaseID, String filename) {
		String Value = "";
		Map<String, String> keyVal = new HashMap<String, String>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Map<String, String>> map = mapper.readValue(new File("Data/" + filename + ".json"),
					new TypeReference<Map<String, Map<String, String>>>() {
					});

			// System.out.println("Map is " + map);

			String key = "";
			String val = "";
			for (int i = 1; i <= map.size(); i++) {
				Map<String, String> str = map.get(testCaseID);

				for (Entry<String, String> data : str.entrySet()) {
					// System.out.println("Key is " + data.getKey() + " Value is " +
					// data.getValue());
					key = data.getKey();
					val = data.getValue();
					keyVal.put(key, val);
				}
			}
			// Value=keyVal.get(value);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyVal;
	}

	/**
	 * Login to the application
	 *
	 * @throws Exception
	 */
	public void loginApplication() throws Exception {
		EncryptFileData encryptFile = new EncryptFileData();
		Properties config = new ExecutionerClass().setEnv();
		if (!Boolean.valueOf(config.getProperty("isFileDataEncrypted"))) {
			try {
				encryptFile.encryptConfigFileData("Password");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
			}
			encryptFile.serializeKeys();
		}
		config = new ExecutionerClass().setEnv();
		final String userName = config.getProperty("Username");
		EncryptFileData.secretKey = encryptFile.deserializeKey();

		final String password = encryptFile.decrypt(config.getProperty("Password"), EncryptFileData.secretKey);
		System.out.println("Decrypted password : " + password);

		String browser = config.getProperty("browser");

		try {
			if ((browser.equalsIgnoreCase("IE")) || (browser.equalsIgnoreCase("InternetExplorer"))) {
				login(userName, password);

			} else if (browser.equalsIgnoreCase("Chrome")) {
				login(userName, password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * login
	 *
	 * @throws AWTException
	 * @throws InterruptedException
	 */
	public void login(String userName, String password) throws AWTException, InterruptedException {
		Robot r = new Robot();
		StringSelection selection = new StringSelection(userName);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		System.out.println(clipboard);
		Thread.sleep(3000);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_C);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_C);
		Thread.sleep(500);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_V);
		Thread.sleep(500);
		r.keyPress(KeyEvent.VK_TAB);
		r.keyRelease(KeyEvent.VK_TAB);
		selection = new StringSelection(password);
		Clipboard clipboard1 = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard1.setContents(selection, selection);
		Thread.sleep(2000);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_C);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_C);
		Thread.sleep(500);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_V);
		Thread.sleep(1000);
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(2000);
	}

	public void runScript(String strScriptName) {
		if (strScriptName.contains(".exe")) {
			try {
				Runtime.getRuntime().exec("\"" + strVBScriptsPath + "\\" + strScriptName + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (strScriptName.contains(".vbs")) {
			try {
				Runtime.getRuntime().exec("wscript.exe \"" + strVBScriptsPath + "\\" + strScriptName + "\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Verify drop down
	 */
	public void verifyDropdownvalues(String locatorKey, String expectedvalue) {
		boolean exists = false;
		WebElement element = getWebElement(locatorKey);
		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		for (WebElement we : options) {
			if (we.getText().equals(expectedvalue)) {
				we.click();
				exists = true;
			}
		}
		if (exists) {
			reporter.writeStepResult(tc_id, scenarioName, "User verifies dropdown values",
					"User verifies " + expectedvalue + " dropdown value is present",
					"the " + expectedvalue + " dropdown value is present", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "User verifies dropdown values",
					"User verifies " + expectedvalue + " dropdown value is present",
					"the " + expectedvalue + " dropdown value is not present", "Fail", strReportFilename);
		}
	}

	/**
	 * Verify drop down values which are not present
	 */
	public void verifyDropDownValNotPresent(String locatorKey, String expectedvalue) {
		boolean notExist = false;
		WebElement element = getWebElement(locatorKey);
		Select select = new Select(element);
		List<WebElement> options = select.getOptions();
		for (WebElement we : options) {
			if (!we.getText().equals(expectedvalue)) {
				notExist = true;
			}
		}
		if (notExist) {
			reporter.writeStepResult(tc_id, scenarioName, "User verifies dropdown values",
					"User verifies " + expectedvalue + " dropdown values present",
					"the " + expectedvalue + " dropdown values are not present", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "User verifies dropdown values",
					"User verifies " + expectedvalue + " dropdown values present",
					"the " + expectedvalue + " dropdown values are present", "Fail", strReportFilename);
		}
	}

	public String RGBAToHeXa(String RGBA) {

		String colorarray[] = RGBA.substring(5).split(",");

		int R = Integer.parseInt(colorarray[0].trim());
		int G = Integer.parseInt(colorarray[1].trim());
		int B = Integer.parseInt(colorarray[2].trim());

		// int aa = (int)(alpha * 255.99);
		String hex = String.format("#%02x%02x%02x", R, G, B);
		System.out.println(hex);
		return hex;
	}

	/**
	 * Launch an application
	 */
	public void launchApplication(String URL) {
		driver.get(URL);
	}

	public void globalWait(int seconds) {

		try {
			for (int i = 0; i < seconds; i++) {
				Thread.sleep(1000);

			}
		} catch (Exception e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
	}

	public void SwitchUsingTitle(String title) {
		Set<String> totalWindow = driver.getWindowHandles();
		if (totalWindow.size() >= 1) {
			for (String loopWindow : totalWindow) {
				System.out.println(loopWindow);
				driver.switchTo().window(loopWindow);
				System.out.println(driver.getTitle().trim() + "******" + title);
				if (driver.getTitle().trim().contains(title)) {
					break;
				}
			}
		} else {
			System.out.println("No Windows was Found");
		}
	}

	public void waitForElementUsingPresence(String locatorMethod, String locatorValue) {
		WebElement element = null;

		try {
			switch (locatorMethod) {
			case "id":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.id(locatorValue)));
				break;
			case "name":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.name(locatorValue)));
				break;
			case "class":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.className(locatorValue)));
				break;
			case "linkText":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(locatorValue)));
				break;
			case "partiallinkText":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(locatorValue)));
				break;
			case "tagname":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(locatorValue)));
				break;
			case "css":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locatorValue)));
				break;
			case "xpath":
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locatorValue)));
				break;

			default:
				break;
			}
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Find Element By " + locatorMethod + " with Value " + locatorValue + " in the webpage",
					"The Locator Method and Value should be available in the page", e.getMessage(), "Fail",
					strReportFilename);
		}
	}

	public ArrayList<String> extractColumnsDatainWB(String columnNo, String columnName, ArrayList<String> data) {

		int pages = Integer.parseInt(
				FunctionsLibrary.driver.findElement(By.xpath("//div[@class='rgWrap rgInfoPart']/strong[2]")).getText());
		try {
			for (int j = 1; j <= pages; j++) {
				int size = FunctionsLibrary.driver.findElements(By.xpath("//table[@id='RadGrid1_ctl00']/tbody/tr"))
						.size();
				for (int i = 1; i <= size; i++) {
					String rowData = FunctionsLibrary.driver
							.findElement(
									By.xpath("//table[@id='RadGrid1_ctl00']/tbody/tr[" + i + "]/td[" + columnNo + "]"))
							.getText();
					data.add(rowData.toLowerCase());
				}
				FunctionsLibrary.driver.findElement(By.xpath("//input[@class='rgPageNext']")).click();
				waitForPageLoad();
			}
			System.out.println(data);
			FunctionsLibrary.driver.findElement(By.xpath("//input[@class='rgPageFirst']")).click();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public ArrayList<Date> extractDateColumnsDatainWB(String columnNo, String columnName, ArrayList<Date> dates)
			throws InterruptedException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		int pages = Integer.parseInt(
				FunctionsLibrary.driver.findElement(By.xpath("//div[@class='rgWrap rgInfoPart']/strong[2]")).getText());
		try {
			for (int j = 1; j <= pages; j++) {
				int size = FunctionsLibrary.driver.findElements(By.xpath("//table[@id='RadGrid1_ctl00']/tbody/tr"))
						.size();
				for (int i = 1; i <= size; i++) {
					String date = FunctionsLibrary.driver
							.findElement(
									By.xpath("//table[@id='RadGrid1_ctl00']/tbody/tr[" + i + "]/td[" + columnNo + "]"))
							.getText();
					Date parsedDate = df.parse(date);
					dates.add(parsedDate);
				}
				FunctionsLibrary.driver.findElement(By.xpath("//input[@class='rgPageNext']")).click();
				waitForPageLoad();
			}
			System.out.println(dates);
			FunctionsLibrary.driver.findElement(By.xpath("//input[@class='rgPageFirst']")).click();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dates;
	}

	public void selectMultipleValuesInDropDown(String locatorKey, String Option, ArrayList<String> data)
			throws InterruptedException {
		WebElement element = getWebElementWithWait(locatorKey);
		Select sel = new Select(element);
		for (int i = 0; i < data.size(); i++) {
			try {
				if (Option.equalsIgnoreCase("VisibleText")) {
					sel.selectByVisibleText(data.get(i));
				} else if (Option.equalsIgnoreCase("Value")) {
					sel.selectByValue(data.get(i));
				} else if (Option.equalsIgnoreCase("Index")) {
					int index = Integer.parseInt(data.get(i));
					sel.selectByIndex(index);
				}
				reporter.writeStepResult(tc_id, scenarioName, "Select value from " + locatorKey + " Listbox",
						data.get(i), "Expected value " + data.get(i) + " is selected in the listbox", "Pass",
						strReportFilename);
			} catch (Exception e) {
				reporter.writeStepResult(tc_id, scenarioName, "Select value from " + locatorKey + " Listbox",
						data.get(i), "Expected value " + data.get(i) + " is not present in the listbox", "Pass",
						strReportFilename);
			}
		}
	}

	/*
	 * public boolean verifyElementPresentByFindElements(String locatorKey,String
	 * label) { List<WebElement> element = null; boolean exists=false; try { element
	 * = getWebElements(locatorKey); if (!(element.size() == 0)) exists = true; else
	 * exists = false;
	 *
	 * } catch (Exception e) { } if(exists) return true; else return false; }
	 */

	public void verifyElementTextPresent(WebElement element, String strExpectedText) {
		String strActualText = null;
		try {
			strActualText = element.getText().trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present on the page", strExpectedText,
					e.getMessage(), "Fail", strReportFilename);
		}
		System.out.println("***" + strExpectedText + "***");
		System.out.println("***" + strActualText + "***");
		if (strActualText.equals(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element",
					"Expected: " + strExpectedText, "Actual: " + strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify text is present in the element", strExpectedText,
					strActualText, "Fail", strReportFilename);
	}

	// Reading data from ".doc" file.
	public String[] readDocFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		HWPFDocument doc = new HWPFDocument(fis);

		WordExtractor extractor = new WordExtractor(doc);
		String[] getDocParagraphs = extractor.getParagraphText(); // Getting all the paragraphs from the document and
																	// adding the same in String array.
		int totalParagraphs = getDocParagraphs.length; // Getting total number of paragraphs in word document.
		System.out.println("Total count of paragraphs : " + totalParagraphs + "\n");
		for (String currentPara : getDocParagraphs) {
			System.out.print(currentPara);
		}
		// extractor.close();
		return getDocParagraphs;
	}

	public void verifyElementStatus(String locatorKey, String status) {
		try {
			WebElement element = getElementFluentWait(locatorKey);
			boolean actualStatus = element.isEnabled();
			boolean result = false;

			if (status.equalsIgnoreCase("enabled")) {
				if (actualStatus)
					result = true;
			}
			if (status.equalsIgnoreCase("disabled")) {
				if (!actualStatus)
					result = true;
			}
			if (result)
				reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + locatorKey,
						"Status of the element should be " + status, "Status of the element is " + status, "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + locatorKey,
						"Status of the element should be " + status, "Status of the element is not as expected", "Fail",
						strReportFilename);

		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + locatorKey,
					"Status of the element should be " + status, "Status of the element is not as expected", "Fail",
					strReportFilename);
		}
	}

	public void verifyElementStatus(WebElement element, String status, String label) {
		try {
			boolean actualStatus = element.isEnabled();
			boolean result = false;

			if (status.equalsIgnoreCase("enabled")) {
				if (actualStatus)
					result = true;
			}
			if (status.equalsIgnoreCase("disabled")) {
				if (!actualStatus)
					result = true;
			}
			if (result)
				reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + label,
						"Status of the element should be " + status, "Status of the element is " + status, "Pass",
						strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + label,
						"Status of the element should be " + status, "Status of the element is not as expected", "Fail",
						strReportFilename);

		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Status of the element " + label,
					"Status of the element should be " + status, "Status of the element is not as expected", "Fail",
					strReportFilename);
		}
	}

	public void verifyElementAttributeValue(String locatorKey, String attributeType, String expectedVal) {

		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getAttribute(attributeType).trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + attributeType + " attribute of the element " + locatorKey, expectedVal, e.getMessage(),
					"Fail", strReportFilename);
		}
		if (strActualText.equals(expectedVal))
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + attributeType + " attribute of the element " + locatorKey, expectedVal, strActualText,
					"Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName,
					"Verify " + attributeType + " attribute of the element " + locatorKey, expectedVal, strActualText,
					"Fail", strReportFilename);

	}

	public String getAttributeValueOfAnElement(String locatorKey, String attributeType) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getAttribute(attributeType).trim();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return strActualText;

	}

	public void verifyRadioButtonStatus(String locatorKey, String expectedStatus) {
		boolean actualStatus = false;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				actualStatus = element.isSelected();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Radio Button Status of the element " + locatorKey,
					expectedStatus, e.getMessage(), "Fail", strReportFilename);
		}
		if (expectedStatus.equals("Selected")) {
			if (actualStatus)
				reporter.writeStepResult(tc_id, scenarioName, "Verify Radio Button Status of the element " + locatorKey,
						"The Element should be Selected", "The Element is selected", "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify Radio Button Status of the element " + locatorKey,
						"The Element should be Selected", "The Element is not selected", "Fail", strReportFilename);
		}

		if (expectedStatus.equals("Not Selected")) {
			if (!actualStatus)
				reporter.writeStepResult(tc_id, scenarioName, "Verify Radio Button Status of the element " + locatorKey,
						"The Element should not be Selected", "The Element is not selected", "Pass", strReportFilename);
			else
				reporter.writeStepResult(tc_id, scenarioName, "Verify Radio Button Status of the element " + locatorKey,
						"The Element should not be Selected", "The Element is selected", "Fail", strReportFilename);
		}
	}

	public void verifyValueNotPresentInTheTextBox(String Label, String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getAttribute("value").trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, e.getMessage(), "Fail", strReportFilename);
		}
		if (!strActualText.equals(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value is not present in the TextBox",
					strExpectedText, strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, strActualText, "Fail", strReportFilename);
	}

	public void savePDFPrintDocs() {
		Robot robot;
		try {
			robot = new Robot();
			globalWait(15);

			robot.mouseMove(936, 208);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			// Shortcut to save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			// robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_S);
			// robot.keyRelease(KeyEvent.VK_SHIFT);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			String ProjectPath = new File("").getAbsolutePath();
			String PDFFolder = ProjectPath + "\\Files\\PDF\\";
			int x = (int) (Math.random() * 999999) + 1126;
			String PDFName = "hpstExhibitE" + x + ".pdf";
			String PDFPath = PDFFolder + PDFName;
			setEnvironmentVariable("PDFurl", PDFPath);

			// Store the path of PDF to clipboard
			StringSelection selection = new StringSelection(PDFPath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			globalWait(3);
			// Paste the path of PDF from clipboard and save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			globalWait(1);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			globalWait(1);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			driver.switchTo().defaultContent();

			driver.close();

			SwitchUsingTitle("");

			driver.switchTo().defaultContent();

		} catch (AWTException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		} catch (Exception e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
	}

	public void verifyValueContainsInTheTextBox(String Label, String locatorKey, String strExpectedText) {
		String strActualText = null;
		WebElement element = null;
		try {
			element = getElementFluentWait(locatorKey);
			waitForElementUsingVisibility(locatorKey);

			if (verifyElementPresent(locatorKey))
				strActualText = element.getAttribute("value").trim();
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, e.getMessage(), "Fail", strReportFilename);
		}
		if (strActualText.contains(strExpectedText))
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, strActualText, "Pass", strReportFilename);
		else
			reporter.writeStepResult(tc_id, scenarioName, "Verify " + Label + " value present in the TextBox",
					strExpectedText, strActualText, "Fail", strReportFilename);
	}

	public void savePDF_for_specific_file() {
		Robot robot;
		try {
			robot = new Robot();
			globalWait(8);

			robot.mouseMove(936, 208);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);

			// Shortcut to save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			// robot.keyPress(KeyEvent.VK_SHIFT);
			robot.keyPress(KeyEvent.VK_S);
			robot.keyRelease(KeyEvent.VK_S);
			// robot.keyRelease(KeyEvent.VK_SHIFT);
			robot.keyRelease(KeyEvent.VK_CONTROL);

			String ProjectPath = new File("").getAbsolutePath();
			String PDFFolder = ProjectPath + "\\Files\\PDF\\";
			int x = (int) (Math.random() * 999999) + 1126;
			String PDFName = "hpstExhibitE" + x + ".pdf";
			String PDFPath = PDFFolder + PDFName;
			setEnvironmentVariable("PDFurl", PDFPath);

			// Store the path of PDF to clipboard
			StringSelection selection = new StringSelection(PDFPath);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);

			globalWait(3);
			// Paste the path of PDF from clipboard and save the PDF
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			globalWait(1);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			globalWait(1);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);

			driver.switchTo().defaultContent();
			driver.close();
			SwitchUsingTitle("");
			driver.switchTo().defaultContent();

		} catch (AWTException e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		} catch (Exception e) {
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: Handled Full Stack Trace Information :: ==========================");

			ExceptionUtils.getFullStackTrace(e);
			;
			System.out.println(java.time.LocalDateTime.now() + "-:: "
					+ "====================== :: ============================================ :: ==========================");
		}
	}

	public WebElement getWebElementWithQuickWait(String elementAddress) throws InterruptedException {
		if (this.driver == null)
			return null;

		int defaultTimeOut = 5;

		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(elementAddress);
			locatorMethod = locatorMethodName[0];
			locatorValue = locatorMethodName[1];
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + elementAddress,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}

		final String eleAddress = locatorValue;
		final WebDriver driver = this.driver;
		final String locator = locatorMethod;
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		WebElement returnEle = null;
		wait.withTimeout(defaultTimeOut, TimeUnit.SECONDS);
		wait.pollingEvery(2, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		WebElement element = null;
		int elementCheck = 1;

		while (element == null && elementCheck <= elementCheckCount) {
			try {
				element = wait.until(new Function<WebDriver, WebElement>() {

					public WebElement apply(WebDriver driver) {

						WebElement ele = null;

						switch (locator) {
						case "id":
							ele = driver.findElement(By.id((eleAddress)));

							break;
						case "name":
							ele = driver.findElement(By.name((eleAddress)));
							break;
						case "class":
							ele = driver.findElement(By.className((eleAddress)));
							break;
						case "linkText":
							ele = driver.findElement(By.linkText((eleAddress)));
							break;
						case "partiallinkText":
							ele = driver.findElement(By.partialLinkText((eleAddress)));
							break;
						case "tagname":
							ele = driver.findElement(By.tagName((eleAddress)));
							break;
						case "css":
							ele = driver.findElement(By.cssSelector((eleAddress)));
							break;
						case "xpath":
							ele = driver.findElement(By.xpath((eleAddress)));
							break;

						default:
							break;
						}
						CommonStepDefinitions.exceptioncounter = 0;
						// highlightElement(ele);
						if (ele != null) {

							// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element located in
							// DOM
							// successfully.. with FluentWait");

							return ele;

						} else {
							/*
							 * System.out. println("Element not located in DOM for " + eleAddress +
							 * " with FluentWait, Returning null value");
							 */

							return null;

						}
					}

				});
			} catch (TimeoutException te) {
				// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element not found "
				// + element + " for " +
				// eleAddress);
				// te.getStackTrace();
				ExceptionUtils.getFullStackTrace(te);
				if (elementCheck == elementCheckCount) {
					// tExceptionUtils.getFullStackTrace(e);;
					CommonStepDefinitions.exceptioncounter++;
					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page", "Element By "
					 * + locatorMethod + " with Value " + locatorValue +
					 * " is Not Available in the webpage", "INFO", strReportFilename);
					 */
				}
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(te));
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page",
					 * "The Element is not available and Execution is Not Completed for this TestCase"
					 * , "Not Completed", strReportFilename);
					 */

				}

			} catch (Exception e) {
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					ExceptionUtils.getFullStackTrace(e);
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: ============================================ :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + e.getStackTrace());
					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page",
					 * "Page is not fully loaded hence unable to fetch the elmement details ",
					 * "INFO", strReportFilename);
					 */
				}
				// globalWait();
			}

			elementCheck++;
		}
		return element;
	}

	public WebElement getWebElementWithCustomWaitAndIteration(String elementAddress, int waitTime, int iteration)
			throws InterruptedException {
		if (this.driver == null)
			return null;

		int defaultTimeOut = waitTime;

		String locatorMethod = null;
		String locatorValue = null;
		try {
			String[] locatorMethodName = readProperties(elementAddress);
			locatorMethod = locatorMethodName[0];
			locatorValue = locatorMethodName[1];
		} catch (Exception e) {
			reporter.writeStepResult(tc_id, scenarioName, "Fetch LocatorMethod and LocatorValue for " + elementAddress,
					"LocatorMethod: " + locatorMethod + ";" + "LocatorValue: " + locatorValue, e.getMessage(), "Fail",
					strReportFilename);
		}

		final String eleAddress = locatorValue;
		final WebDriver driver = this.driver;
		final String locator = locatorMethod;
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver);
		WebElement returnEle = null;
		wait.withTimeout(defaultTimeOut, TimeUnit.SECONDS);
		wait.pollingEvery(2, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		WebElement element = null;
		int elementCheck = 1;

		while (element == null && elementCheck <= iteration) {
			try {
				element = wait.until(new Function<WebDriver, WebElement>() {

					public WebElement apply(WebDriver driver) {

						WebElement ele = null;

						switch (locator) {
						case "id":
							ele = driver.findElement(By.id((eleAddress)));

							break;
						case "name":
							ele = driver.findElement(By.name((eleAddress)));
							break;
						case "class":
							ele = driver.findElement(By.className((eleAddress)));
							break;
						case "linkText":
							ele = driver.findElement(By.linkText((eleAddress)));
							break;
						case "partiallinkText":
							ele = driver.findElement(By.partialLinkText((eleAddress)));
							break;
						case "tagname":
							ele = driver.findElement(By.tagName((eleAddress)));
							break;
						case "css":
							ele = driver.findElement(By.cssSelector((eleAddress)));
							break;
						case "xpath":
							ele = driver.findElement(By.xpath((eleAddress)));
							break;

						default:
							break;
						}
						CommonStepDefinitions.exceptioncounter = 0;
						// highlightElement(ele);
						if (ele != null) {
							// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element located in
							// DOM
							// successfully.. with FluentWait");
							return ele;
						} else {
							/*
							 * System.out. println("Element not located in DOM for " + eleAddress +
							 * " with FluentWait, Returning null value");
							 */
							return null;
						}

					}

				});
			} catch (TimeoutException te) {
				// System.out.println(java.time.LocalDateTime.now()+"-:: "+"Element not found "
				// + element + " for " +
				// eleAddress);
				// te.getStackTrace();
				ExceptionUtils.getFullStackTrace(te);
				if (elementCheck == elementCheckCount) {
					// tExceptionUtils.getFullStackTrace(e);;
					CommonStepDefinitions.exceptioncounter++;
					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page", "Element By "
					 * + locatorMethod + " with Value " + locatorValue +
					 * " is Not Available in the webpage", "INFO", strReportFilename);
					 */
				}
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + ExceptionUtils.getFullStackTrace(te));
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page",
					 * "The Element is not available and Execution is Not Completed for this TestCase"
					 * , "Not Completed", strReportFilename);
					 */

				}

			} catch (Exception e) {
				if (elementCheck == elementCheckCount) {
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: Handled Full Stack Trace Information :: ==========================");

					ExceptionUtils.getFullStackTrace(e);
					System.out.println(java.time.LocalDateTime.now() + "-:: "
							+ "====================== :: ============================================ :: ==========================");
					System.out.println(java.time.LocalDateTime.now() + "-:: " + e.getStackTrace());
					/*
					 * reporter.writeStepResult(tc_id, scenarioName, "Find Element By " +
					 * locatorMethod + " with Value " + locatorValue + " in the webpage",
					 * "The Locator Method and Value should be available in the page",
					 * "Page is not fully loaded hence unable to fetch the elmement details ",
					 * "INFO", strReportFilename);
					 */
				}
			}

			elementCheck++;
		}
		return element;
	}

	public void stopTheTCExecution() {
		reporter.writeStepResult(tc_id, scenarioName, "Aborting the Test case  execution due the Error",
				"Stop the execution", "Execution stopped", "Not Completed", strReportFilename);
		/*
		 * reporter.writeStepResult(tc_id, scenarioName,
		 * "Re-login the user due to the failure",
		 * "The Locator Method and Value should be available in the page",
		 * "Close the driver instance and re-login the application.", "Not Completed",
		 * strReportFilename);
		 */
		CommonStepDefinitions.executeScenario = false;
		this.driver.quit();
		this.driver = null;
	}
}