package com.webapp.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class FeatureFileUpdate {
	public static String updatedFileName = "";
	public static String simpleFileName = "";
	public static List<String> totalTCsList = new ArrayList<>();
	final static Map<String, LinkedList<String>> mapTcList = new LinkedHashMap<String, LinkedList<String>>();

	/**
	 * This method create new feature file at run time based on the selection of
	 * test cases from external GUI.
	 *
	 * @param featureFile
	 * @param tcid
	 * @return List<String>
	 * @Author Prabhuling Kalshetti
	 * @throws IOException
	 *
	 */

	private static List<String> createNewFeatureFile(File featureFile, List<String> tcid) throws IOException {
		List<String> fileDatalist = new ArrayList<String>();
		BufferedReader featureFileData = new BufferedReader(new FileReader(featureFile));
		String lineStr = featureFileData.readLine();
		String TCHeader = null;

		while (lineStr != null && tcid.size() > 0) {
			if (lineStr.contains("Feature:") || lineStr.contains("Background:") && !lineStr.equals("")) {
				fileDatalist.add(lineStr);
				lineStr = featureFileData.readLine();
				while (!(lineStr.contains("Scenario:") || lineStr.contains("Scenario Outline:"))) {
					fileDatalist.add(lineStr);
					lineStr = featureFileData.readLine();
				}
			}

			if (lineStr.contains("Scenario:") || lineStr.contains("Scenario Outline:") || lineStr.startsWith("@")) {
				String nextLineStr = "";

				if (lineStr.startsWith("@")) {
					fileDatalist.add(lineStr);
					lineStr = featureFileData.readLine();
				}

				// nextLineStr.replaceAll(nextLineStr, lineStr);
				nextLineStr = featureFileData.readLine();
				if(nextLineStr == null) {
					nextLineStr = "";
				}
				for (int i = 0; i < tcid.size(); i++) {
					if (nextLineStr.contains(tcid.get(i)))
					{
						//System.out.println("TC Found : " + tcid.get(i));
						fileDatalist.add(lineStr);
						fileDatalist.add(nextLineStr);
						lineStr = featureFileData.readLine();
						// System.out.println(lineStr);
						if (lineStr != null)
							while (lineStr != null && !(lineStr.contains("Scenario:")
									|| lineStr.contains("Scenario Outline:") || lineStr.startsWith("@"))) {
								fileDatalist.add(lineStr);
								lineStr = featureFileData.readLine();
							}
						tcid.remove(i);
						break;
					} else if (nextLineStr.contains("TC_Mul")) {
						for (Entry<String, LinkedList<String>> entry : mapTcList.entrySet()) {
							for (int j = 0; j < entry.getValue().size(); j++) {
								//System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue().get(j));

								if (entry.getValue().get(j).equals(tcid.get(i))) {

									TCHeader = entry.getKey();
									//System.out.println(entry.getKey());
									break;
								}
							}
						}

						if (nextLineStr.contains(TCHeader + ">")) {
							fileDatalist.add(lineStr);
							if (nextLineStr != null)
								while (nextLineStr != null && !(nextLineStr.contains("Scenario:")
										|| nextLineStr.contains("Scenario Outline:") || nextLineStr.startsWith("@")
										|| nextLineStr.contains("Examples:"))) {
									fileDatalist.add(nextLineStr);
									nextLineStr = featureFileData.readLine();
								}
							// fileDatalist.add("Examples:");
							while (nextLineStr.contains("Examples:"))
							{
								fileDatalist.add(nextLineStr);
								nextLineStr = featureFileData.readLine();
								fileDatalist.add(nextLineStr);
								nextLineStr = featureFileData.readLine();
							}

							for (Entry<String, LinkedList<String>> entry : mapTcList.entrySet()) {
								if (nextLineStr != null) {
									if (!nextLineStr.trim().equals("")) {
										for (int j = 0; j < entry.getValue().size(); j++) {
											//System.out.println(
												//	"Key = " + entry.getKey() + " Value = " + entry.getValue().get(j));

											for (int k = i; k < tcid.size(); k++) {

												if (nextLineStr.contains(tcid.get(k))) {
													fileDatalist.add(nextLineStr);
													tcid.remove(k);
													// nextLineStr = featureFileData.readLine();
													break;
												}
											}

											nextLineStr = featureFileData.readLine();

											if ((nextLineStr != null)) {
												if (nextLineStr.trim().equals("")) {
													break;
												}
											} else {
												break;
											}

										}

									} else {
										nextLineStr = featureFileData.readLine();
										break;
									}
								} else {
									break;
								}
							}

							break;
						}
					}

					else
					{
						//System.out.println("Not Found - Test Case ID : " + tcid.get(i));
					}

					if (i == tcid.size() - 1) {
						do {
							lineStr = featureFileData.readLine();
						} while (lineStr != null && !(lineStr.contains("Scenario:")
								|| lineStr.contains("Scenario Outline:") || lineStr.contains("#EOF#")));
					}

				}

			}

			if (lineStr != null && lineStr.equals(""))
				lineStr = featureFileData.readLine();

			if (lineStr != null && lineStr.contains("#EOF#"))
				break;

		}
		featureFileData.close();
		if (!listContainsVal(fileDatalist, "#EOF#"))
			fileDatalist.add("##############EOF#############");

		return fileDatalist;
	}

	/**
	 * Collecting list of file feature file from MasterFeature folder
	 *
	 * @param featureFile
	 * @param tcid
	 * @return File
	 * @Author Prabhuling Kalshetti *
	 */

	protected static List<File> listOfFeatureFiles(File folder) {
		List<File> featureFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				featureFiles.addAll(listOfFeatureFiles(fileEntry));
			} else {
				if (fileEntry.isFile() && fileEntry.getName().endsWith(".feature")) {
					featureFiles.add(fileEntry);
				}
			}
		}
		return featureFiles;
	}

	/**
	 * This method create new feature and add the selected scenario's every step in
	 * the feature file.
	 *
	 * @param featureFile
	 * @param tcid
	 * @return List<String>
	 * @Author Prabhuling Kalshetti
	 * @throws IOException
	 *
	 */
	public static void newFeatureFile(String featuresDirectoryPath) throws IOException {
		List<File> listOfFeatureFiles = listOfFeatureFiles(new File(featuresDirectoryPath));

		List<String> testCasesSet = getListOfSelectedTestCases();

		for (File featureFile : listOfFeatureFiles) {
			// if(featureFile.getName().replace(".feature","").equals("masterFeature"))
			// featureFile.getName().replace(".feature","");
			{
				List<String> featureWithExcelData = createNewFeatureFile(featureFile, testCasesSet);
				simpleFileName = "./src/test/resources/features/";
				String featureFilePath = featureFile.getAbsolutePath();
				String fileName = featureFilePath
						.substring(featureFilePath.lastIndexOf("\\") + 1, featureFilePath.length())
						.replace(".feature", "").trim();

				updatedFileName = simpleFileName + fileName + "_Updated" + ".feature";
				try (BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(updatedFileName), "UTF-8"));) {
					for (String string : featureWithExcelData) {
						if (!string.contains("@"))
							writer.write(string);
						//System.out.println(string);
						writer.write("\n");
					}
				}
			}
		}
		System.out.println("Feature file updated successfully");
	}

	/**
	 * Counting and returning number of rows in selected scenario
	 *
	 * @param featureFile
	 * @param tcid
	 * @return int
	 * @throws IOException
	 * @Author Prabhuling Kalshetti
	 *
	 */
	public static int numberOfRowsInOutline(String tcid) throws IOException {
		LineNumberReader lineNumberReader = null;
		try {
			File featureFile = null;
			List<File> listOfFeatureFiles = listOfFeatureFiles(new File("./src/test/resources/features"));

			for (File featureFiles : listOfFeatureFiles) {
				if (featureFiles.getName().replace(".feature", "").contains("_Updated")) {
					BufferedReader read = new BufferedReader(new FileReader(featureFiles));
					String line = read.readLine();
					while (line != null) {
						if (line.contains(tcid)) {
							featureFile = featureFiles;

							break;
						}
						line = read.readLine();
					}

				}
			}
			if (featureFile == null)
				return 0;

			int exampleStartLine = 0;
			int exampleEndLine = 0;
			final Path path = Paths.get("./" + featureFile);

			final long lineCount = Files.lines(path).count();
			String data = "";
			lineNumberReader = new LineNumberReader(new FileReader(featureFile));

			while ((data = lineNumberReader.readLine()) != null) {
				int num = lineNumberReader.getLineNumber();

				if (data.trim().contains(tcid)) {
					//System.out.println("Contains TC");

					// System.out.println("line number is "+num);

					for (int i = num; i < lineCount; i++) {
						String nextLine = "";
						String line = FileUtils.readLines(featureFile).get(i);
						if (i + 2 < lineCount) {
							nextLine = FileUtils.readLines(featureFile).get(i + 2);
						}
						// System.out.println(line);
						if (line.trim().equals("")) {
							continue;
						}
						if (line.contains("Examples:")) {
							exampleStartLine = num;

						}
						/*
						 * if(nextLine.trim().equals("")){ continue; }
						 */

						if (line.trim().contains("Scenario Outline:") || line.trim().contains("Scenario:")
								|| line.trim().contains("@") || line.contains("#EOF#")) {

							if (line.trim().contains("@") || line.trim().contains("#EOF#")) {
								exampleEndLine = num;
							} else
								exampleEndLine = num;

							break;
						}
						num++;
					}
					break;
				}
			}
			return exampleEndLine - exampleStartLine - 2;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lineNumberReader.close();
		}

		return 0;
	}

	/**
	 * This method get the list of selected test cases from Swing GUI and write the
	 * test cases in UpdatedTCsFile txt file.
	 *
	 * @return List<String>
	 * @Author Prabhuling Kalshetti
	 *
	 */
	public static List<String> getListOfSelectedTestCases() {

		List<String> testCasesList = new ArrayList<>();

		File updatedTCsFile = new File("UpdatedFiles/updatedTCsFile.txt");
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(updatedTCsFile);

			// inputStream = new FileInputStream(path);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				// System.out.println(line);
				testCasesList.add(line.trim());

			}
			Collections.sort(testCasesList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {

			sc.close();
		}
		return testCasesList;
	}

	/*
	 * public static String extractTestCaseID(String line) {
	 *
	 * int startIndexOfTC = line.indexOf("test case 'TC_") + 11; int lastIndexOfTC =
	 * line.indexOf("' for");
	 *
	 * String tcId = line.substring(startIndexOfTC, lastIndexOfTC);
	 *
	 * return tcId; }
	 */

	public static String extractTestCaseID(String line) {

		int startIndexOfTC=0;
		int lastIndexOfTC;
		if (line.contains("<TC_Mul")) {
			startIndexOfTC = line.indexOf("testcases Id  \"<TC_") + 15;
			lastIndexOfTC = line.indexOf("\" for");
		} else if(line.contains("testcaseId")) {
			if(line.contains("TC_"))
				startIndexOfTC = line.indexOf("testcaseId 'TC_") + 12;
			else if(line.contains("TC-"))
				startIndexOfTC = line.indexOf("testcaseId 'TC-") + 12;
			lastIndexOfTC = line.indexOf("' for");
		} else {
			if(line.contains("TC_"))
				startIndexOfTC = line.indexOf("test case 'TC_") + 11;
			else if(line.contains("TC-"))
				startIndexOfTC = line.indexOf("test case 'TC-") + 11;
			lastIndexOfTC = line.indexOf("' for");
		}

		String tcId = line.substring(startIndexOfTC, lastIndexOfTC);

		return tcId;
	}

	/**
	 * Fetching the test cases details from MasterFeature file
	 *
	 * @return List<String>
	 * @Author Prabhuling Kalshetti
	 * @throws IOException
	 *
	 */
	public List<String> testCaseList() {
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream fis = new FileInputStream("Config/Sys.properties");
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String allFeatures = properties.getProperty("SelectedFeatures");
		String[] featuresarr = allFeatures.split(",");
		List<File> listOfFeatureFiles = listOfFeatureFiles(new File("./src/test/resources/MasterFeature"));
		List<String> list = new LinkedList<String>();
		LinkedList<String> tcList;
		String tcHeader = null;

		if(featuresarr[0].equalsIgnoreCase("")) {
		for (File featureFile : listOfFeatureFiles) {
			try {
				FileReader in;

				in = new FileReader(featureFile);

				BufferedReader br = new BufferedReader(in);
				String line = br.readLine();
				while (line != null) {
					tcList = new LinkedList<String>();

					//System.out.println(line);
					String regex = "TC[_,-]" + "\\d+";
					Pattern p = Pattern.compile(regex); // the pattern to search for
					Matcher m = p.matcher(line);

					if (line.contains("test case 'TC_") || line.contains("test case 'TC-")) {
						list.add(FeatureFileUpdate.extractTestCaseID(line));
					} else if (line.contains("testcaseId 'TC_") || line.contains("testcaseId 'TC-")) {
						list.add(FeatureFileUpdate.extractTestCaseID(line));
					}
					else if (line.contains("<TC_Mul")) {
						int startIndexOfTC;
						int lastIndexOfTC;
						startIndexOfTC = line.indexOf("test cases \"<TC_") + 13;
						lastIndexOfTC = line.indexOf("\" for") - 1;

						tcHeader = line.substring(startIndexOfTC, lastIndexOfTC);
					} else if (m.find() && (line.contains("| TC_") || line.contains("|TC_") || line.contains("| TC-") || line.contains("|TC-"))) {
						// tcList.clear();
						while (line != null && (!line.trim().equals(""))) {
							String[] testCaseArray = line.replaceAll("[|]+", "*").split("\\*");
							list.add(testCaseArray[1].trim());
							//System.out.println("Print list of Test cases" + list);

							tcList.add(testCaseArray[1].trim());

							// updatedTcList.add(testCaseArray[1].trim());
							Arrays.fill(testCaseArray, null);

							line = br.readLine();
						}
						if(mapTcList.containsKey(tcHeader)) {
							System.out.println("Change the header "+tcHeader);
						}
						mapTcList.put(tcHeader, tcList);
					}

					line = br.readLine();
				}

				for (String tc : list) {
					//System.out.println(tc);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}else {
			List<File> selectedFeatureFiles = new ArrayList<File>();
			for(int i=0;i<featuresarr.length;i++) {
				selectedFeatureFiles.add(new File("./src/test/resources/MasterFeature/"+featuresarr[i]+".feature"));
			}
			for (File featureFile : selectedFeatureFiles) {
				try {
					FileReader in;
					in = new FileReader(featureFile);

					BufferedReader br = new BufferedReader(in);
					String line = br.readLine();
					while (line != null) {
						tcList = new LinkedList<String>();
						//System.out.println(line);
						String regex = "TC[_,-]" + "\\d+";
						Pattern p = Pattern.compile(regex); // the pattern to search for
						Matcher m = p.matcher(line);

						if (line.contains("test case 'TC_") || line.contains("test case 'TC-")) {
							list.add(FeatureFileUpdate.extractTestCaseID(line));
						} else if (line.contains("testcaseId 'TC_") || line.contains("testcaseId 'TC-")) {
							list.add(FeatureFileUpdate.extractTestCaseID(line));
						}
						else if (line.contains("<TC_Mul")) {
							int startIndexOfTC;
							int lastIndexOfTC;
							startIndexOfTC = line.indexOf("testcases Id \"<TC_") + 13;
							lastIndexOfTC = line.indexOf("\" for") - 1;

							tcHeader = line.substring(startIndexOfTC, lastIndexOfTC);
						} else if (m.find() && (line.contains("| TC_") || line.contains("|TC_") || line.contains("| TC-") || line.contains("|TC-"))) {
							// tcList.clear();
							while (line != null && (!line.trim().equals(""))) {
								String[] testCaseArray = line.replaceAll("[|]+", "*").split("\\*");
								list.add(testCaseArray[1].trim());
								//System.out.println("Print list of Test cases" + list);

								tcList.add(testCaseArray[1].trim());

								// updatedTcList.add(testCaseArray[1].trim());
								Arrays.fill(testCaseArray, null);

								line = br.readLine();
							}

							mapTcList.put(tcHeader, tcList);
						}

						line = br.readLine();
					}

					for (String tc : list) {
						//System.out.println(tc);
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			}
		//Duplicate Test Cases Check
		final Set<String> duplicateTCs = new HashSet();
		final Set<String> checkTC = new HashSet();
		  for (String tc : list) {
		   if (!checkTC.add(tc))
			   duplicateTCs.add(tc);
		  }
		  if(duplicateTCs.size()>0)
			  System.out.println("Duplicate Test Cases found : " + duplicateTCs);
		  //else
			  //System.out.println("No Duplicates Found");
		  System.out.println("TC Count : "+list.size());
		return list;
	}

	public static boolean listContainsVal(List<String> list, String str) {

		for (String listStr : list) {
			if (listStr.contains(str)) {
				return true;
			}
		}
		return false;
	}
}
