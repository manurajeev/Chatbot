package com.webapp.stepDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.executioner.ExecutionerClass;
import com.webapp.utilities.ComponentsLoader;
import com.webapp.utilities.GridReporter;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CommonStepDefinitions extends ExecutionerClass {

	public CommonStepDefinitions() {
		PageFactory.initElements(FunctionsLibrary.driver, this);
	}

	public static String releaseName = "";
	public static String testCycle = "";
	public static String dataFileName = "";
	public static String tc_id = "";
	public static String scenarioName = "";
	public static String moduleName = "";
	static GridReporter reporter = getReporter();
	public static String strReportFilename = "";
	public long executionStartTime;
	public String startTime;
	public static Map<String, String> testData = new HashMap<String, String>();
	public static Map<String, String> commonData = new HashMap<String, String>();
	public static int testCaseCount = 0;
	public static boolean executeScenario = false;
	public static int exceptioncounter=0;
	public static int iterationCount = 1;
	public static int numberOfRow = 0;

	public String getReportFileName() {
		return strReportFilename;
	}

	@Given("^: User loads the \"([^\"]*)\" json file$")
	public void getDataFileName(String fileName) throws Throwable {
		this.dataFileName = fileName;
	}

	public static String getDataFileName() {
		return dataFileName;
	}



	public String getReleaseNameUIInterface() {
		return ComponentsLoader.getReleaseName();
	}

	public static String getTestCaseIdScenarioName() {
		return tc_id + "&" + scenarioName;
	}

	public static String getReleaseName() {
		return releaseName;
	}

	FunctionsLibrary functions = new FunctionsLibrary();

	@When("^User provides the url$")
	public void launchApplication() throws Throwable {
		Properties config = new ExecutionerClass().setEnv();
		//String url = config.getProperty("URL");
		String url = config.getProperty("URL_" + functions.loadPropertiesFile("Config/Sys.properties").getProperty("Execution_Environment"));
		String browser = config.getProperty("browser");
		if(FunctionsLibrary.driver == null) {
			functions.initializeBrowser(browser);
		}

		functions.launchApplication(url);
	}

	@Given("^User Enter username and password and LogIn to application$")
	public void loginToApplication() throws Throwable {
		functions.loginApplication();
	}

	@Then("^User is on the home page of the application$")
	public void homePage() throws Throwable {
		functions.waitForPageLoad();
		functions.globalWait(1);
		if (functions.verifyElementPresent("homeTab")) {
			reporter.writeStepResult(tc_id, scenarioName, "user click on home page tab","Home page tab should be Available", "Home page tab is Available", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "user click on home page tab","Home page tab should be Available", "Home page tab is not Available", "Fail", strReportFilename);
		}
	}

	@After
	public void afterHook() throws Exception {
		String strStopTime = reporter.stop();
		GridReporter.strStopTime = strStopTime;
		float timeElapsed = reporter.getElapsedTime();
		System.out.println("==================================== Execution completed for TC : "+tc_id+" ===================");
		String status = reporter.addDynamicContents(tc_id, scenarioName, releaseName,
				strReportFilename, timeElapsed);
	}

	public void printIteration() {
		if (iterationCount > 0) {
			reporter.writeStepResult(tc_id, scenarioName, "--------------------------------------Row : "
					+ iterationCount + " Execution Starts here -----------------------------", "", "", "",
					strReportFilename);
		}
	}

	public static void printEndOfIteration() {
		if (iterationCount > 0) {
			reporter.writeStepResult(tc_id, scenarioName, "--------------------------------------Row : "
					+ iterationCount + " Execution Ends here -------------------------------", "", "", "",
					strReportFilename);
		}
	}


	/**
	 * parse the json input
	 */
	public String getReleaseName(String filename) {
		String value = "";
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("Data/" + filename + ".json"));

			JSONObject lev1 = (JSONObject) obj;
			Object jObj = lev1.get("ReleaseDetails");

			if (jObj instanceof Map) {
				HashMap<String, ArrayList<String>> map1 = (HashMap<String, ArrayList<String>>) jObj;
				for (Entry<String, ArrayList<String>> entry : map1.entrySet()) {
					for (int i = 0; i < entry.getValue().size(); i++) {
						value = entry.getValue().get(i);
					}
				}
				System.out.println(" " + value);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	@Given("^User executes test cases \"([^\"]*)\" for '(.*)'$")
	public void getMultipleTestcaseIdScenarioName1(String tc_id, String scenarioNames) throws Throwable {
		System.out.println("==================================== Execution started for TC : "+tc_id+" ===================");
		this.tc_id = tc_id;
		this.scenarioName = scenarioNames.replaceAll(" ", "_");
		this.releaseName = getReleaseNameUIInterface();
		// this.releaseName=getReleaseName(dataFileName);
		// Loading JSON Data
		CommonStepDefinitions.commonData = functions.parse("Common_Data", dataFileName);
		System.out.println(CommonStepDefinitions.commonData);
		CommonStepDefinitions.testData = functions.parse(tc_id, dataFileName);
		for (String common : CommonStepDefinitions.testData.keySet()) {
			CommonStepDefinitions.commonData.put(common, CommonStepDefinitions.testData.get(common));
		}
		System.out.println();
		CommonStepDefinitions.testData = CommonStepDefinitions.commonData;
		System.out.println(CommonStepDefinitions.testData);

		executionStartTime = reporter.start();
		strReportFilename = reporter.reportFilestart(currentDateTime(), tc_id, scenarioName);
	}

	@Given("^User executes test case '(.*)' for '(.*)'$")
	public void getMultipleTestcaseIdScenarioName2(String tc_id, String scenarioNames) throws Throwable {
		System.out.println("==================================== Execution started for TC : "+tc_id+" ===================");
		this.tc_id = tc_id;
		this.scenarioName = scenarioNames.replaceAll(" ", "_");
		this.releaseName = getReleaseNameUIInterface();
		// this.releaseName=getReleaseName(dataFileName);
		// Loading JSON Data
		CommonStepDefinitions.commonData = functions.parse("Common_Data", dataFileName);
		System.out.println(CommonStepDefinitions.commonData);
		CommonStepDefinitions.testData = functions.parse(tc_id, dataFileName);
		for (String common : CommonStepDefinitions.testData.keySet()) {
			CommonStepDefinitions.commonData.put(common, CommonStepDefinitions.testData.get(common));
		}
		System.out.println();
		CommonStepDefinitions.testData = CommonStepDefinitions.commonData;
		System.out.println(CommonStepDefinitions.testData);

		executionStartTime = reporter.start();
		strReportFilename = reporter.reportFilestart(currentDateTime(), tc_id, scenarioName);
	}
}
