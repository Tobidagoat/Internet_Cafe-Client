/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import internet_cafe_client.client;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author USER
 */
public class TimeselectorController implements Initializable {

    @FXML
    private Button btnaddtime;
    @FXML
    private AnchorPane option2;
    @FXML
    private AnchorPane option1;
    @FXML
    private AnchorPane option4;
    @FXML
    private AnchorPane option3;
    
    private client c;
    private AnchorPane selectedPane = null;
    private PrintWriter out;
    private int selectedtime = -1;
    private Stage modalStage;
    Parent root;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void btnaddtimeaction(ActionEvent event) {
        if (selectedtime > 0) {
            c.requestAddTime(selectedtime);
        if (modalStage != null) modalStage.close();}
        else {
            JOptionPane.showMessageDialog(null, "Please select a time before confirming.");
        }
    }
        public void setClient(client c) {
            this.c = c;
    }
    
    @FXML
    void onpress(MouseEvent event) {
        AnchorPane pane = (AnchorPane) event.getSource();
        pane.setScaleX(0.95);
        pane.setScaleY(0.95);
    }

    @FXML
    void onrelease(MouseEvent event) {
        AnchorPane pane = (AnchorPane) event.getSource();
        pane.setScaleX(1);
        pane.setScaleY(1);
    }
    
    @FXML
    private void onclick(MouseEvent event) {
        AnchorPane clickedPane = (AnchorPane) event.getSource();

    if (selectedPane != null) {
        selectedPane.getStyleClass().remove("time-option-clicked");
    }

    clickedPane.getStyleClass().add("time-option-clicked");

    selectedPane = clickedPane;
    
     if (clickedPane == option1) {
        selectedtime = 60*60;
    } else if (clickedPane == option2) {
        selectedtime = 60*120;
    } else if (clickedPane == option3) {
        selectedtime = 60*180;
    } else if (clickedPane == option4) {
        selectedtime = 60*240;
    }

    System.out.println("🕒 Selected time: " + selectedtime + " seconds");
        
    }

    void setModalStage(Stage modalStage) {
        this.modalStage=modalStage;
    }
    
}
