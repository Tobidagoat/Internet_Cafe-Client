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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnGame.fire();
    } 
    
    @FXML
    void HandleFoodAction(ActionEvent event) throws IOException {
        loadUI("/view/foodorder.fxml");
    }

    @FXML
    void HandleGameAction(ActionEvent event) throws IOException {
        loadHomepage();
    }
    
    private void loadHomepage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
        AnchorPane newLoadedPane = loader.load();
        containerPane.getChildren().clear();
        containerPane.getChildren().add(newLoadedPane);
        
        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
        AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
        AnchorPane.setRightAnchor(newLoadedPane, 0.0);
        
        homepageController = loader.getController();
        homepageController.setClient(client);
    }
    
    private void loadUI(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        AnchorPane newLoadedPane = loader.load();
        containerPane.getChildren().clear();
        containerPane.getChildren().add(newLoadedPane);
        
        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
        AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
        AnchorPane.setRightAnchor(newLoadedPane, 0.0);
    }
    
    public void setClient(client client) {
        this.client = client;
    }
    
    public void setSessionData(String pcName, String userid, String room, String packageName, int duration) throws SQLException, IOException {
        // Forward session data to homepage controller if it exists
        if (homepageController != null) {
            homepageController.setSessionData(pcName, userid, room, packageName, duration);
        } else {
            // If homepage isn't loaded yet, load it first
            loadHomepage();
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