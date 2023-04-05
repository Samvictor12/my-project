package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class DB_controller_old {

	private ArrayList<Map<String, String>> fieldValues = new ArrayList<Map<String, String>>();
	private ArrayList<Map<String, String>> resultData = new ArrayList<Map<String, String>>();
	private String fileName;
	private String[] fields;

	public DB_controller_old(String fileName) {
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
				System.out.println(input);
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
			fieldValues.add(SaveAsMap(data));
		}
		String f = fieldValues.get(0).keySet().toString();
		f = f.replace("[", "");
		f = f.replace("]", "");
		f = f.replace(" ", "");
		fields = f.split(",");
		resultData = fieldValues;
	}

	// convert data to map value
	public Map<String, String> SaveAsMap(String input) {

		Map<String, String> map = new HashMap<String, String>();
		String[] sInput = input.split(",");
		for (int i = 0; i < sInput.length; i++) {
			String[] svalue = sInput[i].split("=");
			map.put(svalue[0], svalue[1]);
		}
		return map;
	}

	// get record datas
	public ArrayList<Map<String, String>> record() {
		return resultData;
	}

	// view output in a console
	public void output() {
		for (int i = 0; i < resultData.size(); i++) {
			for (int j = 0; j < fields.length; j++) {
				System.out.println(fields[j] + " = " + resultData.get(i).get(fields[j]));
			}
			System.out.println("");
		}
	}

	// data filter
	public void addFilter(String key, String value) {// to filter_out data with the contains the value
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i = 0; i < resultData.size(); i++) {
			if (resultData.get(i).get(key).toString().contains(value)) {
				data.add(resultData.get(i));
			}
		}
		resultData = data;
	}

	// remove filter
	public void removeFilter() {
		resultData = fieldValues;
	}

	// add record to data
	public void insert(String inputs) {// new record to database
		Map<String, String> map = SaveAsMap(inputs);
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].equalsIgnoreCase("number")) {
				map.put(fields[i], (Integer.parseInt(fieldValues.get(fieldValues.size() - 1).get("number")) + 1) + "");
			} else if (map.containsKey(fields[i])) {

			} else {
				map.put(fields[i], "-");
			}
		}
		fieldValues.add(map);
		save();
	}

	// add field to all records
	public void insertField(String field) {// add field name in table
		String[] splitField = field.split(",");
		for (int i = 0; i < fieldValues.size(); i++) {
			for (int j = 0; j < splitField.length; j++) {
				fieldValues.get(i).put(splitField[j], "-");
			}
		}
		resultData = fieldValues;
		save();
	}

//	update values of data
	public void update(String recordNumber, String input) {// Update the record value With a specific key
		String[] splitInputs = input.split(",");
		for (int j = 0; j < fieldValues.size(); j++) {
			if (fieldValues.get(j).get("number").equals(recordNumber)) {
				for (int k = 0; k < splitInputs.length; k++) {
					String[] splitData = splitInputs[k].split("=");
					fieldValues.get(j).replace(splitData[0], splitData[1]);
				}
			}
		}
		save();
	}

//	update values of records
	public void update(String input) {// Update the record value With a specific key
		String[] splitInputs = input.split(",");
		for (int i = 0; i < resultData.size(); i++) {
			if (!resultData.get(i).get("number").equalsIgnoreCase("0")) {
				String num = resultData.get(i).get("number");
				for (int j = 0; j < fieldValues.size(); j++) {
					if (fieldValues.get(j).get("number").equals(num)) {
						for (int k = 0; k < splitInputs.length; k++) {
							String[] splitData = splitInputs[k].split("=");
							fieldValues.get(j).replace(splitData[0], splitData[1]);
						}
					}
				}
			}
		}
		save();
	}

//	update values of data
	public void updateFieldName(String oldKey, String newKey) {// Update the record value With a specific key
		for (int i = 0; i < fieldValues.size(); i++) {
			String value = fieldValues.get(i).get(oldKey);
			if (value!=null) {
				fieldValues.get(i).remove(oldKey);
				fieldValues.get(i).put(newKey, value);
			}
		}
		save();
	}

//	data delete
	public void delete() {// delete the records
		for (int i = 0; i < resultData.size(); i++) {
			for (int j = 1; j < fieldValues.size(); j++) {
				if (resultData.get(i).get("Number").equalsIgnoreCase(fieldValues.get(j).get("Number"))) {
					fieldValues.remove(j);
				}
			}
		}
		resultData = fieldValues;
	}

	// save data in file
	public void save() {
		String fieldData = fieldValues.toString();
		fieldData = fieldData.replace("}, ", ":");
		fieldData = fieldData.replace("}", "");
		fieldData = fieldData.replace("{", "");
		fieldData = fieldData.replace("[", "");
		fieldData = fieldData.replace("]", "");
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
