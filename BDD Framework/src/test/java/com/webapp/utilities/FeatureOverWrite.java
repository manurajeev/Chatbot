package com.webapp.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

/**
 * FeatureOverWrite class defined for creating and overwriting feature file from masterFeature folder
 *  based on the selection of test cases in TestExecutioner GUI.
 * @author Prabhuling Kalshetti
 *
 */

public class FeatureOverWrite 
{
    public List<String> setExcelDataToFeature() throws IOException {
       
    
    	File featureFile = new File("./src/test/resources/MasterFeature/masterFature.feature");
    	
    	
    	List<String> fileData = new ArrayList<String>();
        try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(featureFile)), "UTF-8"))) {
            String data;
            List<Map<String, List<String>>> jsonData = null;
            boolean foundHashTag = false;
            boolean featureData = false;
                       
            while ((data = buffReader.readLine()) != null) {
                String jsonFilePath = null;
                if (data.trim().contains("##@externaldata")) 
                {
                	jsonFilePath = data.substring(StringUtils.ordinalIndexOf(data, "@", 2)+1, data.lastIndexOf("@"));
                    foundHashTag = true;
                    fileData.add(data);
                }
                if (foundHashTag) 
                {
                    jsonData = new JsonReader().getData(jsonFilePath);

                   
                    int iterationVal = 0;
                    for (Entry<String, List<String>> dat : jsonData.get(0).entrySet()) 
                    {
                    	List<String> valueList = dat.getValue();
                    	iterationVal=valueList.size();
                    }
                    
        			for (int i = 0; i < iterationVal; i++) 
        			{
        				String cellData = "";
                        for (int rowNumber = 0; rowNumber < jsonData.size(); rowNumber++) 
                        {
                            for (Entry<String, List<String>> mapData : jsonData.get(rowNumber).entrySet()) 
                            {
                            	List<String> valueList = mapData.getValue();
                            	
                            	//System.out.println("Value list is "+valueList);
                            //	System.out.println(valueList.size());
                            	
                            	cellData = cellData + "|" +mapData.getValue().get(i);
                             }
                        }
                        fileData.add(cellData + "|");
                        System.out.println(fileData);
        			} 
                   foundHashTag = false;
                    featureData = true;
                    continue;
                }
                if(data.startsWith("|")||data.endsWith("|")){
                    if(featureData){
                        continue;
                    } else{
                        fileData.add(data);
                        continue;
                    }
                } else {
                    featureData = false;
                }
                fileData.add(data);
            }
        }
        return fileData; 
    }

	private static List<File> listOfFeatureFiles(File folder) {
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

    public  void overrideFeatureFiles(String featuresDirectoryPath) throws IOException{
        List<File> listOfFeatureFiles = listOfFeatureFiles(new File(featuresDirectoryPath));
        for (File featureFile : listOfFeatureFiles) {
            List<String> featureWithExcelData = setExcelDataToFeature();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(featureFile), "UTF-8"));) {
                for (String string : featureWithExcelData) {
                    writer.write(string);
                    writer.write("\n");
                }
            }
        }
    }
}
