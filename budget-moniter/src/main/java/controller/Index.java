package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import com.gluonhq.charm.glisten.mvc.View;

import Backend.Operations;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import utils.DB_controller;
import utils.constance;

public class Index implements Initializable {

    @FXML
    private HBox accountSection;

    @FXML
    private ImageView budgetTab;

    @FXML
    private ImageView dashBoardTab;

    @FXML
    private ImageView menu;

    @FXML
    private Text profileName;

    @FXML
    private Text profileName1;

    @FXML
    private Circle profilePic;

    @FXML
    private ImageView profileTab;

    @FXML
    private ImageView statementTab;

    @FXML
    private View view;
    
    Operations db;
    @FXML
    void clickdashBoardTab(MouseEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		db = new Operations();
//		db.addAccount("fi", "accountImg/Fi.jpg", "4000");
		accountCard();
	}
	
	public void accountCard() {
		Map<String, Map<String, String>> account = db.getAccount();
		for (String record:account.keySet()) {
			FXMLLoader loader = constance.getComponent("accountCard");
			try {
				VBox input = loader.load();
				accountCard card = loader.getController();
				card.setData(account.get(record));
				accountSection.getChildren().add(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}