package com.webapp.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HTML2XML {

	public static List<Map<String,String>> getTcidAndStatus() {
		InputStream isInHtml = null;
		//LinkedHashMap<String, String> tcidStatus = new LinkedHashMap<String, String>();
		List<Map<String,String>> summeryMaList = new ArrayList<Map<String, String>>();
		DataInputStream disInHtml = null;
		FileOutputStream fosOutHtml = null;
		FileWriter fwOutXml = null;
		FileReader frInHtml = null;
		BufferedWriter bwOutXml = null;
		BufferedReader brInHtml = null;
		try {

			String tempResultPath = (String) HTML.properties.get("TempResultPath");
			String summaryFileName = (String) HTML.properties.get("SummaryFileName");
			
			System.out.println("Generated summary report is "+summaryFileName);
			
			frInHtml = new FileReader(summaryFileName);
			//frInHtml = new FileReader(""+ tempResultPath + "/" + summaryFileName + "");
			brInHtml = new BufferedReader(frInHtml);
			SAXBuilder saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser", false);
			Document jdomDocument = saxBuilder.build(brInHtml);

			XMLOutputter outputter = new XMLOutputter();
			Format newFormat = outputter.getFormat();
			String encoding = "iso-8859-2";
			newFormat.setEncoding(encoding);
			outputter.setFormat(newFormat);

			try {
				outputter.output(jdomDocument, System.out);
				String xmlFilePath = tempResultPath+"/SummaryReport.xml";
				fwOutXml = new FileWriter(xmlFilePath);
				//fwOutXml = new FileWriter(""+tempResultPath+"/SummaryReport.xml");
				bwOutXml = new BufferedWriter(fwOutXml);
				outputter.output(jdomDocument, bwOutXml);
				System.out.flush();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				// Document doc = dBuilder.parse(xmlResponse);
				org.w3c.dom.Document doc = dBuilder.parse(new File(xmlFilePath));
				//org.w3c.dom.Document doc = dBuilder.parse(new File(""+tempResultPath+"/SummaryReport.xml"));
				doc.getDocumentElement().normalize();

				NodeList resonseNodeList = doc.getElementsByTagName("html");
				// for(int i=0; i<resonseNodeList.getLength();i++){
				Element emp = (Element) resonseNodeList.item(1);

				Node name2 = null;
				Node name = null;
				Node name3=null;

				try {
					int tags = emp.getElementsByTagName("Status").getLength();
					for (int j = 0; j < tags; j++) {
						Map<String, String> summeryMap = new HashMap<String, String>();
						name2 = emp.getElementsByTagName("TestCaseId").item(j).getFirstChild();
						String tcid = name2.getNodeValue();
						summeryMap.put("Test Case ID", tcid);
						System.out.println("id " + tcid);
						name = emp.getElementsByTagName("Status").item(j).getFirstChild();
						String tcStatus = name.getNodeValue();
						summeryMap.put("Status", tcStatus);
						name3 = emp.getElementsByTagName("Release").item(j).getFirstChild();
						String tcRelease = name3.getNodeValue();
						summeryMap.put("Release", tcRelease);
						System.out.println("Release " + tcRelease);

						summeryMaList.add(summeryMap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IOException | ParserConfigurationException | SAXException e) {

			}
		} catch (IOException e) {

		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			System.out.flush();
			try {
				isInHtml.close();
				disInHtml.close();
				fosOutHtml.flush();
				fosOutHtml.getFD().sync();
				fosOutHtml.close();
				fwOutXml.flush();
				fwOutXml.close();
				bwOutXml.close();
			} catch (Exception w) {

			}
		}
		return summeryMaList;
	}
}
