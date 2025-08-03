package controller;

import internet_cafe_client.client;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DefaultController implements Initializable {

    @FXML private Button btnGame;
    @FXML private Button btnFood;
    @FXML private AnchorPane containerPane;

    private client client;
    private HomepageController homepageController;
    private AnchorPane homepagePane;
    private AnchorPane foodPane;
    private Stage currentStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Preload homepage
            FXMLLoader homepageLoader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
            homepagePane = homepageLoader.load();
            homepageController = homepageLoader.getController();
            homepageController.setParentcontroller(this);
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
    
    
    public void returnToTestScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/test.fxml"));
            Parent testRoot = loader.load();
            
            // Get current stage from any node
            TestController test =loader.getController();
            test.restartMedia();
            currentStage = (Stage) containerPane.getScene().getWindow();
            
            currentStage.setScene(new Scene(testRoot));
           
            
        } catch (IOException ex) {
            System.out.println("Returning to test controller failed badly");
        }
    }
    
    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
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
    
    public void showFloatingModal(String Reply) {
        Platform.runLater(() -> {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RequestReply.fxml"));
            AnchorPane modalPane = loader.load();

            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.setAlwaysOnTop(true);
            popupStage.initModality(Modality.NONE);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(modalPane);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            

            // Set width/height if not fixed in FXML
            double width = 300;
            double height = 150;
            
            // Position bottom-right
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            popupStage.setX(screenBounds.getMaxX() - width - 20);
            popupStage.setY(screenBounds.getMaxY() - height - 40);

            // Give controller access to close its own stage
            RequestReplyController controller = loader.getController();
            controller.setPopupStage(popupStage,Reply);
            
            System.out.println("Showing popup with reply: " + Reply);
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    });
}

}