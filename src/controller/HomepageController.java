package controller;

import database.DbConnection;
import internet_cafe_client.client;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.swing.*;

public class HomepageController implements Initializable {

    @FXML private Label lbemail;
    @FXML private ImageView userprofile;
    @FXML private Label lbusername;
    @FXML private Label lbtimer;
    @FXML private Label lbstarttime;
    @FXML private Label lbendtime;
    @FXML private Label lbroomindicator;
    @FXML private Button btnaddtime;
    @FXML private Button btnendsession;
    
    @FXML private TextField txtsearchbar;
    @FXML private FlowPane gamebox;
    @FXML private FlowPane othersbox;
    
    
    @FXML
    private Button btnEditPfp;
    
    
    @FXML
    private ScrollPane scrollpp;
    
    private client c=client.getInstance();
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private int userId;
    private int pcid;
    private int saleid;
    private String username;
    private String email;
    private String userimage;
    private int orgduration;
    private int addduration;
    private Parent root;
    public int[] count;
    private Timeline timeline;
    private boolean[] reminded = { false };
    
    private String searchg="";
    private String pkname;
    
    private DefaultController defaultController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DbConnection db = new DbConnection();
        try {
            con = db.getConnection();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtsearchbar.textProperty().addListener((obs, oldText, newText) -> searchgames(newText));
        
        
        loadimg(userimage, userprofile);
    }

    @FXML
    private void btnaddtimeaction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/timeselector.fxml"));
            Parent modalRoot = loader.load();
            TimeselectorController controller=loader.getController();
            controller.setClient(c);
            Stage modalStage = new Stage();
            modalStage.setTitle("Add Time");
            modalStage.setScene(new Scene(modalRoot));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.show();

            TimeselectorController timemodal = loader.getController();
            timemodal.setModalStage(modalStage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnendsessionaction(ActionEvent event) throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Warning");
        alert.setContentText("U can't Log In Back! Are you sure you want to exit?");
        
        Optional<ButtonType> result = alert.showAndWait();
        boolean accepted= result.isPresent() && result.get() == ButtonType.OK;
        
            if (accepted) {
                          terminatesession();
                    }
    }

    
    @FXML
    void HandleEditPfpAction(ActionEvent event) throws IOException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Game Icon Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png","*.jpg","*.jpeg"));
        Stage stage = (Stage)btnEditPfp.getScene().getWindow();
        File selectedFile  = fileChooser.showOpenDialog(stage);
        
        
           // FOR TESTING NEED TO CHANGE AFTER COMPILE
            Path targetDir = Paths.get("D:/Internet_cafe_2.0/Internet_Cafe-Client/src/img");
            
            //USE AFTER COMPILE AS JAR FR
//            Path targetDir = Paths.get(System.getProperty("user.dir"), "img");

            Files.createDirectories(targetDir);
            
            //COPY IMAGE INTO /img
            Path target = targetDir.resolve(selectedFile.getName());
            Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            
            String newpic = selectedFile.getName();
            String sql = "Update users set profile_pic = ? where customer_id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newpic);
            pst.setInt(2, userId);
            pst.executeUpdate();
            
            userprofile.setImage(null);
            loadimg(newpic, userprofile);
            
        
    }

    @FXML
    private void txtsearchaction(ActionEvent event) throws SQLException, IOException {
        scrollpp.setVvalue(0);
        searchg = txtsearchbar.getText();
        loadgames(pkname);
        
    }

    private void searchgames(String queryText) {
        final String query = queryText.toLowerCase();

        gamebox.getChildren().forEach(node -> {
            if (node instanceof AnchorPane) {
                AnchorPane gameCard = (AnchorPane) node;
                Label label = (Label) gameCard.lookup("#gamelabel"); // Make sure label inside game card has fx:id="gamelabel"
                if (label != null) {
                    String gameName = label.getText().toLowerCase();
                    gameCard.setVisible(gameName.contains(query));
                    gameCard.setManaged(gameName.contains(query));
                }
            }
        });

        othersbox.getChildren().forEach(node -> {
            if (node instanceof AnchorPane) {
                AnchorPane gameCard = (AnchorPane) node;
                Label label = (Label) gameCard.lookup("#gamelabel");
                if (label != null) {
                    String gameName = label.getText().toLowerCase();
                    gameCard.setVisible(gameName.contains(query));
                    gameCard.setManaged(gameName.contains(query));
                }
            }
        });
    }

    public void setSessionData(String pcName, String userid, String room, String packageName, int duration) throws SQLException, IOException {
        this.pkname = packageName;
        userId = Integer.parseInt(userid.replaceAll("\\[|\\]", ""));
        pcid = Integer.parseInt(pcName.replaceAll("[^0-9]", ""));
        loadgames(packageName);
        getsaleid();
        String sql = "SELECT * FROM users WHERE customer_id = ?";
        pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        rs = pst.executeQuery();

        while (rs.next()) {
            username = rs.getString("customer_name");
            email = rs.getString("e_mail");
            userimage = rs.getString("profile_pic");
        }

        lbemail.setText(email);
        lbusername.setText(username);
       
//        File file = new File("src/img/"+userimage);
//        Image image = new Image(file.toURI().toString());
//        userprofile.setImage(image);
 
        loadimg(userimage, userprofile);
        lbroomindicator.setText(room);
        startCountdown(duration);

        LocalTime now = LocalTime.now();
        LocalTime end = now.plusSeconds(duration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");

        lbstarttime.setText(now.format(formatter));
        lbendtime.setText(end.format(formatter));
    }

    private void startCountdown(int seconds) {
        count = new int[]{ seconds };
        reminded[0] = false;

        lbtimer.setText(formatTime(count[0]));

        if (timeline != null) timeline.stop();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            count[0]--;
            lbtimer.setText(formatTime(count[0]));

            if (!reminded[0] && count[0] == 300) {
                reminded[0] = true;
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "Only 5 minutes left!", "Reminder", JOptionPane.WARNING_MESSAGE));
            }

            if (count[0] <= 0) {
                lbtimer.setText("Done!");
                timeline.stop();
                terminatesession();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
   public void loadgames(String packagename) throws SQLException, IOException{
       
       gamebox.getChildren().clear();
       othersbox.getChildren().clear();
       int packageid=getpackageid(packagename);
      if(searchg.isEmpty()){
           String sql="select * from games where package_id <= ? ";
            pst=con.prepareStatement(sql);
            pst.setInt(1, packageid);
            rs=pst.executeQuery();
      }else{
          String sql = "select * from games where package_id <= ? && game_name like ? ";
          pst=con.prepareStatement(sql);
          pst.setInt(1, packageid);
          pst.setString(2, "%"+ searchg+"%");
          rs=pst.executeQuery();
      }
      
       while(rs.next()){
           String gamename=rs.getString("game_name");
           String companyname=rs.getString("game_company");
           String genre=rs.getString("game_genre");
//           double rating=rs.getDouble("game_rating");
           String gameimage=rs.getString("game_icon");
           String gamebg=rs.getString("game_thumbnail");
           String desc=rs.getString("game_desc");
           String path=rs.getString("game_exe_path");
           String status=rs.getString("game_status");
           String type=rs.getString("type");
           FXMLLoader loader=new FXMLLoader(getClass().getResource("/view/gamecard.fxml"));
           AnchorPane card=loader.load();
           GamecardController cardcontroller=loader.getController();

           cardcontroller.setdata(gamename,companyname,genre,gameimage,gamebg,desc,path,status);
           if(type.equalsIgnoreCase("games")){
               gamebox.getChildren().add(card);}
           else{
               othersbox.getChildren().add(card);
           }
         
       }
       
   }

    
    public void setClient(client c) {
        this.c=client.getInstance();
        
    }
    
    private int getpackageid(String packagename) throws SQLException{
        int packageid=-1;
        String sql="select package_id from package where package_type= ?";
         pst=con.prepareStatement(sql);
        pst.setString(1, packagename);
        rs = pst.executeQuery();

        if (rs.next()) {
            packageid = rs.getInt("package_id");
        }
    return packageid;
    }

   public void addTime(int extraSeconds) throws SQLException {
    if (count != null && count.length > 0) {
        count[0] += extraSeconds;
        lbtimer.setText(formatTime(count[0]));
        System.out.println("🕒 Added " + extraSeconds + " seconds. New total: " + count[0] + " seconds");
        
        addduration=converttohour(extraSeconds);
        getperiod();
        String sql="UPDATE sale_detail SET period = ? WHERE customer_id = ? AND pc_id = ? AND Status_id = 2";
        pst=con.prepareStatement(sql);
        pst.setInt(1, orgduration+addduration);
        pst.setInt(2, userId);
        pst.setInt(3, pcid);
        pst.executeUpdate();
        // Update the end time label based on new time
        LocalTime now = LocalTime.now();
        LocalTime newEndTime = now.plusSeconds(count[0]);
        lbendtime.setText(newEndTime.format(DateTimeFormatter.ofPattern("hh:mm")));
    } else {
        System.out.println("⚠️ Timer was not running.");
    }
}



    private String formatTime(int seconds) {
        int hrs = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hrs, mins, secs);
    }
    
    public int converttohour(int seconds) {
        return seconds / 3600;
    }
    
    private void getperiod() throws SQLException{
        String gettime="Select period,sale_id from sale_detail where customer_id = ? and pc_id = ? and status_id = 2";
        pst=con.prepareStatement(gettime);
        pst.setInt(1, userId);
        pst.setInt(2, pcid);
        rs=pst.executeQuery();
        if(rs.next()){
            orgduration=rs.getInt("period");
            saleid=rs.getInt("sale_id");
        }
        System.out.println("Original period is "+orgduration);
    }
    
    private void getsaleid() throws SQLException{
        String getsaleid="Select sale_id from sale_detail where customer_id = ? and pc_id = ? and status_id = 2";
        pst=con.prepareStatement(getsaleid);
        pst.setInt(1, userId);
        pst.setInt(2, pcid);
        rs=pst.executeQuery();
        if(rs.next()){
            saleid=rs.getInt("sale_id");
        }
    }
    
        public void terminatesession(){
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::terminatesession);
                return;
            }

            String updatesql = "UPDATE sale_detail SET status_id = 3 WHERE customer_id = ? AND pc_id = ? AND status_id = 2";
            pst = con.prepareStatement(updatesql);
            pst.setInt(1, userId);
            pst.setInt(2, pcid);
            pst.executeUpdate();
            String sql2 ="update pcs set status_id = 1 where pc_id= ?";
            pst=con.prepareStatement(sql2);
            pst.setInt(1, pcid);
            pst.executeUpdate();
            c.sendToServer("SESSION_END|"+pcid+"|"+saleid);
            
            if (defaultController != null) {
                defaultController.returnToTestScreen();
            }
        } catch (SQLException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void loadimg(String imgName, ImageView imageview) {
        
              // FOR TESTING NEED TO CHANGE AFTER COMPILE
            Path targetDir = Paths.get("D:/Internet_cafe_2.0/Internet_Cafe-Client/src/img");
            
            //USE AFTER COMPILE AS JAR FR
//            Path targetDir = Paths.get(System.getProperty("user.dir"), "img");
        
    // Apply circular clip
    
     Circle clip = new Circle(
        imageview.getFitWidth() / 2,
        imageview.getFitHeight() / 2,
        imageview.getFitWidth() / 2
    );

    imageview.setClip(clip);

    // Defensive check for null or empty imgName
    if (imgName == null || imgName.trim().isEmpty()) {
        setDefaultImage(imageview);
        return;
    }

    // File path setup
    File file = new File(targetDir + File.separator + imgName);

    if (file.exists()) {
        Image image = new Image(
            file.toURI().toString(),
            550, 550, true, true, true
        );
        imageview.setImage(image);
    } else {
        setDefaultImage(imageview);
    }

    imageview.setFitWidth(120);
    imageview.setFitHeight(120);
    imageview.setPreserveRatio(false); // force full fit

    
    imageview.setSmooth(true);
    imageview.setCache(true);
}
    
     private void setDefaultImage(ImageView imageview) {
    try {
        // Use resource fallback or a static file path as backup
        Image defaultImg = new Image(getClass().getResourceAsStream("/img/Default_user.png"));
        imageview.setImage(defaultImg);
    } catch (Exception e) {
        System.out.println("Default image not found: " + e.getMessage());
    }
}
     
     public void setParentcontroller(DefaultController defaultController) {
        this.defaultController = defaultController;
    }
}