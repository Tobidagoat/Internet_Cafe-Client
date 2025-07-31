package controller;

import internet_cafe_client.client;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class DefaultController implements Initializable {

    @FXML private Button btnGame;
    @FXML private Button btnFood;
    @FXML private AnchorPane containerPane;

    private client client;
    private HomepageController homepageController;
    private AnchorPane homepagePane;
    private AnchorPane foodPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Preload homepage
            FXMLLoader homepageLoader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
            homepagePane = homepageLoader.load();
            homepageController = homepageLoader.getController();
            homepageController.setClient(client);

            // Optional: Preload food order pane if you want similar behavior
            FXMLLoader foodLoader = new FXMLLoader(getClass().getResource("/view/foodorder.fxml"));
            foodPane = foodLoader.load();

            // Add homepage by default
            containerPane.getChildren().setAll(homepagePane);
            anchorPaneFill(homepagePane);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Optionally trigger the default button if needed
        // btnGame.fire();
    }

    @FXML
    void HandleFoodAction(ActionEvent event) {
        containerPane.getChildren().setAll(foodPane);
        anchorPaneFill(foodPane);
    }

    @FXML
    void HandleGameAction(ActionEvent event) {
        containerPane.getChildren().setAll(homepagePane);
        anchorPaneFill(homepagePane);
    }

    private void anchorPaneFill(AnchorPane pane) {
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }

    public void setClient(client client) {
        this.client = client;
        if (homepageController != null) {
            homepageController.setClient(client);
        }
    }

    public void setSessionData(String pcName, String userid, String room, String packageName, int duration) throws SQLException, IOException {
        if (homepageController != null) {
            homepageController.setSessionData(pcName, userid, room, packageName, duration);
        }
    }

    public void terminateSession() {
        if (homepageController != null) {
            homepageController.terminatesession();
        }
    }

    public void addTime(int extraTime) {
        if (homepageController != null) {
            try {
                homepageController.addTime(extraTime);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
