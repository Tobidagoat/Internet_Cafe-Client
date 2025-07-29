/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class AdminModeController implements Initializable {

    @FXML
    private Button btnEditGames;
    @FXML
    private Button btnEditPc;
    
    @FXML
    private AnchorPane containerpane;
    
    @FXML
    private Button btnClose;
    @FXML
    private Button btnImgInput;
    
    AdminModeController adcontroller= this;
    
    LoginController controller;
    
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void HandleEditGamesAction(ActionEvent event) throws IOException {
        
        loadPane("/view/EditGames.fxml");
        
        
    }

    @FXML
    private void HandleEditPcAction(ActionEvent event) throws IOException {
        loadPane("/view/PcConfig.fxml");
    }
    
    @FXML
    void HandleCloseAction(ActionEvent event) {

        controller.closepane();
    }
    @FXML
    void HandleImgInputAction(ActionEvent event) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Icon Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnImgInput.getScene().getWindow();
        File selectedFile1  = fileChooser.showOpenDialog(stage);
        if (selectedFile1 != null) {
            Path targetDir = Paths.get("D:/Internet_cafe_2.0/Internet_Cafe-Client/src/img");
            
             // Path targetDir = Paths.get(System.getProperty("user.dir"), "img");
             
            Path target = targetDir.resolve(selectedFile1.getName());
            Files.copy(selectedFile1.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Profile image copied to: " + target);
        }
    }
    
    
   public <T> T loadPane(String path) throws IOException {
        containerpane.setMouseTransparent(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        AnchorPane newLoadedPane = loader.load();
        containerpane.getChildren().setAll(newLoadedPane);

        AnchorPane.setTopAnchor(newLoadedPane, 0.0);
        AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
        
        AnchorPane.setRightAnchor(newLoadedPane, 0.0);

        T controller = loader.getController();
        if (controller instanceof AdminModeControllerAware) {
            ((AdminModeControllerAware) controller).setMainController(this);
        }

        return controller;
    }

    public void showEditGames() throws IOException {
        loadPane("/view/EditGames.fxml");
    }

    public void showAddGames() throws IOException {
        loadPane("/view/AddGames.fxml");
    }
    
    public void closepane(){
        containerpane.getChildren().clear();
        containerpane.setMouseTransparent(true);
        
    }
    
    public void setmaincontroller (LoginController loginController){
        this.controller = loginController;
    }

    
    
}

