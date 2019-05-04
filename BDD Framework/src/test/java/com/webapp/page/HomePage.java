package com.webapp.page;

import java.util.Map;

import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.baseLibrary.VerificationFunctions;
import com.webapp.executioner.ExecutionerClass;
import com.webapp.stepDefinition.CommonStepDefinitions;

import cucumber.api.java.en.And;

public class HomePage extends ExecutionerClass {

	/*
	 * public HomePage() { PageFactory.initElements(FunctionsLibrary.driver, this);
	 * }
	 */

	FunctionsLibrary functions = new FunctionsLibrary();
	VerificationFunctions verificationFunctions = new VerificationFunctions();
	public Map<String, String> testData = CommonStepDefinitions.testData;
	public String dataFileName = CommonStepDefinitions.dataFileName;
	public String tc_id = CommonStepDefinitions.tc_id;
	public String scenarioName = CommonStepDefinitions.scenarioName;
	public String strReportFilename = "";
	WebDriverWait wait = new WebDriverWait(FunctionsLibrary.driver, 60);

	public String getReportFileName() {
		return strReportFilename;
	}

	
}

