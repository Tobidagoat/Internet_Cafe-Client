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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class GamemodalController implements Initializable {

    @FXML
    private ImageView imggamebg;
    @FXML
    private ImageView imggame;
    @FXML
    private Label lbgamename;
    @FXML
    private Button btnplay;
    @FXML
    private Label lbdesc;
    @FXML
    private Label lbcompanyname;
    @FXML
    private Label lbcompany;
//    @FXML
//    private Label lbrating;
    @FXML
    private Label lbgenre;
    
    
    String path;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void btnplayaction(ActionEvent event) {
        
        String exePath = path;
        File gameFile = new File(exePath);
        if (gameFile.exists()) {
            try {
                Runtime.getRuntime().exec(exePath);
            } catch (IOException ex) {
                ex.printStackTrace();
                    }
        } else {
                        JOptionPane.showMessageDialog(null, "Game File Not Found.");
        }
    }
    
    public void setdata(String gamename, String companyname, String genre, String gameimage, String gamebg, String desc, String path, String status){
        lbgamename.setText(gamename);
        lbcompany.setText(companyname);
        lbcompanyname.setText(companyname);
        lbgenre.setText(genre);
//        lbrating.setText(String.valueOf(rating));
        lbdesc.setText(desc);
        this.path=path;
        File game = new File("src/img/"+gameimage);
        Image image = new Image(game.toURI().toString());
        imggame.setImage(image);
        
        File bgimg = new File("src/img/"+gamebg);
        Image imagebg = new Image(bgimg.toURI().toString());
        imggamebg.setImage(imagebg);
        
        if(status.equalsIgnoreCase("Not Installed")){
            btnplay.setDisable(true);
        }
    }
}
