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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
    @FXML private FlowPane populargamebox;
    @FXML private FlowPane latestgamebox;
    
    private client clientinstance;
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private int userId;
    private int pcid;
    private String username;
    private String email;
    private String userimage;
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
            controller.setClient(clientinstance);
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
        terminatesession();
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

        populargamebox.getChildren().forEach(node -> {
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

        latestgamebox.getChildren().forEach(node -> {
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

    public void setSessionData(String pcName, String userid, String room, String packageName, int duration) throws SQLException {
        userId = Integer.parseInt(userid.replaceAll("\\[|\\]", ""));
        pcid = Integer.parseInt(pcName.replaceAll("[^0-9]", ""));

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
        System.out.println("The file path is "+file);
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
                try {
                    terminatesession();
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    public void setClient(client c) {
        this.clientinstance = c;
    }

   public void addTime(int extraSeconds) {
    if (count != null && count.length > 0) {
        count[0] += extraSeconds;
        lbtimer.setText(formatTime(count[0]));
        System.out.println("🕒 Added " + extraSeconds + " seconds. New total: " + count[0] + " seconds");

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

    public void terminatesession() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/test.fxml"));
        root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();

        Stage stage2 = (Stage) btnaddtime.getScene().getWindow();
        stage2.close();

        String sql = "UPDATE sale_detail SET status_id = 3 WHERE customer_id = ? AND pc_id = ?";
        pst = con.prepareStatement(sql);
        pst.setInt(1, userId);
        pst.setInt(2, pcid);
        pst.executeUpdate();
    }


}
