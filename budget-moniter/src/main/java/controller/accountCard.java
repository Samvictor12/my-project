package controller;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import utils.constance;

public class accountCard {
    @FXML
    private Text accountBalance;

    @FXML
    private ImageView accountImage;

    @FXML
    private Text accountName;
    
    public void setData(Map<String, String> record) {
    	accountImage.setImage(constance.getImage(record.get("accountImage")));
    	accountBalance.setText(constance.currencyFormat(record.get("accountBalance")));
    	accountName.setText(record.get("accountName").toUpperCase());
	}
}
