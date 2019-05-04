package com.webapp.executioner;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.utilities.GridReporter;
import com.webapp.utilities.HTML;

import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;

/*@CucumberOptions
		(
		strict = true,
		monochrome = true,
		features = "src/test/resources/features",
		glue = {"com.webapp.stepDefinition"},
		plugin = {"pretty", "html:target/cucumber-html-report" },
		tags={"@release222","@release11"}
		)*/
/**
 * Executioner class :
 * @author Prabhuling Kalshetti
 */
public class ExecutionerClass extends AbstractTestNGCucumberTests
{

	public static GridReporter reporter= new GridReporter();
	public static Properties config = null;
	public static WebDriver driver = null;
	public static long executionStartTime;
	public void LoadConfigProperty() throws IOException
	{
		config = new Properties();
		FileInputStream ip = new FileInputStream(
				System.getProperty("user.dir") + "//src//test//resources//config//config.properties");
		config.load(ip);
	}

	public static GridReporter getReporter()
	{
	return reporter;
	}

	public Properties setEnv() throws Exception {
		LoadConfigProperty();
		executionStartTime=reporter.startTime;
		return config;
	}

	public static String currentDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String cal1 = dateFormat.format(cal.getTime());
		return cal1;
	}

	public void cucumberOption() {
		List<String> arguments = new ArrayList<String>();
		arguments.add("src/test/resources/features");
		arguments.add("--plugin");
		arguments.add("html:target/cucumber-html-report");
		arguments.add("--glue");
		arguments.add("com.webapp.stepDefinition");

		final String[] argv = arguments.toArray(new String[0]);
		RuntimeOptions runtimeOptions = new RuntimeOptions(new ArrayList(Arrays.asList(argv)));
		MultiLoader resourceLoader = new MultiLoader(this.getClass().getClassLoader());
		ResourceLoaderClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader,
				this.getClass().getClassLoader());
		cucumber.runtime.Runtime runtime = new cucumber.runtime.Runtime(resourceLoader, classFinder,
				this.getClass().getClassLoader(), runtimeOptions);
		try {
			runtime.run();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@BeforeSuite
	public void setUp() throws Exception
	{
		LoadConfigProperty();
		setEnv();
		HTML.fnSummaryInitialization("Execution Summary Report");
		cucumberOption();
	}

	@AfterSuite(alwaysRun = true)
	public void quit() throws Exception {
		HTML.fnSummaryCloseHtml();
		FunctionsLibrary.driver.quit();
		System.exit(0);
	}
}
