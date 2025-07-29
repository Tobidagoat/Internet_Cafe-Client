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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class EditGamesController implements Initializable ,AdminModeControllerAware {

    @FXML
    private TextField txtName;
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
    private ContextMenu suggestionMenu;
    @FXML
    private AnchorPane editGameRootPane;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    private String Gname;
    private String path;
    private String profile;
    private String banner;
    File selectedFile1 ;
   
    File selectedFile2 ;
    
    private AdminModeController Admincontroller;
    
     @FXML
    private Label txtbannererror;

    @FXML
    private Label txtpatherror;

    @FXML
    private Label txtprofileerror;
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       DbConnection db=new DbConnection();
        try {
            con=db.getConnection();
        } catch (ClassNotFoundException ex) {
            
        }
        
        txtName.textProperty().addListener((obs,oldText,newText)->{
            if(newText == null || newText.isEmpty()){
                suggestionMenu.hide();
            }
                else{
                try {
                    List<String> results =fetchSuggestions(newText);
                    if (results.isEmpty()) {
                suggestionMenu.hide();
            } else {
                suggestionMenu.getItems().clear();
                for (String suggestion : results) {
                    MenuItem item = new MenuItem(suggestion);
                    item.setOnAction(e -> {
                        txtName.setText(suggestion);
                        suggestionMenu.hide();
                        try {
                            fillOtherInfo(suggestion); // <--- fetch and fill details
                        } catch (SQLException ex) {
                            System.out.println("idk just error check");
                        }
                    });
                    suggestionMenu.getItems().add(item);
                }

                if (!suggestionMenu.isShowing()) {
                    suggestionMenu.show(txtName,
                        txtName.localToScreen(txtName.getBoundsInLocal()).getMinX(),
                        txtName.localToScreen(txtName.getBoundsInLocal()).getMaxY());
                }
            }
        

                } catch (SQLException ex) {
                    System.out.println("fetch error");
                }

                
            }
        });
    }    

    @FXML
private void HandleSaveAction(ActionEvent event) throws SQLException, IOException {
    boolean isValid = true;

    if (Gname == null) {
        isValid = false;
    }

    if (path == null || path.trim().isEmpty()) {
        txtpatherror.setText("Path cannot be empty!");
        isValid = false;
    } else {
        txtpatherror.setText("");
    }

    if (profile == null || profile.trim().isEmpty()) {
        txtprofileerror.setText("Profile cannot be empty!");
        isValid = false;
    } else {
        txtprofileerror.setText("");
    }

    if (banner == null || banner.trim().isEmpty()) {
        txtbannererror.setText("Banner cannot be empty!");
        isValid = false;
    } else {
        txtbannererror.setText("");
    }

    if (isValid) {
        String sql = "UPDATE games SET game_exe_path = ?, game_icon = ?, game_thumbnail = ? WHERE game_name = ?";
        pst = con.prepareStatement(sql);
        pst.setString(1, path);
        pst.setString(2, profile);
        pst.setString(3, banner);
        pst.setString(4, txtName.getText());

        pst.executeUpdate();

        Path targetDir = Paths.get("D:/Internet_cafe_2.0/Internet_Cafe-Client/src/img");
        // Use after compile as JAR
        // Path targetDir = Paths.get(System.getProperty("user.dir"), "img");

        Files.createDirectories(targetDir);

        // Copy profile image only if a new one was selected
        if (selectedFile1 != null) {
            Path target = targetDir.resolve(selectedFile1.getName());
            Files.copy(selectedFile1.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Profile image copied to: " + target);
        }

        // Copy banner image only if a new one was selected
        if (selectedFile2 != null) {
            Path target2 = targetDir.resolve(selectedFile2.getName());
            Files.copy(selectedFile2.toPath(), target2, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Banner image copied to: " + target2);
        }
    }
    
      Admincontroller.closepane();
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
        path = txtPath.getText();
    }

    @FXML
    private void HandleProfileAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Icon Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnProfile.getScene().getWindow();
         selectedFile1  = fileChooser.showOpenDialog(stage);
        if(selectedFile1 !=null){
            txtProfile.setText(selectedFile1.getName());
            

        }
        profile =txtProfile.getText();
    }

    @FXML
    private void HandleBannerAction(ActionEvent event) throws IOException {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Banner Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnBanner.getScene().getWindow();
         selectedFile2  = fileChooser.showOpenDialog(stage);
        if(selectedFile2 !=null){
            txtBanner.setText(selectedFile2.getName());
            

        banner = txtBanner.getText();
    }
    }
    @FXML
     void HandleAddGamesAction(ActionEvent event) throws IOException  {
         

        Admincontroller.showAddGames();
        
    }
    
    
    private List<String> fetchSuggestions(String query) throws SQLException {
    List<String> suggestions = new ArrayList<>();
    String sql = "SELECT game_name FROM games WHERE game_name LIKE ? LIMIT 3";

    pst = con.prepareStatement(sql);
    pst.setString(1, "%" + query + "%");
    rs = pst.executeQuery();
        while (rs.next()) {
            suggestions.add(rs.getString("game_name"));
        }
   
    return suggestions;
}
    
    private void fillOtherInfo(String name) throws SQLException {
        
        
    String sql = "SELECT game_exe_path , game_icon, game_thumbnail FROM games WHERE game_name = ? LIMIT 1";

     pst = con.prepareStatement(sql);
    pst.setString(1, name );
    rs = pst.executeQuery();

        if (rs.next()) {

            txtPath.setText(rs.getString("game_exe_path"));
            txtProfile.setText(rs.getString("game_icon"));
            txtBanner.setText(rs.getString("game_thumbnail"));


            Gname = txtName.getText();
            path  = txtPath.getText();
            profile = txtProfile.getText();
            banner = txtBanner.getText();
              
        }
    
}
    
    public void setMainController(AdminModeController controller){
        this.Admincontroller = controller;
    }

    
    
     }
