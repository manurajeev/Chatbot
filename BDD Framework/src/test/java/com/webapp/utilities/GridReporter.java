package com.webapp.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.webapp.baseLibrary.FunctionsLibrary;
import com.webapp.stepDefinition.CommonStepDefinitions;

public class GridReporter {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-hh.mm.ss";
	//Utilities utils = new Utilities(this);
	private String strAbsolutepath = new File("").getAbsolutePath();
	// public static String strScreenshot;
	String strScreenshot;
	String strScreenshotDynamicLinkPath;
	static String reportFilepackagename;
	static String strAbsolutePath = new File("").getAbsolutePath();
	List<String> tempList_scenario_name1 = new ArrayList<String>(0);
	List<String> tempList_teststep_description = new ArrayList<String>(0);
	List<String> tempList_test_data = new ArrayList<String>(0);
	List<String> tempList_snapshot = new ArrayList<String>(0);
	List<String> tempList_status = new ArrayList<String>(0);
	List<String> tempList_result_description = new ArrayList<String>(0);

	List<String> listScenarioDetails = new ArrayList<String>(0);
	List<String> listPassDetails = new ArrayList<String>(0);
	List<String> listFailDetails = new ArrayList<String>(0);
	List<String> listTotalStepsDetails = new ArrayList<String>(0);
	List<String> listReportFileDetails = new ArrayList<String>(0);


	String strScenarioDetails = null;
	String strPassDetails = null;
	String strFailDetails = null;
	String strTotalStepsDetails = null;
	String strReportFileDetails = null;

	private String propFile = "ReportConfigFile.txt";
	private String propFileName = strAbsolutepath + "/resources/Config/" + propFile;

	public static int month;
	public static int day;
	public static int year;

	public String strFinalStartTime;
	public String strFinalStopTime;

	// public static String strStartTime;
	public static String strStopTime;

	public Properties properties= new Properties();

	public float timeElapsed;
	// public static long startTime;
	// public static long stopTime;
	public String strStartTime = null;
	public long startTime;
	long stopTime;


	private int hour;
	private int min;
	private int sec;
	private String am_pm;
	private boolean running = false;
	String strThread=null;

	public GridReporter(String strThread) {
		this.strThread = strThread;


	}

	public GridReporter() {

	}

	/*
	 * public static String now() { String strScreenshotPath = strAbsolutePath +
	 * "/results/screenshot/"; Calendar cal = Calendar.getInstance(); month =
	 * cal.get(Calendar.MONTH) + 1; day = cal.get(Calendar.DAY_OF_MONTH); year =
	 * cal.get(Calendar.YEAR); SimpleDateFormat sdf = new
	 * SimpleDateFormat(DATE_FORMAT_NOW); Random rand = new Random(); int num =
	 * rand.nextInt(1000); strScreenshot = (String) (strScreenshotPath +
	 * sdf.format(cal.getTime())+num +".png"); return sdf.format(cal.getTime());
	 * }
	 */




	public String start(Calendar calander) {

		strStartTime = null;
		int hour, min, sec;
		Long actualStartTime = System.currentTimeMillis();
		hour = calander.get(Calendar.HOUR);
		min = calander.get(Calendar.MINUTE);
		sec = calander.get(Calendar.SECOND);
		if (calander.get(Calendar.AM_PM) == 0)
			am_pm = "AM";
		else
			am_pm = "PM";

		running = true;
		startTime = actualStartTime;
		strStartTime = "" + hour + ":" + min + ":" + sec + " " + am_pm;
		System.out.println(strStartTime);
		return strStartTime;
	}

	public long start() {
		Calendar calendar = new GregorianCalendar();
		strStartTime = null;
		int hour, min, sec;
		Long actualStartTime = System.currentTimeMillis();
		hour = calendar.get(Calendar.HOUR);
		min = calendar.get(Calendar.MINUTE);
		sec = calendar.get(Calendar.SECOND);
		if (calendar.get(Calendar.AM_PM) == 0) {
			am_pm = "AM";
		} else {
			am_pm = "PM";
		}
		running = true;
		startTime = actualStartTime;
		return  startTime;

	}

	public String stop() {
		String strStoTime = null;

		Calendar stop = new GregorianCalendar();
		Long actualstopTime = System.currentTimeMillis();
		hour = stop.get(Calendar.HOUR);
		min = stop.get(Calendar.MINUTE);
		sec = stop.get(Calendar.SECOND);
		if (stop.get(Calendar.AM_PM) == 0)
			am_pm = "AM";
		else
			am_pm = "PM";
		// .currentTimeMillis();
		stopTime = actualstopTime;
		strStoTime = "" + hour + ":" + min + ":" + sec + " " + am_pm;

		running = false;
		return strStoTime;
	}

	public float getElapsedTime() {
		float elapsedTime = 0;
		if (running) {
			elapsedTime = (System.currentTimeMillis() - startTime);
			// .currentTimeMillis() - startTime);
		} else {
			//strStopTime-startTime
			elapsedTime = (stopTime - startTime);
		}
		return elapsedTime;
	}

/*	public float getElapsedTime(long startTime) {
		float elapsedTime = 0;
		if (running) {
			elapsedTime = (System.currentTimeMillis() - startTime);
			// .currentTimeMillis() - startTime);
		} else {
			//strStopTime-startTime
			elapsedTime = (stopTime - startTime);
		}
		return elapsedTime;
	}*/

	public String reportFilestart(String StrExecutionStartTime,
			String className, String scenarioName) throws IOException {

		String strReportFile = null;

		String sConfigfilespath = "Config";
		System.out.println(sConfigfilespath);
		File file = new File (sConfigfilespath + "\\Sys.properties");
			try {
			HTML.counter=0;
			HTML.fnInitilization(className,scenarioName,StrExecutionStartTime );
			HTML.fnInsertResult(className,scenarioName, "Component should start execution","Started Executing " + className + " Component","","", "");
			FileInputStream fis = new FileInputStream( file);
			properties.load(fis);
			fis.close();
			strReportFile = properties.getProperty("FileName");
			reportFilepackagename =  getpropValue("reportPackageName");

		} catch (Exception ex) {
			ex.printStackTrace();
		}


		return strReportFile;
	}

	public void writeStepResult(String testCaseName, String strScenarioName, String strStepDescription,String  expctedValue, String actualValue,
			String strStatus,  String strReportFile) {

			try{
			      HTML.fnInsertResult( testCaseName,strScenarioName, strStepDescription, expctedValue, actualValue, strStatus,strReportFile);
			       }catch (Exception e){
			       e.printStackTrace();
			       }
		}


/*	public void writeResult(String testCaseName, String strScenarioName,String releaseName, String strStepDescription,String  expctedValue, String actualValue,
			String strStatus,  String strReportFile) {

			try{
			      HTML.fnInsertStepResult( testCaseName,strScenarioName,releaseName, strStepDescription, expctedValue, actualValue, strStatus,strReportFile);
			       }catch (Exception e){
			       e.printStackTrace();
			       }
}*/

	public void writeStepResultForVerification(String testCaseName,String strScenarioName, String strStepDescription, String expctedValue, String actualValue,String strStatus,
			 String strReportFile) {

}

	public String addDynamicContents(String testCaseName, String scenario, String releaseName, String strReportFileName, float timeElapsed) {

		FileWriter aWriter = null;
		File file = null;
		BufferedReader reader = null;
		String line = "";
		String oldtext = "";
		// Code for Final status
		File f1 = new File(strReportFileName);
		String strFinalStatus = "Pass";

		System.out.println("Elapsed Time in addDynamicContents" + timeElapsed);
		// Code added for time
		int seconds = Math.round((timeElapsed / 60000) * 60);
		int hours = 0;
		int minutes = 0;
		int newSeconds = 0;
		int newSecond1 = 0;
		String strExecutiontime = "";
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
					strExecutiontime = hours + " Hour(s) " + minutes + " Minute(s) " + newSeconds + " Second(s)";
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
				strExecutiontime = minutes + " Minute(s) ";
			}
			if (minutes > 0 && seconds > 0) {
				strExecutiontime = minutes + " Minute(s) " + seconds + " Second(s)";
			}
			if (minutes == 0) {
				strExecutiontime = seconds + " Second(s)";
			}
		}

		try {
			// Add final status to result file
			file = new File(strReportFileName);
			reader = new BufferedReader(new FileReader(file));
			String strFinalStatus1 = "COLOR=GREEN><B>Pass</B>";


			/* ********* added ************ */
			BufferedReader br = new BufferedReader(new FileReader(file));

	           //One way of reading the file
			/*boolean tds=false;
		   System.out.println("Reading the file ...using readLine() method:");
		   String contentLine = br.readLine();
		   while (contentLine != null) {
		      System.out.println(contentLine);
		      tds  = StringUtils.contains(contentLine, "FAIL") || StringUtils.contains(contentLine, "Fail");
		      contentLine = br.readLine();
		   }
		   if (br != null)
				 br.close();

			if (tds){
				strFinalStatus = "Pass";
				strFinalStatus1 = "COLOR=GREEN><B>Pass</B>";
			}

			if (StringUtils.contains(contentLine, "NOT COMPLETED")){
			strFinalStatus = "Not Completed";
				strFinalStatus1 = "<td><b><font color=\"WHITE\">" + strFinalStatus + "<b></td>\n";
			}*/
			boolean tds=false;
			boolean tcPassed=false;
		   System.out.println("Reading the file ...using readLine() method:");
		   String contentLine = br.readLine();

		   while (contentLine != null) {
			   tcPassed  = StringUtils.contains(contentLine, "Pass") || StringUtils.contains(contentLine, "PASS");
			      contentLine = br.readLine();
			   }
		   br = new BufferedReader(new FileReader(file));
		   contentLine = br.readLine();
		   while (contentLine != null) {
		      tds  = StringUtils.contains(contentLine, "FAIL") || StringUtils.contains(contentLine, "Fail");
		      contentLine = br.readLine();
		   }
		   if (br != null)
				 br.close();

			if (tds){
				strFinalStatus = "FAIL";
				strFinalStatus1 = "COLOR=WHITE><B>FAIL</B>";
			}else	if (StringUtils.contains(contentLine, "NOT COMPLETED")){
			strFinalStatus = "Not Completed";
				strFinalStatus1 = "<td><b><font color=\"WHITE\">" + strFinalStatus + "<b></td>\n";
			} else if(!tds && !tcPassed) {
				strFinalStatus = "FAIL";
				strFinalStatus1 = "COLOR=WHITE><B>FAIL</B>";
			}

			File FailedTCFile= new File("config/FailedTC.txt");
			String failTCPath = FailedTCFile.getAbsolutePath();
			if(strFinalStatus.equals("FAIL")) {
				try (FileWriter fw = new FileWriter(failTCPath, true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw)) {
					out.println(CommonStepDefinitions.tc_id);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			// Modify result file to add dynamic contents

			File file1 = new File(strReportFileName);
			String newtext = "";

			// Modify Final status

			line = "";
			oldtext = "";
			newtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			reader.close();
			// Replace a word in a file
			newtext = oldtext.replaceAll("<td><b>ExecutionStatus</b></td>", strFinalStatus1);

			FileWriter writer = new FileWriter(strReportFileName);
			writer.write(newtext);
			writer.close();
			line = "";
			oldtext = "";
			newtext = "";
			reader = new BufferedReader(new FileReader(file1));
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			reader.close();

			// Replace a word in a file
			newtext = oldtext.replaceAll("strStopTime", strStopTime);

			writer = new FileWriter(strReportFileName);
			writer.write(newtext);
			writer.close();

			reader = new BufferedReader(new FileReader(file1));
			line = "";
			oldtext = "";
			newtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line + "\r\n";
			}
			reader.close();

			// Replace a word in a file
			newtext = oldtext.replaceAll("timeElapsed", strExecutiontime);
			writer = new FileWriter(strReportFileName);
			writer.write(newtext);
			writer.close();

			aWriter = new FileWriter(strReportFileName, true);

			aWriter.write("</table>");
			aWriter.write("</div>");
			aWriter.write("</div>");
			aWriter.write("</div>");
			aWriter.write("</div>");
			aWriter.write("</td>");
			aWriter.write("</tr>");
			aWriter.write("</table>");
			aWriter.write("</div>");
			aWriter.write("</div>");
			aWriter.write("</body>");
			aWriter.write("</html>");
			aWriter.flush();
			aWriter.close();
			String summeryReportName =(String) HTML.properties.get("SummaryFileName");
			//HTML.fnSummaryInsertTestCase(scenario, testCaseName,strReportFileName, summeryReportName, strExecutiontime, strFinalStatus, strStopTime);

			HTML.fnDetailedInsertTestCase(scenario, testCaseName,releaseName,strReportFileName, summeryReportName, strExecutiontime, strFinalStatus, strStopTime);

		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}
		return strFinalStatus;

}



	public String getpropValue(String propValue) {
		Properties prop = new Properties();
		String requiredprop = "";
		InputStream is;
		try {
			is = new FileInputStream(propFileName);
			prop.load(is);
			requiredprop = prop.getProperty(propValue);
			System.out.println(propValue + ": " + requiredprop);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return requiredprop;
	}



}
