package com.webapp.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

	public List<Map<String, List<String>>> getData(String jsonFilePath) 
	{
		List<Map<String, List<String>>> jsonData = new ArrayList<Map<String, List<String>>>();
		
	    try {
	        
			ObjectMapper mapper = new ObjectMapper();

			Map<String, Map<String,String>> map = mapper.readValue(
					new File("./src/test/resources/TestData.json"),
					new TypeReference<Map<String, Map<String,String>>>() {
			});
			
			System.out.println("Map is "+map);
			
			Set<String> key=new LinkedHashSet<String>();
			List<String> val=new ArrayList<String>();
			
			for (int i = 1; i <= map.size(); i++)
			{
				Map<String,String> str=map.get("Iteration"+i+"");
				
				for (Entry<String, String> data : str.entrySet())
				{
					System.out.println("Key is "+data.getKey()+ " Value is "+data.getValue());
					
					key.add(data.getKey());
					val.add(data.getValue());
				}
			}
			System.out.println("Key are "+key);
			System.out.println("Values are "+val);
			
			int size=val.size()/key.size();

			int j=0;
			int temp=0;
			
			for (String string : key)
			{
				Map<String, List<String>> keyVal = new HashMap<String, List<String>>();
				List<String> values = new ArrayList<String>();
				for (int i=0; i < size ; i++) 
				{
					values.add(val.get(j));
					j=j+key.size();
					keyVal.put(string, values);
				}
				temp=temp+1;
				j=temp;
				
				jsonData.add(keyVal);
			}
			System.out.println("json data is "+jsonData);
			System.out.println("json data size is "+jsonData.size());
			
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		return jsonData;
	}

}
