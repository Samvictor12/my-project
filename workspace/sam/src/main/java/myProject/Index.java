package myProject;

import database.Database;

public class Index {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Test();
	}
}

//Number,Account,reason,transfer,debited,credit,Date
class Test extends Database {
	String database = "table";
	public Test() {
		selectFile(database);
		//addField(database,"account,reason,debited,credited");
		//add();
		//contains("account", "ici");
		//contains("Number", "2");
		//update("debited", "101");
		//delete();
		//save(database);
		output();
	}
}
