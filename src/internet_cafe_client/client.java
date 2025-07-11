package internet_cafe_client;

import controller.TestController;
import java.io.*;
import java.net.*;
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

    // Handle incoming messages from server
    private void receiveMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                handleCommand(msg);
            }
        } catch (IOException e) {
            System.out.println("❌ Disconnected from server.");
        }
    }

    // Process command received from server
    private void handleCommand(String msg) {
        if (msg.startsWith("UNLOCK|")) {
            System.out.println("🔓 UNLOCK command received!");

            String[] parts = msg.split("\\|");
            if (parts.length >= 4) {
                System.out.println("🖥 PC: " + parts[1]);
                System.out.println("🏠 Room: " + parts[2]);
                System.out.println(" Package: " + parts[3]);
                System.out.println("⏱ Duration: " + parts[4]);
            }

            // Switch to test2.fxml on unlock
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/test2.fxml"));
                    root = loader.load();

                    TestController controller = loader.getController();
                    // If you need to pass anything to controller, do it here

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    if (primaryStage != null) {
                        primaryStage.close();
                    }

                } catch (IOException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        } else if (msg.startsWith("SHUTDOWN")) {
            System.out.println("🛑 Shutdown requested!");
            // Optionally: trigger client shutdown or GUI message
        } else {
            System.out.println("📩 Server says: " + msg);
        }
    }

    // Set the main stage (for closing during unlock)
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
}
