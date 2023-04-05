package Backend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import utils.DB_controller;
import utils.constance;

public class inputForm {
	public DB_controller account, statement, budget, mainpage, remainder, choiseValues, pendingPayments;
	
	public Map<String, Map<String, String>> getAccount() {
		 return account.record();
	}
	public Map<String, Map<String, String>> getStatement() {
		return statement.record();
	}
	public Map<String, Map<String, String>> getBudget() {
		return budget.record();
	}
	public Map<String, Map<String, String>> getMainpage() {
		return mainpage.record();
	}
	public Map<String, Map<String, String>> getPendingPayments() {
		return pendingPayments.record();
	}
	public String[] getList(String fieldName) {
		return choiseValues.getRecord("1").get(fieldName).split("-");
	}
	public inputForm() {
		account = new DB_controller("account");
		budget = new DB_controller("budget");
		statement = new DB_controller("Statement");
		mainpage = new DB_controller("mainpage");
		remainder = new DB_controller("remainder");
		choiseValues = new DB_controller("StaticValues");
		pendingPayments = new DB_controller("pendingPayments");
		
	}
	public void deleteall() {
		mainpage.update("saving=0.0,totalBudgetBalance=0.0,unUsedAmount=0.0,totalAccountExpence=0.0,active=true,totalBudgetAmount=0.0,totalAccountIncome=0.0,totalAccountBalance=0.0");
		statement.equals("active", "");
		statement.delete();
		account.equals("active", "");
		account.delete();
		budget.notEquals("budgetName", "General Saving");
		budget.delete();
	}
	public void output() {
		System.out.println("MAINPAGE\n");
		mainpage.output();
		System.out.println("ACCOUNT\n");
		account.output();
		System.out.println("BUDGET\n");
		budget.output();
		System.out.println("STATEMENT\n");
		statement.output();
		System.out.println("PENDING PAYMENTS\n");
		pendingPayments.output();
	}

	public void checkForMonthendactions() {
		Float budgetAmount = 0.0f;
		Float savingBudgetAmount = Float.parseFloat(budget.getRecord("1").get("budgetAmount"));
		String currentdate = constance.GetDataTime().split("-")[0];
		if (currentdate.split("/")[0].equals(getlastday(currentdate, 0)[2])) {
			
			for (String record : budget.record().keySet()) {
				String type = budget.getRecord(record).get("type");
				if (type.equalsIgnoreCase("expence")) {
					Float budgetBalance = Float.parseFloat(budget.getRecord(record).get("budgetBalance"));
					Float needToRepeat = Float.parseFloat(budget.getRecord(record).get("needToRepeat"));
					String[] nextend = getlastday(currentdate, 1);
					
					budgetAmount+=budgetBalance;
					
					if (needToRepeat.equals("true")) {
						String dueDate = nextend[2]+"/"+nextend[1]+"/"+nextend[0];
						budget.update(record,"dueDate="+dueDate+",budgetBalance=0");
					}else {
						budget.update(record,"active=false,budgetBalance=0");
					}
				}
			}
			savingBudgetAmount+=budgetAmount;
			budget.update("1", "budgetAmount="+savingBudgetAmount);
		}
		
		Float saving = Float.parseFloat(mainpage.getRecord("1").get("saving"));
		Float totalBudgetBalance = Float.parseFloat(mainpage.getRecord("1").get("totalBudgetBalance"));
		saving+=budgetAmount;
		totalBudgetBalance-=budgetAmount;
		mainpage.update("1", "totalBudgetBalance="+totalBudgetBalance+",saving="+saving);
	}
	
	public void checkForUpdateBudget() {
		Float unUsedAmount = Float.parseFloat(mainpage.getRecord("1").get("unUsedAmount"));
		Float totalBudgetBalance = Float.parseFloat(mainpage.getRecord("1").get("totalBudgetBalance"));
		if (unUsedAmount>0) {
			budget.orderByDesc("budgetpriority");
			for (String record : budget.record().keySet()) {
				System.out.println("record - "+record);
				Float budgetAssigned = Float.parseFloat(budget.getRecord(record).get("budgetAssigned"));
				Float budgetAmount = Float.parseFloat(budget.getRecord(record).get("budgetAmount"));
				if(budgetAssigned<budgetAmount) {
					budgetAmount=budgetAmount-budgetAssigned;
					System.out.println("budgetAmount = "+budgetAmount);
					if(budgetAmount<unUsedAmount) {
						budgetAssigned+=budgetAmount;
						unUsedAmount-=budgetAmount;
						totalBudgetBalance+=budgetAssigned;
						budget.update(record, "budgetAssigned="+budgetAssigned);
					}else {
						budgetAssigned+=unUsedAmount;
						unUsedAmount-=unUsedAmount;
						totalBudgetBalance+=budgetAssigned;
						budget.update(record, "budgetAssigned="+budgetAssigned);
						break;
					}
				}
			}
			
			mainpage.update("1", "unUsedAmount="+unUsedAmount+"totalBudgetBalance="+totalBudgetBalance);
		}
		
	}

	public void updateDueDate() {
		Map<String, Map<String, String>> records = budget.record();
		for (String record : records.keySet()) {
			String type = records.get(record).get("type");
			if (type.equalsIgnoreCase("emi")||type.equalsIgnoreCase("rd")) {
				String endDate = records.get(record).get("endDate");
				String dueDate = records.get(record).get("dueDate");
				String timePeriod = records.get(record).get("timePeriod");
				if(comparedate(constance.GetDataTime().split("-")[0],dueDate)<=0&&comparedate(constance.GetDataTime().split("-")[0],endDate)>0) {
					records.get(record).replace("dueDate", getnextdate(dueDate, timePeriod));
				}else if (comparedate(constance.GetDataTime().split("-")[0],endDate)>=0) {
					records.get(record).replace("active", "false");
				}
			}
		}
	}
//	to add statement and calculate account mainpage and budget datas 
	public void addStatement(String statementAmount, String statementReason, String accountName, String statementType,
			String active, String budgetName) {
		Float totalBudgetBalance = Float.parseFloat(mainpage.getRecord("1").get("totalBudgetBalance"));
		Float unUsedAmount = Float.parseFloat(mainpage.getRecord("1").get("unUsedAmount"));
		Float totalAccountExpence = Float.parseFloat(mainpage.getRecord("1").get("totalAccountExpence"));
		Float totalAccountIncome = Float.parseFloat(mainpage.getRecord("1").get("totalAccountIncome"));
		Float totalAccountBalance = Float.parseFloat(mainpage.getRecord("1").get("totalAccountBalance"));
		Float accountBalance = 0.0f;
		Float accountExpence = 0.0f;
		Float accountIncome = 0.0f;
		if (!statementReason.equals("initial")) {
			accountBalance = Float.parseFloat(account.getRecord(accountName).get("accountBalance"));
			accountExpence = Float.parseFloat(account.getRecord(accountName).get("accountExpence"));
			accountIncome = Float.parseFloat(account.getRecord(accountName).get("accountIncome"));
		}
		
		if (statementType.equals("credit")) {
			totalAccountBalance += Float.parseFloat(statementAmount);
			totalAccountIncome += Float.parseFloat(statementAmount);
			unUsedAmount += Float.parseFloat(statementAmount);
			accountBalance += Float.parseFloat(statementAmount);
			accountIncome += Float.parseFloat(statementAmount);
			mainpage.update("1", "totalAccountBalance=" + totalAccountBalance + ",totalAccountIncome="
					+ totalAccountIncome + ",unUsedAmount=" + unUsedAmount);
			account.update(accountName, "accountBalance=" + accountBalance + ",accountIncome=" + accountIncome);
			statement.insert("statementAmount=" + statementAmount + ",statementReason=" + statementReason
					+ ",currentAccountBalance=" + accountBalance + ",accountName=" + accountName + ",statementType="
					+ statementType + ",active=true,statementDate=" + constance.GetDataTime() + ",budgetName="
					+ budgetName);
		} else {
			totalAccountBalance -= Float.parseFloat(statementAmount);
			totalAccountExpence += Float.parseFloat(statementAmount);
			accountExpence += Float.parseFloat(statementAmount);
			accountBalance -= Float.parseFloat(statementAmount);
			totalBudgetBalance -= Float.parseFloat(statementAmount);
			if (!budgetName.equals("N/A")) {
				Float budgetBalance = Float.parseFloat(budget.getRecord(budgetName).get("budgetBalance"));
				budgetBalance -= Float.parseFloat(statementAmount);
				budget.update(budgetName, "budgetBalance=" + budgetBalance);
			}
			mainpage.update("1", "totalAccountBalance=" + totalAccountBalance + ",totalAccountExpence="
					+ totalAccountExpence + ",totalBudgetBalance=" + totalBudgetBalance);
			account.update(accountName, "accountExpence=" + accountExpence + ",accountBalance=" + accountBalance);
			statement.insert("statementAmount=" + statementAmount + ",statementReason=" + statementReason
					+ ",currentAccountBalance=" + accountBalance + ",accountName=" + accountName + ",statementType="
					+ statementType + ",active=true,statementDate=" + constance.GetDataTime() + ",budgetName="
					+ budgetName);
		}
	}

	public void addAccount(String accountName, String accountImage, String accountIncome) {
		String accountBalance = "0";
		if (Float.parseFloat(accountIncome) > 0) {
			accountBalance = accountIncome;
			String record = account
					.insert("accountName=" + accountName + ",accountImage=" + accountImage + ",accountIncome="
							+ accountIncome + ",accountExpence=0,accountBalance=" + accountBalance + ",active=true");
			addStatement(accountBalance, "initial", record, "credit", "true", "N/A");
		} else {
			account.insert("accountName=" + accountName + ",accountImage=" + accountImage + ",accountIncome="
					+ accountIncome + ",accountExpence=0,accountBalance=0,active=true");
		}
	}

	public void addBudget(String budgetAmount, String budgetimage, String budgetName, String budgetpriority,
			String startDate, String timePeriod, String type, String repeatTimes) {
		float unUsedAmount = Float.parseFloat(mainpage.getRecord("1").get("unUsedAmount")),
				budgetAmounts = Float.parseFloat(budgetAmount),
				totalBudgetBalance = Float.parseFloat(mainpage.getRecord("1").get("totalBudgetBalance")),
				totalBudgetAmount = Float.parseFloat(mainpage.getRecord("1").get("totalBudgetAmount")),
				budgetpercentage = 0, budgetAssigned = 0, budgetBalance = 0;
		String endDate = "", dueDate = "";
		int value = 0;

		switch (type) {
		case "expence":
			String[] needDay = getlastday(constance.GetDataTime().split("-")[0], 0);
			dueDate = "N/A";
			endDate = needDay[2]+"/"+needDay[1]+"/"+needDay[0];
			break;
		case "paydue":
			dueDate = startDate;
			startDate = "N/A";
			break;
		case "saving":
			dueDate = "N/A";
			endDate = "N/A";
			break;
		case "emi":
			value = comparedate(constance.GetDataTime().split("-")[0],startDate);
			if (value > 0) {
				dueDate = startDate;
			} else {
				dueDate = getnextdate(startDate, timePeriod);
			}
			endDate = getlastdate(startDate, repeatTimes, timePeriod);
			break;
		case "rd":
			value = comparedate(constance.GetDataTime().split("-")[0],startDate);
			if (value > 0) {
				dueDate = startDate;
			} else {
				dueDate = getnextdate(startDate, timePeriod);
			}
			endDate = getlastdate(startDate, repeatTimes, timePeriod);
			break;
		default:
			System.out.println("no type found");
			break;
		}

		if (unUsedAmount > budgetAmounts) {
			budgetAssigned = budgetAmounts;
			unUsedAmount -= budgetAmounts;
			budgetBalance = budgetAssigned;
		} else {
			budgetAssigned = unUsedAmount;
			unUsedAmount -= unUsedAmount;
			budgetBalance = budgetAssigned;
		}
		totalBudgetAmount += budgetAmounts;
		totalBudgetBalance += budgetAssigned;
		budgetpercentage = budgetBalance / budgetAmounts;

		mainpage.update("1", "totalBudgetBalance=" + totalBudgetBalance + ",totalBudgetAmount=" + totalBudgetAmount+ ",unUsedAmount=" + unUsedAmount);
		budget.insert("budgetpercentage=" + budgetpercentage + ",budgetBalance=" + budgetBalance + ",budgetAmount="
				+ budgetAmount + ",budgetimage=" + budgetimage + ",budgetdate=" + constance.GetDataTime()
				+ ",active=true,budgetName=" + budgetName + ",budgetpriority=" + budgetpriority + ",budgetAssigned="
				+ budgetAssigned + ",startDate=" + startDate + ",timePeriod=" + timePeriod + ",type=" + type
				+ ",repeatTimes=" + repeatTimes + ",endDate=" + endDate + ",dueDate=" + dueDate);
	}

	private String getnextdate(String dueDate, String timePeriod) {

		String[] date = dueDate.split("/");
		int year = Integer.parseInt(date[2]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[0]);
		int count = 0;
		int enddate = 0;
		switch (timePeriod) {
		case "daily":
			count = 0;
			day++;
			enddate = Integer.parseInt(getlastday(dueDate, count)[2]);
			if (enddate < day) {
				day -= enddate;
				count++;
			}
			date = getlastday(dueDate, count);
			return day + "/" + date[1] + "/" + date[0];
		case "weekly":
			count = 0;
			day = day + 7;
			enddate = Integer.parseInt(getlastday(dueDate, count)[2]);
			if (enddate < day) {
				day -= enddate;
				count++;
			}
			date = getlastday(dueDate, count);
			return day + "/" + date[1] + "/" + date[0];
		case "monthly":
			month++;
			if (month > 12) {
				month -= 12;
				year++;
			}
			return day + "/" + month + "/" + year;
		case "quaterly":
			month += 3;
			if (month > 12) {
				month -= 12;
				year++;
			}
			return day + "/" + month + "/" + year;
		case "yearly":
			year++;
			return day + "/" + month + "/" + year;
		default:
			return "no Timeperiod found";
		}
	}

	private String getlastdate(String startDate, String Duration, String timePeriod) {

		String[] date = startDate.split("/");
		int year = Integer.parseInt(date[2]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[0]);
		int times = Integer.parseInt(Duration);

		switch (timePeriod) {
		case "daily":
			int count = 0;
			int days = Integer.parseInt(getlastday(startDate, 0)[2]) - day;
			if (days < times) {
				while (days < times) {
					times = times - days;
					count++;
					days = Integer.parseInt(getlastday(startDate, count)[2]);
				}
			} else {
				times += day;
			}
			date = getlastday(startDate, count);
			return times + "/" + date[1] + "/" + date[0];
		case "weekly":
			count = 0;
			while (times != 0) {
				day = day + 7;
				int enddate = Integer.parseInt(getlastday(startDate, count)[2]);
				if (enddate < day) {
					day -= enddate;
					count++;
				}
				times--;
			}
			date = getlastday(startDate, count);
			return day + "/" + date[1] + "/" + date[0];
		case "monthly":
			int months = 12 - month;
			if (months < times) {
				while (months < times) {
					times -= months;
					months = 12;
					year++;
				}
			} else {
				times += month;
			}
			return day + "/" + times + "/" + year;
		case "quaterly":
			while (times != 0) {
				month += 3;
				if (month > 12) {
					month = month - 12;
					year++;
				}
				times--;
			}
			return day + "/" + month + "/" + year;
		case "yearly":
			year += times;
			return day + "/" + month + "/" + year;
		default:
			return "no Timeperiod found";
		}
	}

	private String[] getlastday(String startDate, int next) {
		String[] date = startDate.split("/");
		int year = Integer.parseInt(date[2]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[0]);

		LocalDate today = LocalDate.of(year, month, day);
		YearMonth ym = YearMonth.from(today);
		YearMonth ymNext = ym.plusMonths(next);
		LocalDate ld = ymNext.atEndOfMonth();
		String output = ld.toString();
		return output.split("-");
	}

	private int comparedate(String currentDate, String dueDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date1 = sdf.parse(dueDate);
			Date date2 = sdf.parse(currentDate);
			return date1.compareTo(date2);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}