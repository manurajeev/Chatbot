package com.webapp.baseLibrary;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.webapp.stepDefinition.CommonStepDefinitions;
import com.webapp.utilities.GridReporter;

public class VerificationFunctions {
	public String tc_id = CommonStepDefinitions.tc_id;
	public String scenarioName = CommonStepDefinitions.scenarioName;
	public String dataFileName = CommonStepDefinitions.dataFileName;
	public String strReportFilename = "";
	static GridReporter reporter = CommonStepDefinitions.getReporter();
	public static Map<String, String> testData = CommonStepDefinitions.testData;
	FunctionsLibrary functions = new FunctionsLibrary();

	public String getReportFileName() {
		return strReportFilename;
	}

	// Verify the Radio Button is selected or not
	public void verifyRadioButtonStatus(WebElement element, String expectedStatus) {
		boolean status = false;
		try {
			status = element.isSelected();
			if (expectedStatus.equalsIgnoreCase("selected")) {
				if (status) {
					reporter.writeStepResult(tc_id, scenarioName, "Verify element is selected",
							"Expected: " + expectedStatus, "Element is selected", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult(tc_id, scenarioName, "Verify element is selected",
							"Expected: " + expectedStatus, "Element is not selected", "Fail", strReportFilename);
				}
			}
			if (expectedStatus.equalsIgnoreCase("deselected")) {
				if (!status) {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Element is not selected",
							"Expected: " + expectedStatus, "Element is not selected", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Element is not selected",
							"Expected: " + expectedStatus, "Element is selected", "Fail", strReportFilename);
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present", "Expected: " + expectedStatus,
					"Expected element  is not present (Actual: Exception occurred )", "Fail", strReportFilename);
		}
	}

	// Verify Element attribute value for a text field
	public void verifyTextPresent(String locatorKey, String expectedText) throws InterruptedException {

		WebElement element = functions.getWebElementWithWait(locatorKey);
		String strActualText = null;
		try {
			strActualText = element.getAttribute("value");
			if (strActualText.equals(expectedText)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify value in the text field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify value in the text field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Fail", strReportFilename);
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify value in the text field", "Expected: " + expectedText,
					"Expected value is not present (Actual: Exception occurred)", "Fail", strReportFilename);
		}
	}

	// Verify Element attribute value for a text field
	public void verifyTextNotPresent(WebElement element, String expectedText) {
		String strActualText = null;
		try {
			strActualText = element.getAttribute("value");
			if (strActualText.equals(expectedText)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify value is not present in the text field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Fail", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify value is not present in the text field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Pass", strReportFilename);
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify value is not present in the text field",
					"Expected: " + expectedText, "Expected value is not present (Actual: Exception occurred)", "Fail",
					strReportFilename);
		}
	}

	// Verify Element attribute value for a field
	public void verifyElementAttributeValue(WebElement element, String expectedText, String attributeValue) {
		String strActualText = null;
		try {
			strActualText = element.getAttribute(attributeValue);
			if (strActualText.equals(expectedText)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify attribute value of a field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify attribute value of a field",
						"Expected: " + expectedText, "Actual : " + strActualText, "Fail", strReportFilename);
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify attribute value of a field",
					"Expected: " + expectedText, "Expected value is not present (Actual: Exception occurred)", "Fail",
					strReportFilename);
		}
	}

	// verify array list values
	public void verifyListValues(WebElement element, String expectedValues) {
		int counter = 0;
		boolean exists = false;
		String[] arrListValues = expectedValues.split(";");
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			for (int i = 0; i < arrListValues.length; i++) {
				for (WebElement option : options) {
					System.out.println("" + option.getText());
					if (option.getText().equals(arrListValues[i])
							|| option.getAttribute("Value").equals(arrListValues[i])) {
						counter++;
						exists = true;
						break;
					}
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues,
					"Expected values are Not present in the listbox (Actual: Exception occurred )", "Fail",
					strReportFilename);
		}

		if (counter == arrListValues.length) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues, "Expected values are present in the listbox", "Pass",
					strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues,
					"Expected values are Not present in the listbox" + ("Actual" + element), "Fail", strReportFilename);
		}
	}

	// verify array list values not present
	public void verifyListValuesNotPresent(WebElement element, String expectedValues) {
		int counter = 0;
		boolean notExists = false;
		String[] arrListValues = expectedValues.split(";");
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			for (int i = 0; i < arrListValues.length; i++) {
				for (WebElement option : options) {
					System.out.println("" + option.getText());
					if (!(option.getText().equals(arrListValues[i])
							|| option.getAttribute("Value").equals(arrListValues[i]))) {
						notExists = true;
						break;
					}
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues,
					"Expected values are Not present in the listbox (Actual: Exception occurred )", "Fail",
					strReportFilename);
		}

		if (notExists) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues, "Expected values are Not present in the listbox", "Pass",
					strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify values are present in the listbox",
					"Expected: " + expectedValues, "Expected values are present in the listbox" + ("Actual" + element),
					"Fail", strReportFilename);
		}
	}

	// verify array list value not present
	public void verifyListValueNotPresent(WebElement element, String expectedValue) {
		boolean exists = false;
		boolean isPresent = false;
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(expectedValue) || option.getAttribute("Value").equals(expectedValue)) {
					isPresent = true;
					break;
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify expected value is absent in listbox",
					"Expected: " + expectedValue,
					"Expected values are Not present in the listbox (Actual: Exception occurred )", "Fail",
					strReportFilename);
		}

		if (isPresent) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify expected value is absent in listbox",
					"Expected: " + expectedValue, "Expected value is absent in the listbox", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify expected value is absent in listbox",
					"Expected: " + expectedValue, "Expected value is present in the listbox" + ("Actual" + element),
					"Fail", strReportFilename);
		}
	}

	// verify array list box is empty
	public void verifyListBoxIsEmpty(WebElement element) {
		boolean isEmpty = false;
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			if (options.size() == 0) {
				isEmpty = true;
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify listbox is empty", "Expected: " + "No options",
					"Element is not present", "Fail", strReportFilename);
		}

		if (isEmpty) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify listbox is empty", "Expected: " + "No options",
					"List box is empty", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify listbox is empty", "Expected: " + "No options",
					"List box is not empty", "Fail", strReportFilename);
		}
	}

	// verify selected list value
	public void verifySelectedListValue(WebElement element, String expectedValue) {
		boolean isEmpty = false;
		boolean isSelected = false;
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(expectedValue) || option.getAttribute("Value").equals(expectedValue)) {
					if (option.isSelected()) {
						isSelected = true;
						break;
					}
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify selected value in the listbox",
					"Expected: " + expectedValue, "Element is not present", "Fail", strReportFilename);
		}

		if (isSelected) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify selected value in the listbox",
					"Expected: " + expectedValue, "Expected value is selected in the listbox", "Pass",
					strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify selected value in the listbox",
					"Expected: " + expectedValue, "Expected value is not selected in the listbox", "Fail",
					strReportFilename);
		}
	}

	// Verify element is present
	public void verifyElementPresentDropDown(String locatorKey) throws InterruptedException {
		boolean exists = false;
		WebElement element = functions.getWebElementWithWait(locatorKey);
		try {
			List<WebElement> options = element.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (element.isDisplayed()) {
					exists = true;
					break;
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be available", "Element is not present", "Fail", strReportFilename);
		}

		if (exists) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is present", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is not present", "Fail", strReportFilename);
		}
	}

	// Verify Element is present
	public void verifyElementPresent(String locatorKey) throws InterruptedException {
		boolean exists = false;
		WebElement element = functions.getWebElement(locatorKey);
		try {
			if (element.isDisplayed()) {
				exists = true;
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be available", "Element is not present", "Fail", strReportFilename);
		}

		if (exists) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is present", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is not present", "Fail", strReportFilename);
		}
	}

	public void verifyElementPresent(WebElement element) throws InterruptedException {
		boolean exists = false;
		// WebElement element = functions.getWebElement(locatorKey);
		try {
			if (element.isDisplayed()) {
				exists = true;
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be available", "Element is not present", "Fail", strReportFilename);
		}

		if (exists) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is present", "Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element is present",
					"Expected: " + "Element should be present", "Element is not present", "Fail", strReportFilename);
		}
	}

	// Verify element Text is present
	public void verifyElementTextPresent(String locatorKey, String expectedText) {

		WebElement element = functions.getWebElementWithWait(locatorKey);
		boolean exists = false;
		String actualText = null;
		try {
			actualText = element.getText();
			if (actualText.equalsIgnoreCase(expectedText)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify element text", "Expected: " + expectedText,
						"Actual Text: " + actualText, "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify element text", "Expected: " + expectedText,
						"Actual Text: " + actualText, "Fail", strReportFilename);
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify element text",
					"Expected: " + "Expected text should be available", "Element is not present", "Fail",
					strReportFilename);
		}
	}

	// Verify element Text is present using WebElement
	public void verifyElementTextPresent(WebElement element, String expectedText) throws InterruptedException {

		// WebElement element = functions.getWebElementWithWait(locatorKey);
		boolean exists = false;
		String actualText = null;
		try {
			actualText = element.getText();
			if (actualText.equalsIgnoreCase(expectedText)) {
				reporter.writeStepResult(tc_id, scenarioName, "Verify element text", "Expected: " + expectedText,
						"Expected text is present", "Pass", strReportFilename);
			} else {
				reporter.writeStepResult(tc_id, scenarioName, "Verify element text", "Expected: " + expectedText,
						"Expected text is not present", "Fail", strReportFilename);
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify element text",
					"Expected: " + "Expected text should be available", "Element is not present", "Fail",
					strReportFilename);
		}

	}

	// Verify checkbox status
	public void verifyCheckboxStatus(WebElement element, String expectedStatus) {
		boolean status = false;
		try {
			status = element.isSelected();
			if (expectedStatus.equalsIgnoreCase("checked")) {
				if (status) {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Checkbox is checked",
							"Expected: " + expectedStatus, "Checkbox is checked", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Checkbox is checked",
							"Expected: " + expectedStatus, "Checkbox is not checked", "Fail", strReportFilename);
				}
			}
			if (expectedStatus.equalsIgnoreCase("unchecked")) {
				if (!status) {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Checkbox is not checked",
							"Expected: " + expectedStatus, "Checkbox is not checked", "Pass", strReportFilename);
				} else {
					reporter.writeStepResult(tc_id, scenarioName, "Verify Checkbox is not checked",
							"Expected: " + expectedStatus, "Checkbox is checked", "Fail", strReportFilename);
				}
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify element is present", "Expected: " + expectedStatus,
					"Expected element  is not present (Actual: Exception occurred )", "Fail", strReportFilename);
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void verifyElementPresent(WebElement element, String strLabel) throws InterruptedException {
		boolean exists = false;
		// WebElement element = functions.getWebElement(locatorKey);
		try {
			if (element.isDisplayed()) {
				exists = true;
			}
		} catch (Exception e1) {
			System.out.println("Exception occurred -- " + e1.getMessage());
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element " + strLabel + " is present",
					"Expected: " + strLabel + "Element should be available", "Element " + strLabel + " is not present",
					"Fail", strReportFilename);
		}

		if (exists) {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element " + strLabel + " is present",
					"Expected: " + strLabel + " Element should be present", "Element " + strLabel + " is present",
					"Pass", strReportFilename);
		} else {
			reporter.writeStepResult(tc_id, scenarioName, "Verify Element " + strLabel + " is present",
					"Expected: " + strLabel + " Element should be present", "Element " + strLabel + " is not present",
					"Fail", strReportFilename);
		}
	}
}
