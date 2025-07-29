/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class GamecardController implements Initializable {

     @FXML
    private ImageView imggame;

    @FXML
    private Label lbcompany;

    @FXML
    private Label lbgamename;

    @FXML
    private Label lbgametype;
    
    String gamename;
    String companyname;
    String genre;
    double rating;
    String gameimage;
    String gamebg;
    String desc;
    String path;
    String status;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    void playaction(MouseEvent event) throws IOException {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/view/gamemodal.fxml"));    
        AnchorPane popup=loader.load();
        GamemodalController controller=loader.getController();
        controller.setdata(gamename,companyname,genre,gameimage,gamebg,desc,path,status);
        Stage popupstage=new Stage();
        popupstage.initModality(Modality.APPLICATION_MODAL);
        popupstage.setTitle("Select Package");
        popupstage.setScene(new Scene(popup));
        popupstage.showAndWait();
    }

    void setdata(String gamename, String companyname, String genre, String gameimage, String gamebg, String desc, String path, String status) {
        this.gamename=gamename;
        this.companyname=companyname;
        this.genre=genre;
//        this.rating=rating;
        this.gameimage=gameimage;
        this.gamebg=gamebg;
        this.desc=desc;
        this.path=path;
        this.status=status;
        lbgamename.setText(gamename);
        lbcompany.setText(companyname);
        lbgametype.setText(genre);
        File file = new File("src/img/"+gameimage);
        Image image = new Image(file.toURI().toString());
        imggame.setImage(image);
    }
    
}
