package com.webapp.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;

public class ReportEncoder {

    public static String encodeFileToBase64Binary(String fileName){
         String encodedfile = null;
         try {
        	 
 			String resultFolderPath = (String) HTML.properties.get("ResultsFolderPath");
			
 			String resultsPath=FilenameUtils.concat(resultFolderPath,fileName);
 			//System.out.println(resultsPath);
 	    	String path=resultsPath.replace("\\", "/");
 	    	String updatedPath=path.replace("results/results", "results");
 	    	String detailedFileName=updatedPath.replace("/", "\\");
 			System.out.println(detailedFileName);
        	 
 			File file =  new File(detailedFileName);
            FileInputStream fileInputStreamReader = new FileInputStream(file);
             byte[] bytes = new byte[(int)file.length()];
             fileInputStreamReader.read(bytes);
             fileInputStreamReader.close();
             encodedfile = new String(Base64.encodeBase64(bytes),"UTF-8");
             System.out.println(encodedfile);
         } catch (FileNotFoundException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

         return encodedfile;
     }
}
