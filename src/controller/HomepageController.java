package controller;

import database.DbConnection;
import internet_cafe_client.client;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    @FXML private Button btnorderfood;
    @FXML private TextField txtsearchbar;
    @FXML private FlowPane gamebox;
    @FXML private FlowPane othersbox;
    
    private client c;
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private int userId;
    private int pcid;
    private String username;
    private String email;
    private String userimage;
    private int orgduration;
    private int addduration;
    private Parent root;
    public int[] count;
    private Timeline timeline;
    private boolean[] reminded = { false };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DbConnection db = new DbConnection();
        try {
            con = db.getConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtsearchbar.textProperty().addListener((obs, oldText, newText) -> searchgames(newText));
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
    private void btnorderfoodaction(ActionEvent event) {
    }

    @FXML
    private void txtsearchaction(ActionEvent event) {
        searchgames(txtsearchbar.getText());
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
        userId = Integer.parseInt(userid.replaceAll("\\[|\\]", ""));
        pcid = Integer.parseInt(pcName.replaceAll("[^0-9]", ""));
        loadgames(packageName);
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
       
        File file = new File("src/img/"+userimage);
        Image image = new Image(file.toURI().toString());
        userprofile.setImage(image);

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
       int packageid=getpackageid(packagename);
       String sql="select * from games where package_id <= ? ";
       pst=con.prepareStatement(sql);
       pst.setInt(1, packageid);
       rs=pst.executeQuery();
       while(rs.next()){
           String gamename=rs.getString("game_name");
           String companyname=rs.getString("game_company");
           String genre=rs.getString("game_genre");
           double rating=rs.getDouble("game_rating");
           String gameimage=rs.getString("game_icon");
           String gamebg=rs.getString("game_thumbnail");
           String desc=rs.getString("game_desc");
           String path=rs.getString("game_exe_path");
           String status=rs.getString("game_status");
           String type=rs.getString("type");
           FXMLLoader loader=new FXMLLoader(getClass().getResource("/view/gamecard.fxml"));
           AnchorPane card=loader.load();
           GamecardController cardcontroller=loader.getController();

           cardcontroller.setdata(gamename,companyname,genre,rating,gameimage,gamebg,desc,path,status);
           if(type.equalsIgnoreCase("games")){
               gamebox.getChildren().add(card);}
           else{
               othersbox.getChildren().add(card);
           }
         
       }
       
   }

    
    public void setClient(client c) {
        this.c = c;
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
        String gettime="Select period from sale_detail where customer_id = ? and pc_id = ? and status_id = 2";
        pst=con.prepareStatement(gettime);
        pst.setInt(1, userId);
        pst.setInt(2, pcid);
        rs=pst.executeQuery();
        if(rs.next()){
            orgduration=rs.getInt("period");
        }
        System.out.println("Original period is "+orgduration);
    }

    public void terminatesession(){
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(this::terminatesession);
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/test.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
            
            Stage stage2 = (Stage) btnaddtime.getScene().getWindow();
            stage2.close();
            
            String sql = "UPDATE sale_detail SET status_id = 3 WHERE customer_id = ? AND pc_id = ? AND status_id = 2";
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setInt(2, pcid);
            pst.executeUpdate();
            String sql2 ="update pcs set status_id=1 where pc_id= ?";
            pst=con.prepareStatement(sql2);
            pst.setInt(1, pcid);
            pst.executeUpdate();
            c.sendToServer("SESSION_END|"+pcid);
        } catch (IOException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
