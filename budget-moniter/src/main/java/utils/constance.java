package utils;

import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class constance {
	public static final String CurrentLocation = System.getProperty("user.dir"); 
	public static final String resourceLocation = System.getProperty("user.dir")+"\\src\\main\\resources\\";
	
	public static Image getImage(String Location) {
		return new Image(new constance().getClass().getResourceAsStream("/img/"+Location));
	}
	
	public static boolean hasSpecialCharater(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isDigit(input.charAt(i))&&!Character.isLetter(input.charAt(i))&&!Character.isWhitespace(input.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static String GetDataTime() {
		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH/mm/ss-EEEE");  
		   LocalDateTime now = LocalDateTime.now();  
		   String date = dtf.format(now);
		return date;
	}
	
	public static String currencyFormat(String amount) {
		amount = amount.replace(".", "-");
		String[] split = amount.split("-");
		amount = split[0];
	    StringBuilder stringBuilder = new StringBuilder();
	    char amountArray[] = amount.toCharArray();
	    int a = 0, b = 0;
	    for (int i = amountArray.length - 1; i >= 0; i--) {
	        if (a < 3) {
	            stringBuilder.append(amountArray[i]);
	            a++;
	        } else if (b < 2) {
	            if (b == 0) {
	                stringBuilder.append(",");
	                stringBuilder.append(amountArray[i]);
	                b++;
	            } else {
	                stringBuilder.append(amountArray[i]);
	                b = 0;
	            }
	        }
	    }
	    return "₹ "+stringBuilder.reverse().toString()+"."+split[1];
	}
	
	public static FXMLLoader getComponent(String fileName) {
		return new FXMLLoader(new constance().getClass().getResource("/UI/components/"+fileName+".fxml"));
	}
}
