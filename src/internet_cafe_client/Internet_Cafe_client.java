/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package internet_cafe_client;

import controller.HomepageController;
import controller.TestController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author USER
 */
public class Internet_Cafe_client extends Application {
    
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage=stage;
            client c = client.getInstance();
            c.setPrimaryStage(stage);
        new Thread(() -> {

            c.startClient();
        }).start();
        
        
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/test.fxml"));
        
        //testing code
        
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Default.fxml"));
        
       // Parent root = loader.load(); 
        
        Scene scene = new Scene(root);

        stage.initStyle(StageStyle.UNDECORATED); 
        stage.centerOnScreen();
        stage.setMaximized(true);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);
        
    }
    
}
