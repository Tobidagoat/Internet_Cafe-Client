    /*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
     */
    package controller;

    import static internet_cafe_client.Internet_Cafe_client.stage;
    import internet_cafe_client.client;
    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.media.Media;
    import javafx.scene.media.MediaPlayer;
    import javafx.scene.media.MediaView;
    import javafx.stage.Modality;
    import javafx.stage.Stage;
    import javafx.util.Duration;

    /**
     * FXML Controller class
     *
     * @author USER
     */
    public class TestController implements Initializable {

        Parent root;
        public static Stage adminStage;
        public Stage mainstage=internet_cafe_client.Internet_Cafe_client.stage;
        private MediaPlayer mediaPlayer;


        @FXML
        private Button btnAdminMode;

         @FXML
        private AnchorPane containerPane;
        @FXML
        private MediaView bgvideo;
        @FXML
        private Button btnclose;

        @Override
        public void initialize(URL url, ResourceBundle rb) {

            initializeMediaPlayer();

        }  

        private void initializeMediaPlayer() {
    if (mediaPlayer != null) {
        mediaPlayer.stop();
        mediaPlayer.dispose();
    }

    URL videoURL = getClass().getResource("/img/bg_video.mp4");
    if (videoURL == null) {
        System.err.println("Video file not found!");
        return;
    }

    String videoPath = videoURL.toExternalForm();
    Media media = new Media(videoPath);

    mediaPlayer = new MediaPlayer(media);
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    mediaPlayer.setMute(true);

    bgvideo.setMediaPlayer(mediaPlayer);
    bgvideo.setPreserveRatio(false);
    bgvideo.setSmooth(true);
    bgvideo.setCache(true);
//    bgvideo.fitWidthProperty().bind(containerPane.widthProperty());
//    bgvideo.fitHeightProperty().bind(containerPane.heightProperty());

    mediaPlayer.setOnReady(() -> {
        System.out.println("Media ready: Duration = " + media.getDuration());
        Platform.runLater(() -> mediaPlayer.play());
    });

    mediaPlayer.setOnError(() -> {
        System.err.println("MediaPlayer Error: " + mediaPlayer.getError().getMessage());
        // Retry once
        Platform.runLater(() -> initializeMediaPlayer());
    });
}



        public void cleanup() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        }

        public void restartMedia() {
            Platform.runLater(() -> {
                if (bgvideo.getMediaPlayer() == null || bgvideo.getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
                    initializeMediaPlayer();
                }
            });
        }

          @FXML
        void HandleAdminaAction(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            AnchorPane newLoadedPane = loader.load();

    //        popupstage.initModality(Modality.APPLICATION_MODAL);
    //        popupstage.setTitle("Admin Mode Log");
    //        popupstage.setScene(new Scene(newLoadedPane));
    //        popupstage.showAndWait();



            containerPane.getChildren().clear();
            containerPane.getChildren().add(newLoadedPane);

            AnchorPane.setTopAnchor(newLoadedPane, 0.0);
            AnchorPane.setLeftAnchor(newLoadedPane, 0.0);
    //         AnchorPane.setBottomAnchor(newLoadedPane, 0.0);
              AnchorPane.setRightAnchor(newLoadedPane, 0.0);

              LoginController controller = loader.getController();
              controller.setmaincontroller(this);


              adminStage.initModality(Modality.APPLICATION_MODAL);
              adminStage.setTitle("Admin Mode Log");
              adminStage.setScene(new Scene(containerPane));
              adminStage.showAndWait();
        }

        public void paneclose(){
            containerPane.getChildren().clear();
        }

        @FXML
        private void btncloseaction(ActionEvent event) {
            cleanup();
            mainstage.close();
            client.getInstance().stopClient();
        }


    }