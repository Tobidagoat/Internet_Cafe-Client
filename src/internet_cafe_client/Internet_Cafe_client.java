/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package internet_cafe_client;

import controller.HomepageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author USER
 */
public class Internet_Cafe_client extends Application {
    
    public static Stage stage;
    @Override
    public void start(Stage stage) throws Exception {
        this.stage=stage;
        new Thread(() -> {
            client c = new client();
             c.setPrimaryStage(stage);
            c.startClient("localhost", 5000);
        }).start();
        
        
        
        Parent root = FXMLLoader.load(getClass().getResource("/view/test.fxml"));
        
        //testing code
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
//        Parent root = loader.load();
//        HomepageController controller = loader.getController();
//        controller.setSessionData("pc3", "1", "Room A", "Silver", 1800); 
        
        
        Scene scene = new Scene(root);        
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
