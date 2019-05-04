package com.webapp.utilities;

import java.awt.AWTException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;


import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.stepDefinition.CommonStepDefinitions;

public class HTML {
	public static String g_FileName = ""; // 'Report Log File Name.
	public static String g_sFileName = "";
	public static String g_iCapture_Count = "";// 'Number of Image capture
	public static int g_iImage_Capture;// =""; //'Flag for Image Capture in Result File
	public static int g_iPass_Count = 0; // 'Pass Count
	public static int g_iFail_Count;// =0; //'Fail Count
	public static Date g_tStart_Time; // 'Start Time
	public static Date g_tEnd_Time; // 'End Time
	public static String g_sScreenName = ""; // 'Screen shot name
	public static int g_Total_TC;// =0;
	public static int g_Total_Pass;// =0;
	public static int g_Total_Fail;// =0;
	public static int g_Flag;// =0;
	public static int g_Flag1;// =0;
	public static Date g_tSummaryStart_Time;// 'Start Time
	public static Date g_tSummaryEnd_Time; // 'End Time
	public static Date g_tSummaryTCStart_Time; // 'Start time for each test case in Summary Report
	public static Date g_tSummaryTCEnd_Time; // 'Start time for each test case in Summary Report
	public static int g_SummaryTotal_TC;// =0;
	public static int g_SummaryTotal_Pass;// =0;
	public static int g_SummaryTotal_Fail;// =0;
	public static int g_SummaryFlag = 0;
	public static String g_sSummaryFileName = "";
	public static String g_ScriptName = "";
	public static String g_sSection = "";
	public static int hour;
	public static int minute;
	public static int second;
	public static LinkedHashSet<String> storeDetailReport = new LinkedHashSet<String>();
	public static int counter = 0;
	public static File file;
	public static FileInputStream fileInput;
	public static Properties properties = new Properties();
	public static String detailReport;
	public static String filterreport = new File("").getAbsolutePath() + "//config//filterreport";

	//////////////////////////// Summary Initialization
	//////////////////////////// Start//////////////////////////////////////////////////////////
	public static void fnSummaryInitialization(String strSummaryReportName) throws IOException {

		String sConfigfilespath = "Config";

		file = new File(sConfigfilespath + "\\Sys.properties");

		FileInputStream fis = new FileInputStream(file);
		properties.load(fis);

		// FileOutputStream fos = new FileOutputStream(
		// "D:\\workspace\\CSPCode\\Config\\Sys.properties");
		FileOutputStream fos = new FileOutputStream("Config\\Sys.properties");

		File resultsFolder = new File("results");
		if (!resultsFolder.exists()) {
			resultsFolder.mkdir();
		}
		// properties.setProperty("TempResultPath",properties.getProperty("ResultsFolderPath"));
		properties.setProperty("TempResults", resultsFolder.getAbsolutePath());
		properties.setProperty("SummaryFileName", "");
		properties.setProperty("SummaryFolderName", resultsFolder.getAbsolutePath());

		properties.store(fos, null);
		fos.close();

		File objDir = new File(properties.getProperty("SummaryFolderName"));
		if (!objDir.exists()) {
			objDir.mkdir();
		}

		properties.setProperty("SummaryFolderName1", resultsFolder.getAbsolutePath() + "\\" + strSummaryReportName);
		File objDir1 = new File(properties.getProperty("TempResults"));
		if (!objDir1.exists()) {
			objDir1.mkdir();
		}
		fnSummaryOpenHtmlFile(strSummaryReportName);// 'logo, heading
		fnSummaryInsertSection(); // 'TestCaseID,Scenario Name and Result

		// fnSummaryInsertHeader();

		fis.close();
	}

	@SuppressWarnings("deprecation")
	public static void fnSummaryOpenHtmlFile(String sSection) throws IOException {
		g_iPass_Count = 0;
		g_iFail_Count = 0;
		g_sFileName = "sScriptName";
		// g_iImage_Capture = 1

		g_SummaryTotal_TC = 0;
		g_SummaryTotal_Pass = 0;
		g_SummaryTotal_Fail = 0;
		g_SummaryFlag = 0;
		g_ScriptName = "sScriptName";
		hour = 0;
		minute = 0;
		second = 0;

		// String sConfigfilespath = "D:\\workspace\\CSPCode\\Config";
		String sConfigfilespath = "Config";
		file = new File(sConfigfilespath + "\\Sys.properties");

		/*
		 * FileInputStream fis = new FileInputStream( file); properties.load(fis);
		 * fis.close();
		 */
		// FileOutputStream fos = new FileOutputStream(
		// "D:\\workspace\\CSPCode\\Config\\Sys.properties");
		FileOutputStream fos = new FileOutputStream("Config\\Sys.properties");

		Date d = new Date();
		String gsTempFile = properties.getProperty("SummaryFileName");
		// String ModuleName= properties.getProperty("ModuleName");
		if ((gsTempFile == null) || (gsTempFile == "")) {
			gsTempFile = properties.getProperty("SummaryFolderName1") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
			properties.setProperty("SummaryFileName", gsTempFile);
		}
		properties.store(fos, null);
		fos.close();
		// Path objPath = Paths.get(gsTempFile);
		// String ResSummary = properties.getProperty("ResultsHeader");
		FileWriter objFile = new FileWriter(gsTempFile, true);
		objFile.write("<HTML><BODY><TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
		objFile.write(
				"<TR COLS=2><TD BGCOLOR=WHITE WIDTH=6%><IMG SRC=''></TD><TD WIDTH=94% BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=NAVY SIZE=3><B>&nbsp;"
						+ properties.getProperty("ResultsHeader")
						+ "<BR/><FONT FACE=VERDANA COLOR=SILVER SIZE=2>&nbsp;Date: " + d
						+ "</BR>&nbsp;</B></FONT></TD><TD BGCOLOR=WHITE WIDTH=6%><IMG SRC='https://www.capgemini.com/wp-content/themes/rebranding/assets/images/logo.svg'></TD></TR></TABLE>");
		objFile.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
		objFile.write("<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Title : " + sSection
				+ "</B></FONT></TD></TR>");
		objFile.write("</TABLE></BODY></HTML>");
		objFile.close();

		g_tSummaryStart_Time = d;
		g_tSummaryTCStart_Time = d;
		g_sSection = sSection;
		// fis.close();
	}

	@SuppressWarnings("deprecation")
	public static void fnSummaryInsertSection() throws IOException {
		Date d = new Date();
		String gsTempFile = properties.getProperty("SummaryFileName");
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("SummaryFolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}
		// Path objPath=Paths.get(gsTempFile);
		FileWriter objFile = new FileWriter(gsTempFile, true);
		objFile.write(
				"<HTML><BODY><script language='javascript' type='text/javascript'>setFilterGrid('table2');</script><TABLE id='table2' BORDER=1 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
		objFile.write(
				"<THEAD><TR><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Test Case Name</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Scenario Name</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Release Name</B></FONT></TH><TH BGCOLOR=#FFCC99 SIZE=2 WIDTH=15%><B>Execution Time</B></FONT></TH><TH BGCOLOR=#FFCC99 SIZE=2 WIDTH=10%><select id='filterText' style='display:inline-block' onchange='filterText()'><option BGCOLOR=#FFCC99 SIZE=0 disabled selected>Select</option><option value='Pass'>PASS</option><option value='FAIL'>FAIL</option><option value='ALL'>All</option></select><B>Result</B></FONT></TH></TR></THEAD>");
		objFile.close();
	}
	//////////////////////////// Summary Initialization
	//////////////////////////// End/////////////////////////////////////////////////////////////

	private static String getComputerName() {
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME"))
			return env.get("COMPUTERNAME");
		else if (env.containsKey("HOSTNAME"))
			return env.get("HOSTNAME");
		else
			return "Unknown Computer";
	}

	//////////////////////////// Test Case Initialization
	//////////////////////////// Start/////////////////////////////////////////////////////////
	public static void fnInitilization(String className, String scenarioName, String strExecutionStartTime)
			throws IOException, AWTException {
		// File directory = new File (".");
		// String sConfigfilespath = directory.getCanonicalPath()+"\\Config";
		// String sConfigfilespath = "D:\\workspace\\CSPCode\\Config";
		String sConfigfilespath = "Config";
		File file = new File(sConfigfilespath + "\\Sys.properties");
		FileInputStream fis = new FileInputStream(file);
		properties.load(fis);
		fis.close();

		FileOutputStream fos = new FileOutputStream(file);

		// properties.setProperty("TempResultPath",properties.getProperty("ResultsFolderPath")
		// + "\\temp");
		// properties.setProperty("TempResults",properties.getProperty("TempResultPath"));
		File resultsFolder = new File("results");
		if (!resultsFolder.exists()) {
			resultsFolder.mkdir();
		}
		properties.setProperty("FileName", "");
		properties.setProperty("FolderName", resultsFolder.getAbsolutePath() + "\\" + CommonStepDefinitions.tc_id);

		File objDir = new File(properties.getProperty("FolderName"));
		if (!objDir.exists()) {
			objDir.mkdir();
		}
		// properties.setProperty("FolderName1",properties.getProperty("ResultsFolderPath")
		// + "\\" + className + "\\" +scenarioName );
		properties.store(fos, null);
		fos.close();
		fnOpenHtmlFile(strExecutionStartTime, className, scenarioName);
		fnInsertSection();
		fnInsertTestCaseName("");

	}

	@SuppressWarnings("deprecation")
	public static void fnOpenHtmlFile(String strExecutionStartTime, String className, String scenarioName)
			throws IOException, AWTException {
		g_iPass_Count = 0;
		g_iFail_Count = 0;
		g_sFileName = "sScriptName";
		g_iImage_Capture = 1;
		g_Total_TC = 0;
		g_Total_Pass = 0;
		g_Total_Fail = 0;
		g_Flag = 0;
		g_Flag1 = 0;
		g_ScriptName = "sScriptName";

		// String sConfigfilespath = "D:\\workspace\\CSPCode\\Config";
		String sConfigfilespath = "Config";
		File file = new File(sConfigfilespath + "\\Sys.properties");
		FileOutputStream fos = new FileOutputStream(file);

		String resultsFolderPath = "results/" + className;

		File resultFolderPaths = new File(resultsFolderPath);
		if (!resultFolderPaths.exists()) {
			resultFolderPaths.mkdir();
		}
		System.out.println();
		Date d = new Date();
		String videoRecordFile = "";
		String gsTempFile = "";
		String TCNo = className;
		String TCModule = scenarioName;

		if (gsTempFile == "") {
			gsTempFile = resultsFolderPath + "/" + scenarioName + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";

			videoRecordFile = scenarioName + d.getYear() + d.getMonth() + d.getDay() + "_" + d.getHours()
					+ d.getMinutes() + d.getSeconds();
			properties.setProperty("FileName", gsTempFile);
		}

		properties.store(fos, null);
		File tempFile = new File(gsTempFile);
		fos.close();
		if (!tempFile.exists()) {
			tempFile.createNewFile();
		}
		Path objPath = Paths.get(gsTempFile);
		if (Files.exists(objPath)) {
			FileWriter objFile = new FileWriter(gsTempFile, true);
			objFile.write("<HTML><BODY><TABLE BORDER=0 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			objFile.write(
					"<TR COLS=2><TD BGCOLOR=WHITE WIDTH=6%><IMG SRC='https://az846835.vo.msecnd.net/company/logos/GlobalPayments.png'></TD><TD WIDTH=94% BGCOLOR=WHITE><FONT FACE=VERDANA COLOR=NAVY SIZE=3><B>Heartland WebTops BDD Automation Result<BR/><FONT FACE=VERDANA COLOR=SILVER SIZE=2>Execution Start Time: "
							+ strExecutionStartTime + "</BR>On Machine :" + getComputerName()
							+ "</B></FONT></TD><TD BGCOLOR=WHITE WIDTH=6%><IMG  SRC='https://www.capgemini.com/sites/all/themes/capgemini/logo.png'></TD></TR></TABLE>");
			objFile.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			objFile.write("<TR><TD BGCOLOR=#66699 WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Test Case : "
					+ TCNo
					+ "</B></FONT></TD><TD BGCOLOR=#66699 WIDTH=75%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Module Name : "
					+ TCModule + "</B></FONT></TD></TR>");
			objFile.write("</TABLE></BODY></HTML>");
			objFile.close();
		}
		// ScreenVideoCapture.startVideoCapture();
		g_tStart_Time = d;
		resultsFolderPath = className;

	}

	@SuppressWarnings("deprecation")
	public static void fnInsertSection() throws IOException {
		Date d = new Date();
		String gsTempFile = properties.getProperty("FileName");
		Path objPath = Paths.get(gsTempFile);
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("FolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}

		if (Files.exists(objPath)) {
			FileWriter objFile = new FileWriter(gsTempFile, true);
			objFile.write(
					"<HTML><BODY><script language='javascript' type='text/javascript'>setFilterGrid('table2');</script><TABLE id='table2' BORDER=1 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			objFile.write(
					"<thead><TR><TH BGCOLOR=#FFCC99 WIDTH=5%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>S. No.</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=20%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Component</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=35%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Expected Value</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=35%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Actual Value</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=10%><select id='filterText' style='display:inline-block' onchange='filterText()'><option BGCOLOR=#FFCC99 SIZE=0 disabled selected>Select</option><option value='Pass'>PASS</option><option value='FAil'>FAil</option><option value='ALL'>All</option></select><B>Result</B></FONT></th><B>Result</B></FONT></TH></TR></thead>");
			objFile.close();
		}
	}

	@SuppressWarnings("deprecation")
	public static void fnInsertTestCaseName(String sDesc) {
		Date d = new Date();
		String gsTempFile = properties.getProperty("FileName");
		// Path objPath=Paths.get(gsTempFile);
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("FolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}
		g_Total_TC = g_Total_TC + 1;
		if (g_Flag1 != 0) {
			if (g_Flag == 0) {
				g_Total_Pass = g_Total_Pass + 1;
			} else {
				g_Total_Fail = g_Total_Fail + 1;
			}
		}
		g_Flag = 0;
	}
	//////////////////////////// Test Case Initialization
	//////////////////////////// End/////////////////////////////////////////////////////////

	//////////////////////////// Write Results
	//////////////////////////// Start//////////////////////////////////////////////////////////////////
	@SuppressWarnings("deprecation")
	public static void fnInsertResult(String sTestCaseName, String scenarioName, String sDesc, String sExpected,
			String sActual, String sResult, String strResultPath) throws IOException {
		// Common c = new Common();
		// driver= c.dr();
		g_Flag1 = 1;
		Date d = new Date();
		String gsTempFile = properties.getProperty("FileName");
		Path objPath = Paths.get(gsTempFile);
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("FolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}
		detailReport = gsTempFile;
		storeDetailReport.add(gsTempFile);

		if (Files.exists(objPath)) {
			FileWriter objFile = new FileWriter(gsTempFile, true);

			if (sResult.toUpperCase().equalsIgnoreCase("PASS")) {
				g_iPass_Count = g_iPass_Count + 1;
				if (properties.getProperty("CaptureScreenShotforPass").equalsIgnoreCase("YES")) {
					String I_sFile = "";
					g_iCapture_Count = "Screen" + d.getHours() + d.getMinutes() + d.getSeconds();
					I_sFile = properties.getProperty("FolderName") + "\\screenshot\\" + g_iCapture_Count + ".png";
					if (FunctionsLibrary.driver != null) {
						File scrFile = ((TakesScreenshot) FunctionsLibrary.driver).getScreenshotAs(OutputType.FILE);
						FileUtils.copyFile(scrFile, new File(I_sFile));

					}

					// objFile.write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA
					// SIZE=2>" + sTestCaseName +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT
					// FACE=VERDANA SIZE=2>" + sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE
					// WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sExpected +"</FONT></TD><TD
					// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sActual +
					// "</A></FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2'
					// SIZE=5 COLOR=GREEN>P</FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B><A
					// HREF='" + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>");
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B><A HREF='"
							+ I_sFile + "'><Status>" + sResult
							+ "</Status></A></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");

				} else {
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B><Status>"
							+ sResult
							+ "</Status></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");
				}
			} else if (sResult.toUpperCase().equalsIgnoreCase("FAIL")) {
				g_Flag = 1;
				g_SummaryFlag = 1;
				g_iFail_Count = g_iFail_Count + 1;
				if (properties.getProperty("CaptureScreenShotforFail").equalsIgnoreCase("YES")) {
					String I_sFile = "";
					g_iCapture_Count = "Screen" + d.getHours() + d.getMinutes() + d.getSeconds();
					I_sFile = properties.getProperty("FolderName") + "\\Screen\\" + g_iCapture_Count + ".png";
					if (FunctionsLibrary.driver != null) {
						File scrFile = ((TakesScreenshot) FunctionsLibrary.driver).getScreenshotAs(OutputType.FILE);
						FileUtils.copyFile(scrFile, new File(I_sFile));
					}
					// objFile.write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA
					// SIZE=2>" + sTestCaseName +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT
					// FACE=VERDANA SIZE=2>" + sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE
					// WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sExpected +"</FONT></TD><TD
					// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=WINGDINGS SIZE=4></FONT><FONT
					// FACE=VERDANA SIZE=2>" + sActual +"</FONT></TD><TD BGCOLOR=#EEEEEE
					// WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED>O</FONT><FONT
					// FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='" + I_sFile +"'>" + sResult +
					// "</A></B></FONT></TD></TR>");
					// objFile.write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA
					// SIZE=2>" + sTestCaseName +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT
					// FACE=VERDANA SIZE=2>" + sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE
					// WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sExpected +"</FONT></TD><TD
					// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sActual
					// +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5
					// COLOR=RED>O</FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='" + I_sFile
					// +"'>" + sResult + "</A></B></FONT></TD></TR>");
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
							+ I_sFile + "'style=color:#ff0000><Status>" + sResult
							+ "</Status></A></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");
				} else {
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><Status>"
							+ sResult
							+ "</Status></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");
				}
			} else if (sResult.equalsIgnoreCase("NOT COMPLETED")) {
				if (properties.getProperty("CaptureScreenShotforWarning").equalsIgnoreCase("YES")) {
					String I_sFile = "";
					g_iCapture_Count = "Screen" + d.getHours() + d.getMinutes() + d.getSeconds();
					I_sFile = properties.getProperty("FolderName") + "\\Screen\\" + g_iCapture_Count + ".jpeg";
					if (FunctionsLibrary.driver != null) {
						File scrFile = ((TakesScreenshot) FunctionsLibrary.driver).getScreenshotAs(OutputType.FILE);
						FileUtils.copyFile(scrFile, new File(I_sFile));
					}
					// objFile.write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA
					// SIZE=2>" + sTestCaseName +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT
					// FACE=VERDANA SIZE=2>" + sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE
					// WIDTH=30%><FONT FACE=VERDANA SIZE=2>" + sExpected +"</FONT></TD><TD
					// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=WINGDINGS SIZE=4>2></FONT><FONT
					// FACE=VERDANA SIZE=2><A HREF='" + I_sFile +"'>" + sActual +
					// "</A></FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2'
					// SIZE=5 COLOR=RED>O</FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B>" + sResult +
					// "</B></FONT></TD></TR>");
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
							+ I_sFile + "'><Status>" + sResult
							+ "</Status></A></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");
				} else {
					objFile.write("<TR class='content' COLS=5><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE=VERDANA SIZE=2>"
							+ counter + "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=20%><FONT FACE=VERDANA SIZE=2>" + sDesc
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sExpected
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=35%><FONT FACE=VERDANA SIZE=2>" + sActual
							+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=5%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><Status>"
							+ sResult
							+ "</Status></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
							+ filterreport + ".js'></script>");
				}
			} else {
				// if(sExpected !="Component should start execution")
				// if(sExpected !="")
				{
					objFile.write(
							"<TR class='content' COLS=5><TD BGCOLOR=#FAAC58; colspan='5';text-align: justify;word-wrap: break-word: break-all;white-space: pre-line;><FONT FACE=VERDANA SIZE=2>"
									+ "" + "</FONT><FONT FACE=VERDANA SIZE=2>" + sDesc
									+ "</FONT><FONT FACE=VERDANA SIZE=2>" + sExpected
									+ "</FONT><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 ><B><Status>"
									+ sResult
									+ "</Status></B></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
									+ filterreport + ".js'></script>");
				}

			}
			objFile.close();
		}
		counter = counter + 1;
	}

	/*
	 * @SuppressWarnings("deprecation") public static void fnInsertStepResult(String
	 * sTestCaseName,String scenarioName,String releaseName, String sDesc, String
	 * sExpected, String sActual, String sResult, String strResultPath) throws
	 * IOException { //Common c = new Common(); //driver= c.dr(); g_Flag1=1; Date d
	 * = new Date(); String gsTempFile = properties.getProperty("FileName"); Path
	 * objPath=Paths.get(gsTempFile); if(gsTempFile =="") { gsTempFile =
	 * properties.getProperty("FolderName")+d.getYear() + d.getMonth() + d.getDay()
	 * + "_" + d.getHours() + d.getMinutes() + d.getSeconds()+ ".htm"; }
	 * 
	 * storeDetailReport.add(gsTempFile);
	 * 
	 * if(Files.exists(objPath)) { FileWriter objFile=new
	 * FileWriter(gsTempFile,true);
	 * 
	 * if(sResult.toUpperCase().equalsIgnoreCase("PASS")) { g_iPass_Count =
	 * g_iPass_Count + 1; if
	 * (properties.getProperty("CaptureScreenShotforPass").equalsIgnoreCase("YES"))
	 * { String I_sFile=""; g_iCapture_Count="Screen" + d.getHours() +d.getMinutes()
	 * + d.getSeconds(); I_sFile = properties.getProperty("FolderName") +
	 * "\\screenshot\\" + g_iCapture_Count + ".png"; if(FunctionsLibrary.driver !=
	 * null) { File
	 * scrFile=((TakesScreenshot)FunctionsLibrary.driver).getScreenshotAs(OutputType
	 * .FILE); FileUtils.copyFile(scrFile, new File(I_sFile));
	 * 
	 * }
	 * 
	 * //objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>"
	 * + sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</A></FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=GREEN>P</FONT><FONT FACE=VERDANA SIZE=2  COLOR=GREEN><B><A HREF='"
	 * + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>"); objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=100%><FONT FACE=VERDANA SIZE=2>"
	 * + sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B><A HREF='"
	 * + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>"); } else { objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><B>"
	 * + sResult + "</B></FONT></TD></TR>"); } } else
	 * if(sResult.toUpperCase().equalsIgnoreCase("FAIL")) { g_Flag = 1;
	 * g_SummaryFlag = 1; g_iFail_Count = g_iFail_Count + 1; if
	 * (properties.getProperty("CaptureScreenShotforFail").equalsIgnoreCase("YES"))
	 * { String I_sFile=""; g_iCapture_Count="Screen" + d.getHours() +d.getMinutes()
	 * + d.getSeconds(); I_sFile = properties.getProperty("FolderName") +
	 * "\\Screen\\" + g_iCapture_Count + ".png"; if(FunctionsLibrary.driver != null)
	 * { File
	 * scrFile=((TakesScreenshot)FunctionsLibrary.driver).getScreenshotAs(OutputType
	 * .FILE); FileUtils.copyFile(scrFile, new File(I_sFile)); } //objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>"
	 * + sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=WINGDINGS SIZE=4></FONT><FONT FACE=VERDANA SIZE=2>"
	 * + sActual
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED>O</FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
	 * + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>"); //objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>"
	 * + sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED>O</FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
	 * + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>"); objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
	 * + I_sFile +"'style=color:#ff0000>" + sResult + "</A></B></FONT></TD></TR>");
	 * } else { objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B>"
	 * + sResult + "</B></FONT></TD></TR>"); } } else
	 * if(sResult.equalsIgnoreCase("NOT COMPLETED")) { if
	 * (properties.getProperty("CaptureScreenShotforWarning").equalsIgnoreCase("YES"
	 * )) { String I_sFile=""; g_iCapture_Count="Screen" + d.getHours()
	 * +d.getMinutes() + d.getSeconds(); I_sFile =
	 * properties.getProperty("FolderName") +
	 * "\\Screen\\" + g_iCapture_Count + ".jpeg"; if(FunctionsLibrary.driver !=
	 * null){ File
	 * scrFile=((TakesScreenshot)FunctionsLibrary.driver).getScreenshotAs(OutputType
	 * .FILE); FileUtils.copyFile(scrFile, new File(I_sFile)); } //objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>"
	 * + sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=WINGDINGS SIZE=4>2></FONT><FONT FACE=VERDANA SIZE=2><A HREF='"
	 * + I_sFile +"'>" + sActual +
	 * "</A></FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED>O</FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B>"
	 * + sResult + "</B></FONT></TD></TR>"); objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B><A HREF='"
	 * + I_sFile +"'>" + sResult + "</A></B></FONT></TD></TR>"); } else { objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#EEEEEE WIDTH=100%><FONT FACE=VERDANA SIZE=2>"
	 * + sTestCaseName +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA SIZE=2>" +
	 * sDesc +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sExpected
	 * +"</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA SIZE=2>" +
	 * sActual +
	 * "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><B>"
	 * + sResult + "</B></FONT></TD></TR>"); } } else{ if(sExpected
	 * !="Component should start execution") { objFile.
	 * write("<TR COLS=5><TD BGCOLOR=#FAAC58; colspan='5';text-align: justify;word-wrap: break-word: break-all;white-space: pre-line;><FONT FACE=VERDANA SIZE=2>"
	 * + "" + "</FONT><FONT FACE=VERDANA SIZE=2>" + sDesc +
	 * "</FONT><FONT FACE=VERDANA SIZE=2>" + sExpected
	 * +"</FONT><FONT FACE='WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 ><B>"
	 * + sResult + "</B></FONT></TD></TR>"); }
	 * 
	 * } objFile.close(); } }
	 */

	// @SuppressWarnings("deprecation")
	// public static void fnSummaryInsertTestCase(String scenarioName, String
	// TCName, String strReportFileName,String summeryReportName, String Time,
	// String strStatus, String strStopTime ) throws IOException
	// {
	// Date d = new Date();
	// String gsTempFile = summeryReportName;
	// String gsTempFile1 = strReportFileName;
	// strReportFileName = strReportFileName.replace("results/", "");
	// Path objPath=Paths.get(gsTempFile);
	// System.out.println(objPath);
	// Path objPath1=Paths.get(gsTempFile1);
	//
	// String[] arrTime = Time.split(" ");
	// for (int i=0; i< arrTime.length; i++)
	// {
	// if (arrTime[i].contains("Hour"))
	// {
	// int temphour = Integer.parseInt(arrTime[i-1]);
	// hour = hour + temphour;
	// }
	// if (arrTime[i].contains("Minute"))
	// {
	// int tempmin = Integer.parseInt(arrTime[i-1]);
	// minute = minute + tempmin;
	// }
	// if (arrTime[i].contains("Second"))
	// {
	// int tempseconds = Integer.parseInt(arrTime[i-1]);
	// second = second + tempseconds;
	// }
	// }
	//
	//
	//
	// gsTempFile = summeryReportName;
	//
	// /* if(gsTempFile1 =="")
	// {
	// gsTempFile1 = properties.getProperty("FolderName") +d.getYear() +
	// d.getMonth() + d.getDay() + "_" + d.getHours() + d.getMinutes() +
	// d.getSeconds()+ ".htm";
	// }*/
	//
	// g_SummaryTotal_TC = g_SummaryTotal_TC+1;
	// /*
	//
	// */
	// if(Files.exists(objPath))
	// {
	// FileWriter objFile=new FileWriter(gsTempFile,true);
	// if(strStatus.equalsIgnoreCase("PASS"))
	// {
	// g_SummaryTotal_Pass = g_SummaryTotal_Pass+1;
	// objFile.write("<TR COLS=6><TD BGCOLOR=#EEEEEE WIDTH=15%><TestCaseId
	// FACE=VERDANA COLOR=BLACK SIZE=2>" + TCName + "</TestCaseId></TD><TD
	// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><A HREF='" +
	// strReportFileName + "'>" + scenarioName + "</A></FONT></TD><TD
	// BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2>" + Time +
	// "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE=WINGDINGS 2' SIZE=5
	// COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><Status>" +
	// strStatus + "</Status></FONT></TD></TR>");
	// }
	// else if (strStatus.equalsIgnoreCase("FAIL"))
	// {
	// g_SummaryTotal_Fail = g_SummaryTotal_Fail+1;
	// objFile.write("<TR COLS=6><TD BGCOLOR=#EEEEEE WIDTH=15%><TestCaseId
	// FACE=VERDANA COLOR=BLACK SIZE=2>" + TCName + "</TestCaseId></TD><TD
	// BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><A HREF='" +
	// strReportFileName + "'>" + scenarioName + "</A></FONT></TD><TD
	// BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2>" + Time +
	// "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE=WINGDINGS 2' SIZE=5
	// COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><Status>" + strStatus +
	// "</Status></FONT></TD></TR>");
	// }
	//
	// objFile.close();
	// }
	// if(Files.exists(objPath1))
	// {
	// FileWriter objFile1=new FileWriter(gsTempFile1,true);
	// if(strStatus.equalsIgnoreCase("PASS"))
	// {
	// //g_SummaryTotal_Pass = g_SummaryTotal_Pass+1;
	//
	// objFile1.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1
	// WIDTH=100%>");
	// objFile1.write("<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA
	// COLOR=WHITE SIZE=2><B>Execution Status : " + strStatus +
	// "</B></FONT></B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA
	// COLOR=WHITE SIZE=2><B>Execution Time: "+ Time + "</B></FONT></TD><TD
	// COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution
	// Stop Time : "+ strStopTime + "</B></FONT></TD></TR>");
	//
	// }
	// else if (strStatus.equalsIgnoreCase("FAIL"))
	// {
	// //g_SummaryTotal_Fail = g_SummaryTotal_Fail+1;
	//
	// objFile1.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1
	// WIDTH=100%>");
	// objFile1.write("<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA
	// COLOR=WHITE SIZE=2><B>Execution Status : <FONT FACE=VERDANA SIZE=2
	// COLOR=RED><B>" + strStatus + "</B></FONT></B></FONT></TD><TD COLSPAN=16
	// BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution Time: "+
	// Time + "</B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA
	// COLOR=WHITE SIZE=2><B>Execution Stop Time : "+ strStopTime +
	// "</B></FONT></TD></TR>"); }
	// objFile1.close();
	// }
	//
	// g_tSummaryTCStart_Time = d;
	//
	// }
	//
	@SuppressWarnings("deprecation")
	public static void fnSummaryInsertHeader() throws IOException {
		Date d = new Date();
		String gsTempFile = properties.getProperty("SummaryFileName");
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("SummaryFolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}
		// Path objPath=Paths.get(gsTempFile);
		FileWriter objFile = new FileWriter(gsTempFile, true);

		objFile.write(
				"<HTML><BODY><script language='javascript' type='text/javascript'>setFilterGrid('table2');</script><TABLE id='table2' BORDER=1 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
		objFile.write(
				"<THEAD><TR><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Test Case Name</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Scenario Name</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Release</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Path</B></FONT></TH><TH BGCOLOR=#FFCC99 WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><B>Release Name</B></FONT></TH><TH BGCOLOR=#FFCC99 SIZE=2 WIDTH=15%><B>Execution Time</B></FONT></TH><TH BGCOLOR=#FFCC99 SIZE=2 WIDTH=10%><select id='filterText' style='display:inline-block' onchange='filterText()'><option BGCOLOR=#FFCC99 SIZE=0 disabled selected>Select</option><option value='Pass'>PASS</option><option value='FAIL'>FAIL</option><option value='ALL'>All</option></select><B>Result</B></FONT></TH></TR></THEAD>");
		objFile.close();
	}

	@SuppressWarnings("deprecation")
	public static void fnDetailedInsertTestCase(String scenarioName, String TCName,
			String releaseName, String strReportFileName, String summeryReportName, String Time, String strStatus,
			String strStopTime) throws IOException {
		Date d = new Date();
		String gsTempFile = summeryReportName;
		String gsTempFile1 = strReportFileName;
		strReportFileName = strReportFileName.replace("results/", "");
		Path objPath = Paths.get(gsTempFile);
		System.out.println(objPath);
		Path objPath1 = Paths.get(gsTempFile1);

		SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm:ss aa");
		SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm:ss aa");
		try {
			strStopTime = outFormat.format(inFormat.parse(strStopTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] arrTime = Time.split(" ");
		for (int i = 0; i < arrTime.length; i++) {
			if (arrTime[i].contains("Hour")) {
				int temphour = Integer.parseInt(arrTime[i - 1]);
				hour = hour + temphour;
			}
			if (arrTime[i].contains("Minute")) {
				int tempmin = Integer.parseInt(arrTime[i - 1]);
				minute = minute + tempmin;
			}
			if (arrTime[i].contains("Second")) {
				int tempseconds = Integer.parseInt(arrTime[i - 1]);
				second = second + tempseconds;
			}
		}

		gsTempFile = summeryReportName;

		/*
		 * if(gsTempFile1 =="") { gsTempFile1 = properties.getProperty("FolderName")
		 * +d.getYear() + d.getMonth() + d.getDay() + "_" + d.getHours() +
		 * d.getMinutes() + d.getSeconds()+ ".htm"; }
		 */

		g_SummaryTotal_TC = g_SummaryTotal_TC + 1;
		/*		      
				                     
		*/
		if (Files.exists(objPath)) {
			FileWriter objFile = new FileWriter(gsTempFile, true);
			if (strStatus.equalsIgnoreCase("PASS")) {
				g_SummaryTotal_Pass = g_SummaryTotal_Pass + 1;
				objFile.write(
						"<TR class='content' COLS=7><TD BGCOLOR=#EEEEEE WIDTH=15%><TestCaseId FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ TCName
								+ "</TestCaseId></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><A HREF='"
								+ strReportFileName + "'>" + scenarioName
								+ "</Module></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><Release FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ releaseName
								+ "</Release></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ Time
								+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE=WINGDINGS 2' SIZE=5 COLOR=GREEN></FONT><FONT FACE=VERDANA SIZE=2 COLOR=GREEN><Status>"
								+ strStatus
								+ "</Status></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
								+ filterreport + ".js'></script>");
			} else if (strStatus.equalsIgnoreCase("FAIL")) {
				g_SummaryTotal_Fail = g_SummaryTotal_Fail + 1;
				objFile.write(
						"<TR class='content' COLS=7><TD BGCOLOR=#EEEEEE WIDTH=15%><TestCaseId FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ TCName
								+ "</TestCaseId></TD><TD BGCOLOR=#EEEEEE WIDTH=30%><FONT FACE=VERDANA COLOR=BLACK SIZE=2><A HREF='"
								+ strReportFileName + "'>" + scenarioName
								+ "</Module></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><Release FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ releaseName
								+ "</Release></TD><TD BGCOLOR=#EEEEEE WIDTH=15%><FONT FACE=VERDANA COLOR=BLACK SIZE=2>"
								+ Time
								+ "</FONT></TD><TD BGCOLOR=#EEEEEE WIDTH=10%><FONT FACE=WINGDINGS 2' SIZE=5 COLOR=RED></FONT><FONT FACE=VERDANA SIZE=2 COLOR=RED><Status>"
								+ strStatus
								+ "</Status></FONT></TD></TR><script src='https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js'></script><script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js'></script><script src='"
								+ filterreport + ".js'></script>");
			}
			counter = 1;
			objFile.close();
		}
		if (Files.exists(objPath1)) {
			FileWriter objFile1 = new FileWriter(gsTempFile1, true);
			if (strStatus.equalsIgnoreCase("PASS")) {
				// g_SummaryTotal_Pass = g_SummaryTotal_Pass+1;

				objFile1.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
				objFile1.write(
						"<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution Status : "
								+ strStatus
								+ "</B></FONT></B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution Time: "
								+ Time
								+ "</B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><StopTime>Execution Stop Time : "
								+ strStopTime + "</StopTime></FONT></TD></TR>");

			} else if (strStatus.equalsIgnoreCase("FAIL")) {
				// g_SummaryTotal_Fail = g_SummaryTotal_Fail+1;

				objFile1.write("<TABLE BORDER=0 BGCOLOR=BLACK CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
				objFile1.write(
						"<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution Status : <FONT FACE=VERDANA SIZE=2 COLOR=RED><B>"
								+ strStatus
								+ "</B></FONT></B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Execution Time: "
								+ Time
								+ "</B></FONT></TD><TD COLSPAN=16 BGCOLOR=#66699 ><FONT FACE=VERDANA COLOR=WHITE SIZE=2><StopTime>Execution Stop Time : "
								+ strStopTime + "</StopTime></FONT></TD></TR>");
			}
			counter = 1;
			objFile1.close();
		}

		g_tSummaryTCStart_Time = d;

	}


	@SuppressWarnings("deprecation")
	public static void fnSummaryCloseHtml() throws IOException {

		Date d = new Date();
		String gsTempFile = properties.getProperty("SummaryFileName");
		Path objPath = Paths.get(gsTempFile);
		String strRelease = properties.getProperty("ReleaseName");
		String strSprint = properties.getProperty("Sprint");
		if (gsTempFile == "") {
			gsTempFile = properties.getProperty("SummaryFolderName") + d.getYear() + d.getMonth() + d.getDay() + "_"
					+ d.getHours() + d.getMinutes() + d.getSeconds() + ".htm";
		}

		g_tSummaryEnd_Time = d;

		System.out.println("Total hours in summary close " + hour);
		System.out.println("Total minutes in summary close " + minute);
		System.out.println("Total seconds in summary close " + second);
		int hours;
		int minutes;

		int newSeconds;
		int newSecond1;
		String strExecutiontime = "";

		int seconds = (hour * 60 * 60) + (minute * 60) + second;

		if (seconds >= 3600) {
			hours = seconds / 3600;
			minutes = (seconds % 3600) / 60;
			newSeconds = (seconds % 3600) % 60;
			if (minutes == 0) {
				newSecond1 = (seconds % 3600) % 60;
				if (newSecond1 != 0) {
					strExecutiontime = hours + " Hour(s)" + newSecond1 + " Second(s)";
				} else {
					strExecutiontime = hours + " Hour(s)";
				}
			}

			if (minutes > 0 && minutes < 60) {
				if (newSeconds > 0 && minutes > 0) {
					strExecutiontime = hours + " Hour(s) " + minutes + " Minute(s)" + newSeconds + " Second(s)";
				}
				if (newSeconds == 0 && minutes > 0) {
					strExecutiontime = hours + " Hour(s) " + minutes + " Minute(s)";
				}
			}

			if (minutes > 60) {
				seconds = minutes % 60;
				minutes = minutes / 60;
				strExecutiontime = hours + " Hour(s) " + minutes + " Minutes " + seconds + " Seconds";
			}

		} else {
			minutes = seconds / 60;
			seconds = seconds % 60;
			if (minutes > 0 && seconds == 0) {
				strExecutiontime = minutes + " Minute(s)";
			}
			if (minutes > 0 && seconds > 0) {
				strExecutiontime = minutes + " Minute(s) " + seconds + " Second(s)";
			}
			if (minutes == 0) {
				strExecutiontime = seconds + " Second(s)";
			}
		}

		String sTime = strExecutiontime;

		if (Files.exists(objPath)) {
			FileWriter objFile = new FileWriter(gsTempFile, true);
			objFile.write("<HTML><BODY><TABLE BORDER=1 CELLPADDING=3 CELLSPACING=1 WIDTH=100%>");
			objFile.write("<TR><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Release : "
					+ strRelease
					+ "</B></FONT></TD><TD COLSPAN=6 BGCOLOR=#66699><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Sprint Number : "
					+ strSprint + "</B></FONT></TD></TR>");
			objFile.write(
					"<TR COLS=5><TD BGCOLOR=#66699 WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Total Test Case Executed : "
							+ g_SummaryTotal_TC
							+ "</B></FONT></TD><TD BGCOLOR=#66699 WIDTH=15%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Total Test Cases Passed : "
							+ g_SummaryTotal_Pass
							+ "</B></FONT></TD><TD BGCOLOR=#66699 WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B> Total Test Cases Failed : "
							+ g_SummaryTotal_Fail
							+ "</B></FONT></TD><TD BGCOLOR=#66699 WIDTH=25%><FONT FACE=VERDANA COLOR=WHITE SIZE=2><B>Total Execution Time : "
							+ sTime + " </B></FONT></TD></TR>");
			objFile.write("</TABLE></BODY></HTML>");
			objFile.close();
		}
		String SendMail = properties.getProperty("SendMail");
		if (SendMail.equalsIgnoreCase("yes")) {
			// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Common.SendMail(properties.getProperty("MailTo"),
			// properties.getProperty("MailCC"), gsTempFile, sTime, strRelease,
			// properties.getProperty("ModuleName"), String.valueOf(g_SummaryTotal_TC),
			// String.valueOf(g_SummaryTotal_Pass),String.valueOf(g_SummaryTotal_Fail),
			// properties.getProperty("Region"));
		}
	}

	//////////////////////////// Close Summary Report
	//////////////////////////// End/////////////////////////////////////////////////////////////////////////////
	public static String fnSecondsToTime(int intSeconds) {
		int hours, minutes, seconds;
		hours = intSeconds / 3600;
		intSeconds = intSeconds % 3600;
		minutes = intSeconds / 60;
		seconds = intSeconds % 60;
		return hours + ":" + minutes + ":" + seconds;
	}
	//////////////////////////// Send Email
	//////////////////////////// End/////////////////////////////////////////////////////////////////////////////////////
}