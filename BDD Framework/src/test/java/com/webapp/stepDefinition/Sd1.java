package com.webapp.stepDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.baseLibrary.VerificationFunctions;
import com.webapp.page.HomePage;
import com.webapp.utilities.GridReporter;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Sd1 {
	FunctionsLibrary functions = new FunctionsLibrary();
	HomePage homePage = new HomePage();
	public String tc_id = CommonStepDefinitions.tc_id;
	public String scenarioName = CommonStepDefinitions.scenarioName;
	VerificationFunctions verificationFunctions = new VerificationFunctions();
	public Map<String, String> testData = CommonStepDefinitions.testData;
	public String strReportFilename = "";
	static GridReporter reporter = CommonStepDefinitions.getReporter();
	WebDriverWait wait = new WebDriverWait(FunctionsLibrary.driver, 60);

	@And("^Verify \"([^\"]*)\" data in the home$")
	public void verify_data_in_home(String channel) {
		if (!CommonStepDefinitions.executeScenario) {
			return;
		}
	}
	
	@And("^User Logs in to the webpage with username and password$")
	public void user_logs_into_webpage_with_username_and_password() {
		functions.enterText("username", testData.get("username"));
		functions.enterText("password", testData.get("password"));
		functions.clickAnElement("login", "Login");
	}
	
	@Then("^Verify Valid user login$")
	public void verify_valid_user_login() {
		functions.verifyElementTextPresent("login_success", testData.get("success_msg"));
	}
	
	@Then("^Verify Invalid user login$")
	public void verify_invalid_user_login() {
		functions.verifyElementTextPresent("login_failure", testData.get("failure_msg"));
	}
}