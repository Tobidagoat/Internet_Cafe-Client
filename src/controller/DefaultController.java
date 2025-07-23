/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class DefaultController implements Initializable {

    @FXML
    private Button btnGame;
    @FXML
    private Button btnFood;
    @FXML
    private AnchorPane containerPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        try {
////            loadUI("/view/homepage.fxml");
//
//            
//            
//            // TODO
//        } catch (IOException ex) {
//            Logger.getLogger(DefaultController.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("error loading homepage in init");
//                    
//                    
//                    
//        }

btnGame.fire();
    } 
     @FXML
    void HandleFoodAction(ActionEvent event) throws IOException {
         loadUI("/view/foodorder.fxml");
         

    }

    @FXML
    void HandleGameAction(ActionEvent event) throws IOException, SQLException {
//        loadUI("/view/homepage.fxml");
        
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
        AnchorPane newLoadedPane = loader.load();
        containerPane.getChildren().clear();
        containerPane.getChildren().add(newLoadedPane);
        
        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
         AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
          AnchorPane.setRightAnchor(newLoadedPane, 0.0);
         HomepageController controller = loader.getController();
        controller.setSessionData("pc3", "1", "Room A", "Silver", 1800); 

    }
    
    
    
    public void loadUI(String fxmlPath) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        AnchorPane newLoadedPane = loader.load();
        containerPane.getChildren().clear();
        containerPane.getChildren().add(newLoadedPane);
        
        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
         AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
          AnchorPane.setRightAnchor(newLoadedPane, 0.0);
        
        
    }
    
}
