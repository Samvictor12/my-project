package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utils.constance;

public class Database {
	private ArrayList<Map<String, String>> fieldValues = new ArrayList<Map<String, String>>();
	public ArrayList<Map<String, String>> resultData = new ArrayList<Map<String, String>>();
	private String[] fields;
    
	//read file
    public String selectFile(String fileName) {
		String data ="";
		try {
			File file = new File(constance.resourceLocation+"Database\\"+fileName+".txt");
			Scanner input = new Scanner(file);
			data = input.nextLine();
			input.close();
			inputData(data);
		} catch (FileNotFoundException e) {
			try {
				File file = new File(constance.resourceLocation+"StandardData\\rawdata.txt");
				Scanner input = new Scanner(file);
				data = input.nextLine();
				input.close();
				inputData(data);
				save(fileName);
				selectFile(fileName);
			} catch (FileNotFoundException a) {
				a.printStackTrace();
			}
			
		} 
		return data;
		
	}
    
    // data reading
    public void inputData(String input){// get input
    	String[] listValue = input.split(":");
    	fields = listValue[0].split(",");
        for(int i = 1; i<listValue.length; i++){
        	fieldValues.add(SaveMap(listValue[i]));
        }
        resultData = fieldValues; 
    }
   
    public Map<String, String> SaveMap(String input){//convert data to map value
        Map<String, String> map = new HashMap<String, String>();
        String[] sInput = input.split(",");
        ;
        for(int i=0;i<fields.length;i++){
            map.put(fields[i],sInput[i]);
        }
        return map;
    }
    
    public void output(){// display output of Map data
        for(int i = 0;i<resultData.size();i++){
            for (int j = 0; j < fields.length ; j++) {
				System.out.println(fields[j]+" = "+resultData.get(i).get(fields[j]));
			}
            System.out.println("");
        }
    }
    
    //data filter
    public void contains(String key, String value){// to filter_out data with the contains the value
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>(); 
        for(int i = 0 ; i<resultData.size();i++){
           if (resultData.get(i).get(key).toString().contains(value)){
                data.add(resultData.get(i));
           }
        }
        resultData = data;
    }
    
    //update values of data
    public void update(String key, String value){//Update the record value With a specific key
        for (int i = 0; i < resultData.size(); i++) {
        	for (int j = 1; j < fieldValues.size(); j++) {
    			if (resultData.get(i).get("Number").equalsIgnoreCase(fieldValues.get(j).get("Number"))) {
    				fieldValues.get(j).replace(key, value);
    			}
    		}
		}
        resultData=fieldValues; 
    }
    
    //data delete
    public void delete() {// delete the records
		for (int i = 0; i < resultData.size(); i++) {
			for (int j = 1; j < fieldValues.size(); j++) {
				if(resultData.get(i).get("Number").equalsIgnoreCase(fieldValues.get(j).get("Number"))) {
					fieldValues.remove(j);
				}
			}
		}
		resultData = fieldValues;
	}
    
    //add record to data
    public void add() {// new record to database
    	Scanner input = new Scanner(System.in);
    	Map<String, String> data = new HashMap<String, String>();
    	for (int i = 0; i < fields.length; i++) {
    		if (fields[i].equalsIgnoreCase("Number")) {
    			data.put(fields[i], fieldValues.size()+1+"");
			}else {
				System.out.println("please enter the '"+fields[i]+"' value - ");
	    		String value = input.nextLine();
	    		data.put(fields[i], value);
			}
		}
    	input.close();
    	fieldValues.add(data);
    	resultData = fieldValues;
    }

    //add fields for data
    public void addField(String fileName, String fields) {// add field name in table
		String[] splitFields = fields.split(",");
		for (int i = 0; i < splitFields.length; i++) {
			for (int j = 0; j < fieldValues.size(); j++) {
				fieldValues.get(j).put(splitFields[i]," ");
			}
		}
		String data = fieldValues.get(0).keySet().toString();
		data = data.replace("[", "");
		data = data.replace("]", "");
		data = data.replace(" ", "");
		this.fields = data.split(",");
		resultData = fieldValues;
	}

    //save data in file
    public void save(String fileName){// to save the data 
    	String fieldData ="";
    	
    	String field = fieldValues.get(0).keySet().toString();
		field = field.replace("[", "");
		field = field.replace("]", "");
		field = field.replace(" ", "");
		this.fields = field.split(",");
		
		fieldData+=field+":";
    	
    	for (int i = 0; i < fieldValues.size(); i++) {
    		for (int j = 0; j < fields.length; j++) {
    			if (j==fields.length-1) {
    				fieldData+=resultData.get(i).get(fields[j])+":";
    			}else {
    				fieldData+=resultData.get(i).get(fields[j])+",";
    			}
    		}
		}
		try {
			FileWriter myWriter = new FileWriter(constance.resourceLocation + "DataBase\\" + fileName + ".txt");
			myWriter.write(fieldData);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred : " + e);
		}
	}
    
    public void rename() {
		//File file = new File(constance.resourceLocation+"");
		
	}
}
