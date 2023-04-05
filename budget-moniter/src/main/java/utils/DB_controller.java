package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class DB_controller {

	private Map<String, Map<String, String>> fieldValues = new LinkedHashMap<String, Map<String, String>>();
	private Map<String, Map<String, String>> resultData = new LinkedHashMap<String, Map<String, String>>();
	private String fileName;

	public DB_controller(String fileName) {
		this.fileName = fileName;
		readFile();
	}

	// read file
	public String readFile() {
		String data = "";
		try {
			File file = new File(constance.resourceLocation + "Database\\" + fileName + ".txt");
			Scanner input = new Scanner(file);
			data = input.next();
			input.close();
			inputData(data);
		} catch (FileNotFoundException e) {
			try {
				File file = new File(constance.resourceLocation + "Database\\rawData.txt");
				Scanner input = new Scanner(file);
				data = input.nextLine();
				input.close();
				inputData(data);
				save();
				readFile();
			} catch (FileNotFoundException a) {
				a.printStackTrace();
			}
		}
		return data;

	}

	// data reading
	public void inputData(String input) {// get input
		String[] listValue = input.split(":");
		for (int i = 0; i < listValue.length; i++) {
			String data = listValue[i].replace("+", " ");
			String[] SplitData = data.split("~");
			fieldValues.put(SplitData[0] ,asMap(SplitData[1]));
		}
		resultData = fieldValues;
	}

	// convert data to map value
	public Map<String, String> asMap(String input) {

		Map<String, String> map = new HashMap<String, String>();
		String[] sInput = input.split(",");
		for (int i = 0; i < sInput.length; i++) {
			String[] svalue = sInput[i].split("=");
			map.put(svalue[0], svalue[1]);
		}
		return map;
	}
	public void orderBy(String fieldName) {
		removeFilter();
		ArrayList<Map<String, String>> check = new ArrayList<Map<String, String>>();
		Map<String, Map<String, String>> sort = new LinkedHashMap<String, Map<String, String>>();
//		convert to array list for sorting operation
		for (String record : resultData.keySet()) {
			if (!record.equals("0")) {
				resultData.get(record).put("record", record);
				check.add(resultData.get(record));
			}
		}
//		arrange in order
		try {
			Integer.parseInt(check.get(0).get(fieldName));
			for (int i = 0; i < check.size()-1; i++) {
				Integer first = Integer.parseInt(check.get(i).get(fieldName));
				Integer second = Integer.parseInt(check.get(i+1).get(fieldName));
				int value = first.compareTo(second);
				if(value > 0) {
					Map<String, String> temp = check.get(i);
					check.set(i, check.get(i+1));
					check.set(i+1, temp);
					i=-1;
				}
			}
		} catch (NumberFormatException e) {
			for (int i = 0; i < check.size()-1; i++) {
				int value = check.get(i).get(fieldName).compareTo(check.get(i+1).get(fieldName));
				if(value > 0) {
					Map<String, String> temp = check.get(i);
					check.set(i, check.get(i+1));
					check.set(i+1, temp);
					i=-1;
				}
			}
		}
//		reattached in records
		for (int i = 0; i < check.size(); i++) {
			String record = check.get(i).get("record");
			check.get(i).remove("record");
			sort.put(record, check.get(i));
		}
		resultData = sort;
	}
	
	public void orderByDesc(String fieldName) {
		removeFilter();
		ArrayList<Map<String, String>> check = new ArrayList<Map<String, String>>();
		Map<String, Map<String, String>> sort = new LinkedHashMap<String, Map<String, String>>();
//		convert to array list for sorting operation
		for (String record : resultData.keySet()) {
			if (!record.equals("0")) {
				resultData.get(record).put("record", record);
				check.add(resultData.get(record));
			}
		}
//		arrange in reverse order
		try {
			Integer.parseInt(check.get(0).get(fieldName));
			for (int i = 0; i < check.size()-1; i++) {
				Integer first = Integer.parseInt(check.get(i).get(fieldName));
				Integer second = Integer.parseInt(check.get(i+1).get(fieldName));
				int value = first.compareTo(second);
				if(value < 0) {
					Map<String, String> temp = check.get(i);
					check.set(i, check.get(i+1));
					check.set(i+1, temp);
					i=-1;
				}
			}
		} catch (NumberFormatException e) {
			for (int i = 0; i < check.size()-1; i++) {
				int value = check.get(i).get(fieldName).compareTo(check.get(i+1).get(fieldName));
				if(value < 0) {
					Map<String, String> temp = check.get(i);
					check.set(i, check.get(i+1));
					check.set(i+1, temp);
					i=-1;
				}
			}
		}
		
//		reattached in records
		for (int i = 0; i < check.size(); i++) {
			String record = check.get(i).get("record");
			check.get(i).remove("record");
			sort.put(record, check.get(i));
		}
		resultData = sort;
	}
	
	// get record datas
	public Map<String, Map<String, String>> record() {
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		for (String record : resultData.keySet()) {
			if (!record.equals("0")) {
				data.put(record, resultData.get(record));
			}
		}
		return data;
	}
	
	public Map<String, String> getRecord(String recordNumber) {
		return fieldValues.get(recordNumber);
	}

	// view output in a console
	public void output() {
		for(String record : resultData.keySet()) {
			if (!record.equals("0")) {
				System.out.println("record - "+record+"\n");
				for(String key : resultData.get(record).keySet()){
					System.out.println(key + " = " + resultData.get(record).get(key));
				}
				System.out.println("");
			}
		}
	}
	
	// view output in a console
		public void output(String records) {
			System.out.println("records - "+records+"\n");
			if (fieldValues.containsKey(records)) {
				for(String key : fieldValues.get(records).keySet()){
					System.out.println(key + " = " + fieldValues.get(records).get(key));
				}
				System.out.println("");
			}else {
				System.out.println("No records found");
			}
		}
	// data filter
	public void equals(String key, String value) {// to filter_out data with the contains the value
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		for (String record : resultData.keySet()) {
			if (resultData.get(record).get(key).toString().equalsIgnoreCase(value)) {
				data.put(record, resultData.get(record));
			}
		}
		resultData = data;
	}
	
	// data filter
	public void notEquals(String key, String value) {// to filter_out data with the contains the value
		Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		for (String record : resultData.keySet()) {
			if (!resultData.get(record).get(key).toString().equalsIgnoreCase(value)) {
				data.put(record, resultData.get(record));
			}
		}
		resultData = data;
	}

	// remove filter
	public void removeFilter() {
		resultData = fieldValues;
	}
	public int getLastKey() {
		int count=1;
		for(String record : resultData.keySet()) {
			if (count == resultData.size()) {
				return Integer.parseInt(record);
			}
			count++;
		}
		return 0;
	}
	public int getFirstKey() {
		for(String record : resultData.keySet()) {
			if(!record.equals("0")) {
				return Integer.parseInt(record);
			}
		}
		return -1;
	}
	// add record to data
	public String insert(String inputs) {// new record to database
		int recordNumber = getLastKey()+1;
		Map<String, String> map = asMap(inputs);
		for (String key : fieldValues.get("0").keySet()) {
			if (!map.containsKey(key)) {
				map.put(key, "N/A");
			}
		}
		fieldValues.put(recordNumber+"",map);
		resultData = fieldValues;
		save();
		return recordNumber+"";
	}

	// add field to all records
	public void insertField(String field) {// add field name in table
		String[] key = field.split(",");
		for (String record : fieldValues.keySet()) {
			for (int j = 0; j < key.length; j++) {
				if(!fieldValues.get(record).containsKey(key[j])) {
					fieldValues.get(record).put(key[j], "N/A");
				}
			}
		}
		resultData = fieldValues;
		save();
	}

//	update values of data
	public void update(String recordNumber, String input) {// Update the record value With a specific key
		String[] splitInputs = input.split(",");
		if(!recordNumber.equals("0")) {
			for (int k = 0; k < splitInputs.length; k++) {
				String[] splitData = splitInputs[k].split("=");
				System.out.println(splitInputs[k]);
				fieldValues.get(recordNumber).replace(splitData[0], splitData[1]);
			}
		}
		
		resultData = fieldValues;
		save();
	}

//	update values of records
	public void update(String input) {// Update the record value With a specific key
		String[] splitInputs = input.split(",");
		for (String record : resultData.keySet()) {
			if(!record.equals("0")) {
				for (int i = 0; i < splitInputs.length; i++) {
					String[] splitData = splitInputs[i].split("=");
					fieldValues.get(record).replace(splitData[0], splitData[1]);
				}
			}
		}
		resultData = fieldValues;
		save();
	}

//	update values of data
	public void updateFieldName(String oldKey, String newKey) {// Update the record value With a specific key
		for (String record : fieldValues.keySet()) {
			String value = fieldValues.get(record).get(oldKey);
			if (value!=null) {
				fieldValues.get(record).remove(oldKey);
				fieldValues.get(record).put(newKey, value);
			}
		}
		resultData = fieldValues;
		save();
	}

//	data delete
	public void deleteField(String fieldName) {// delete the records
		for (String record : fieldValues.keySet()) {
			fieldValues.get(record).remove(fieldName);
		}
		resultData = fieldValues;
		save();
	}
	
//	data delete
	public void delete() {// delete the records
		for (String record : resultData.keySet()) {
			if(!record.equals("0")) {
				fieldValues.remove(record);
			}
		}
		resultData = fieldValues;
		save();
	}
	
//	data delete
	public void delete(String record) {// delete the records
		if(!record.equals("0")) {
			fieldValues.remove(record);
		}
		resultData = fieldValues;
		save();
	}

	// save data in file
	public void save() {
		String fieldData = fieldValues.toString();
		fieldData = fieldData.replace("}, ", ":");
		fieldData = fieldData.replace("}", "");
		fieldData = fieldData.replace("={", "~");
		fieldData = fieldData.replace("{", "");
		fieldData = fieldData.replace(", ", ",");
		fieldData = fieldData.replace(" ", "+");
		try {
			FileWriter myWriter = new FileWriter(constance.resourceLocation + "DataBase\\" + fileName + ".txt");
			myWriter.write(fieldData);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred : " + e);
		}
	}

	public void rename() {
		// File file = new File(constance.resourceLocation+"");

	}

}
