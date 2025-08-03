package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RequestReplyController implements Initializable {

    @FXML
    private Label txtReply;
    @FXML
    private Button btnOk;

    private Stage popupStage;
    private String reply;

    private Timeline countdownTimeline;
    private int countdownSeconds = 5;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Do not set text here because reply isn't available yet
    }

    @FXML
    private void HandleOkAction(ActionEvent event) {
        if (popupStage != null) {
            popupStage.close();
        }
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    public void setPopupStage(Stage stage, String reply) {
        this.popupStage = stage;
        this.reply = reply;

        txtReply.setText(reply);
        startCountdown();
    }

    private void startCountdown() {
        btnOk.setText("OK (" + countdownSeconds + ")");

        countdownTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                countdownSeconds--;
                btnOk.setText("OK (" + countdownSeconds + ")");

                if (countdownSeconds <= 0) {
                    if (popupStage != null) {
                        popupStage.close();
                    }
                    countdownTimeline.stop();
                }
            })
        );
        countdownTimeline.setCycleCount(countdownSeconds);
        countdownTimeline.play();
    }
}
