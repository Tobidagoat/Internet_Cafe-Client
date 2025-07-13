package internet_cafe_client;

import controller.HomepageController;
import controller.TestController;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class client {

    private static final String CLIENT_NAME = "pc3"; // Change this per machine
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Stage primaryStage;
    private Parent root;
    private HomepageController controller;

    // Start the client and connect to server
    public void startClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send client name to server
            out.println(CLIENT_NAME);
            System.out.println("✅ Connected to server as: " + CLIENT_NAME);

            // Start listening for messages from server
            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            System.out.println("❌ Failed to connect to server.");
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                handleCommand(msg);
            }
        } catch (IOException e) {
            System.out.println("❌ Disconnected from server.");
        } catch (SQLException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendToServer(String msg) {
    if (out != null) {
        out.println(msg);
        System.out.println("Sent: " + msg);
    } else {
        System.out.println("Cannot send message.");
    }
}

    private void handleCommand(String msg) throws IOException, SQLException {

        if (msg.startsWith("UNLOCK|")) {
        System.out.println("🔓 UNLOCK command received!");

        String[] parts = msg.split("\\|");
        if (parts.length >= 6) {
            String pcName = parts[1];
            String userid = parts[2];
            String room = parts[3];
            String packageName = parts[4];
            int duration = Integer.parseInt(parts[5]);

            System.out.println("🖥 PC: " + pcName);
            System.out.println("👤 User: " + userid);
            System.out.println("🏠 Room: " + room);
            System.out.println("📦 Package: " + packageName);
            System.out.println("⏱ Duration: " + duration);

        
            Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/homepage.fxml"));
                root = loader.load();

                controller = loader.getController();                
                
                controller.setSessionData(pcName, userid, room, packageName, duration);
                controller.setClient(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.show();

                if (primaryStage != null) {
                    primaryStage.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }   catch (SQLException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
        });
    }
        } else if (msg.startsWith("TERMINATE")) {
            System.out.println("🛑 Shutdown requested!");
            HomepageController c=new HomepageController();
            c.terminatesession();
        } else if (msg.startsWith("ADD_TIME_CONFIRMED|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 2) {
                int extratime = Integer.parseInt(parts[1]);
                System.out.println("✅ Time add confirmed: +" + extratime + " seconds");

                Platform.runLater(() -> {
               
                if (controller != null) {
                    controller.addTime(extratime);
                } else {
                    System.out.println("❌ HomepageController is null!");
                }
            });
            }
        } else {
            System.out.println("📩 Server says: " + msg);
        }
    }
    
    public void requestAddTime(int minutes) {
    if (out != null) {
        String message = "REQUEST_ADD_TIME|"  + minutes;
        out.println(message);
        System.out.println("⏳ Sent time add request: " + minutes + " seconds");
    }
}

    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}
