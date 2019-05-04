package com.webapp.baseLibrary;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.executioner.ExecutionerClass;
import com.webapp.utilities.EncryptFileData;

public class LaunchLogin extends ExecutionerClass {
	public static RemoteWebDriver driver = null;
	static Properties object,browserLoad = null;
	static WebDriverWait wait;

	public LaunchLogin() throws Exception {
		if(driver == null)
		{
			initilizeBrowser(new ExecutionerClass().setEnv().getProperty("browser"));

			String objectFileName = "src/test/resources/config/object.properties";
			object = loadPropertiesFile(objectFileName);
		}
	}

	public Properties loadPropertiesFile(String propFilePath) {
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream fis = new FileInputStream(propFilePath);
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * To initialize/launch browser
	 * @param browser
	 */
	public static void initilizeBrowser(String browser){
		browser = browser.toLowerCase().trim();
		switch (browser) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;
		case "firefox":
			System.setProperty("webdriver.gecko.driver", "Config/Drivers/geckodriver.exe");
			driver = new FirefoxDriver();
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			break;
		case "ie":
			System.setProperty("webdriver.ie.driver", "src/test/resources/drivers/IEDriverServer.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(CapabilityType.BROWSER_NAME, "ie");
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			capabilities.setCapability("requireWindowFocus", true);
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
		wait=new WebDriverWait(driver, 60);
	}

	/**
	 * Launch an application
	 */
	public void launchApplication(String URL)
	{
		driver.get(URL);
	}
	public static RemoteWebDriver getWebDriver()
	{
		
		return driver;
		
	}

	/**
	 * Close application
	 */
	public void closeApplication()
	{
		driver.close();
	}

	/**
	 * Login to the application
	 * @throws Exception 
	 */
	public void login() throws Exception  {
		EncryptFileData encryptFile = new EncryptFileData();
		@SuppressWarnings("resource")
		Properties config = new ExecutionerClass().setEnv();
		if(!Boolean.valueOf(config.getProperty("isFileDataEncrypted"))){
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
		final String userName= config.getProperty("Username");
		EncryptFileData.secretKey = encryptFile.deserializeKey();

		final String password =encryptFile.decrypt(config.getProperty("Password"), EncryptFileData.secretKey);
		String browser=config.getProperty("browser");
		//String browser=applicationProperties.getApplicationPropertiesObject().getProperty("browser");

		try {
			if((browser.equalsIgnoreCase("IE"))||(browser.equalsIgnoreCase("InternetExplorer")))
			{
				Robot r = new Robot();
				StringSelection selection = new StringSelection(userName);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection); 
				System.out.println(clipboard);
				Thread.sleep(500);
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
				Thread.sleep(500);
				r.keyPress(KeyEvent.VK_CONTROL);
				r.keyPress(KeyEvent.VK_C);
				r.keyRelease(KeyEvent.VK_CONTROL);
				r.keyRelease(KeyEvent.VK_C);
				r.keyPress(KeyEvent.VK_CONTROL);
				r.keyPress(KeyEvent.VK_V);
				r.keyRelease(KeyEvent.VK_CONTROL);
				r.keyRelease(KeyEvent.VK_V);
				Thread.sleep(500);
				r.keyPress(KeyEvent.VK_ENTER);
				r.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(3000);
			}
			else if(browser.equalsIgnoreCase("Chrome"))
			{
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
				r.keyPress(KeyEvent.VK_CONTROL);
				r.keyPress(KeyEvent.VK_V);
				r.keyRelease(KeyEvent.VK_CONTROL);
				r.keyRelease(KeyEvent.VK_V);
				Thread.sleep(2000);
				r.keyPress(KeyEvent.VK_ENTER);
				r.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(2000);
			}
			else if(browser.equalsIgnoreCase("FireFox"))
			{
				Runtime.getRuntime().exec("C:/workspace/testngwebapplication/FireFoxAutoIt.exe");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Login to the application with invalid credentials
	 * @throws Exception 
	 */
	public void InvalidLogin() throws AWTException, InterruptedException
	{
		Robot r = new Robot();
		StringSelection selection = new StringSelection("panjurth1");
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
		selection = new StringSelection("Sag2CaCa");
		Clipboard clipboard1 = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard1.setContents(selection, selection);
		Thread.sleep(3000);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_C);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_C);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_V);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_V);
		Thread.sleep(3000);
		r.keyPress(KeyEvent.VK_ENTER);
		r.keyRelease(KeyEvent.VK_ENTER);
	}

}
