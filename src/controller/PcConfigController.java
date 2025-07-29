/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import internet_cafe_client.ConfigManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class PcConfigController implements Initializable, AdminModeControllerAware {

    @FXML
    private TextField txtPcid;
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPort;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;
    
    private AdminModeController Admincontroller;
    
    

   private ConfigManager config = new ConfigManager();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    txtPcid.setText(config.getPcId());
    txtHost.setText(config.getHost());
    txtPort.setText(String.valueOf(config.getPort()));
    }    

    @FXML
    private void HandleCancelAction(ActionEvent event) {
         Admincontroller.closepane();
    }

    @FXML
    private void HandleSaveAction(ActionEvent event) {
        
    String pcId = txtPcid.getText();
    String host = txtHost.getText();
    int port = Integer.parseInt(txtPort.getText());

    config.saveConfig(pcId, host, port);
    System.out.println("💾 Configuration saved!");
    
    Admincontroller.closepane();
    }
    
     public void setMainController(AdminModeController controller){
        this.Admincontroller = controller;
    }
    
}
