/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import static internet_cafe_client.Internet_Cafe_client.stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class TestController implements Initializable {
    
    Parent root;
    public static Stage adminStage;
    
    
    @FXML
    private Button btnAdminMode;
    
     @FXML
    private AnchorPane containerPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    
      @FXML
    void HandleAdminaAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        AnchorPane newLoadedPane = loader.load();
        
//        popupstage.initModality(Modality.APPLICATION_MODAL);
//        popupstage.setTitle("Admin Mode Log");
//        popupstage.setScene(new Scene(newLoadedPane));
//        popupstage.showAndWait();
        
        
        
        containerPane.getChildren().clear();
        containerPane.getChildren().add(newLoadedPane);
        
        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
//         AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
          AnchorPane.setRightAnchor(newLoadedPane, 0.0);
          
          LoginController controller = loader.getController();
          controller.setmaincontroller(this);
                 
          
          adminStage.initModality(Modality.APPLICATION_MODAL);
          adminStage.setTitle("Admin Mode Log");
          adminStage.setScene(new Scene(containerPane));
          adminStage.showAndWait();
    }
    
    public void paneclose(){
        containerPane.getChildren().clear();
    }
    
    
}
