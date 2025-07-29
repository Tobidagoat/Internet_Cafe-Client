/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import database.DbConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class AddGamesController implements Initializable, AdminModeControllerAware {

    @FXML
    private AnchorPane editGameRootPane;
    @FXML
    private TextField txtName;
    @FXML
    private ContextMenu suggestionMenu;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField txtPath;
    @FXML
    private TextField txtProfile;
    @FXML
    private TextField txtBanner;
    @FXML
    private Button btnPath;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnBanner;
    @FXML
    private Label txtpatherror;
    @FXML
    private Label txtprofileerror;
    @FXML
    private Label txtbannererror;
    @FXML
    private Button btnEdit;
    @FXML
    private ComboBox<Integer> cbPid;

    @FXML
    private ComboBox<String> cbType;
    
    @FXML
    private TextField txtCompany;

    @FXML
    private TextArea txtDes;

    @FXML
    private TextField txtGenre;
     @FXML
    private Label txtcompanyerror;

    @FXML
    private Label txtdeserror;

    @FXML
    private Label txtgenreerror;



    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    
    File selectedFile1 ;
   
    File selectedFile2 ;
    
    private AdminModeController Admincontroller;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        DbConnection db=new DbConnection();
        try {
            con=db.getConnection();
        } catch (ClassNotFoundException ex) {
            
        }
        cbPid.getItems().addAll(1, 2, 3, 4);
        cbType.getItems().addAll("games", "others");
        cbPid.getSelectionModel().selectFirst();
        cbType.getSelectionModel().selectFirst();
    }    

    @FXML
    private void HandleSaveAction(ActionEvent event) throws SQLException, IOException {
        
                 boolean isValid  = true;
         
         if(txtName.getText().isEmpty()){
             isValid = false;
         }
        if (txtPath.getText().isEmpty() || txtPath.getText().trim().isEmpty()) {
        txtpatherror.setText("Path cannot be empty!");
        isValid = false;
    }
        else{
            txtpatherror.setText("");
        }
    if (txtProfile.getText().isEmpty() || txtProfile.getText().trim().isEmpty()) {
        txtprofileerror.setText("Profile cannot be empty!");
        isValid = false;
    }else{
        txtprofileerror.setText("");
    }
    if (txtBanner.getText().isEmpty() || txtBanner.getText().trim().isEmpty()) {
        txtbannererror.setText("Banner cannot be empty!");
        isValid = false;
    }else{
        txtbannererror.setText("");
    }
    if (txtCompany.getText().isEmpty() || txtCompany.getText().trim().isEmpty()) {
        txtcompanyerror.setText("Company cannot be empty!");
        isValid = false;
    }else{
        txtcompanyerror.setText("");
    }
    if (txtGenre.getText().isEmpty() || txtGenre.getText().trim().isEmpty()) {
        txtgenreerror.setText("Genre cannot be empty!");
        isValid = false;
    }else{
        txtgenreerror.setText("");
    }
    if (txtDes.getText().isEmpty() || txtDes.getText().trim().isEmpty()) {
        txtdeserror.setText("Description cannot be empty!");
        isValid = false;
    }else{
        txtdeserror.setText("");
    }
    
    
   
    
    
    



    // If everything is valid, do something
    if (isValid) {
         String sql = "insert into games(game_name,game_company,game_genre,game_icon, game_thumbnail,game_desc,game_exe_path,package_id,type) values (?,?,?,?,?,?,?,?,?);";
         
        pst = con.prepareStatement(sql);
        pst.setString(1, txtName.getText());
        pst.setString(2, txtCompany.getText());
        pst.setString(3, txtGenre.getText());
        
       
        pst.setString(4, txtProfile.getText());
        pst.setString(5, txtBanner.getText());
        pst.setString(6, txtDes.getText());
         pst.setString(7, txtPath.getText());
         int id = cbPid.getValue();;
          pst.setInt(8, id);
         String type = cbType.getValue();
         pst.setString(9, type);
         
        
        
        pst.executeUpdate();
        
        //Profile Update
                    
            
            // FOR TESTING NEED TO CHANGE AFTER COMPILE
            Path targetDir = Paths.get("D:/Internet_cafe_2.0/Internet_Cafe-Client/src/img");
            
            //USE AFTER COMPILE AS JAR FR
//            Path targetDir = Paths.get(System.getProperty("user.dir"), "img");

            Files.createDirectories(targetDir);
            
            //COPY IMAGE INTO /img
            Path target = targetDir.resolve(selectedFile1.getName());
            Files.copy(selectedFile1.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            
            
            
            System.out.println("Image copied to :"+ target);
            
            
          //Banner Update
                     
            Files.createDirectories(targetDir);
            
            Path target2 = targetDir.resolve(selectedFile2.getName());
            Files.copy(selectedFile2.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            
            
            
            System.out.println("Image copied to :"+ target2);
            
            
              Admincontroller.closepane();
        
            
        
        
    }

    
    }

    @FXML
    private void HandleCancelAction(ActionEvent event) {
        Admincontroller.closepane();
    }

    @FXML
    private void HandlePathAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Executable");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable Files", "*.exe"));
        Stage stage = (Stage)btnPath.getScene().getWindow();
         File  selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile !=null){
            txtPath.setText(selectedFile.getAbsolutePath());
        }
        
    
        
    }

    @FXML
    private void HandleProfileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Icon Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnProfile.getScene().getWindow();
         selectedFile1  = fileChooser.showOpenDialog(stage);
        if(selectedFile1 !=null){
            txtProfile.setText(selectedFile1.getName());
            

        }
        
    }

    @FXML
    private void HandleBannerAction(ActionEvent event) {
         FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Banner Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnBanner.getScene().getWindow();
         selectedFile2  = fileChooser.showOpenDialog(stage);
        if(selectedFile2 !=null){
            txtBanner.setText(selectedFile2.getName());
            

        
    }
    }

    @FXML
    private void HandleEditAction(ActionEvent event) throws IOException {
         
       Admincontroller.showEditGames();
    }
    
    public void setMainController(AdminModeController controller){
        this.Admincontroller = controller;
    }
}
