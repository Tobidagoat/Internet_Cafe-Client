package internet_cafe_client;

import controller.DefaultController;
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
    // Singleton instance
    private static client instance;
    
    // Client properties
    private String clientName;
    private String host;
    private int port;
    private DefaultController defaultController;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Stage primaryStage;
    private Parent root;

    // Private constructor for singleton
    private client() {
        ConfigManager config = new ConfigManager();
        this.clientName = config.getPcId();
        this.host = config.getHost();
        this.port = config.getPort();
    }

    // Singleton access method
    public static synchronized client getInstance() {
        if (instance == null) {
            instance = new client();
        }
        return instance;
    }

    public void startClient() {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(clientName);
            System.out.println("✅ Connected to server as: " + clientName +
                    " (Host: " + host + ", Port: " + port + ")");

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

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/default.fxml"));
                        root = loader.load();
                        
                        defaultController = loader.getController();
                        defaultController.setClient(client.getInstance()); // Fixed this line
                        defaultController.setSessionData(pcName, userid, room, packageName, duration);
                        
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.centerOnScreen();
                        stage.show();

                        if (primaryStage != null) {
                            primaryStage.close();
                        }
                    } catch (IOException | SQLException ex) {
                        Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } else if (msg.startsWith("TERMINATE")) {
            System.out.println("🛑 Shutdown requested!");
            Platform.runLater(() -> {
                if (defaultController != null) {
                    defaultController.terminateSession();
                } else {
                    System.out.println("❌ Cannot terminate — defaultController is null.");
                }
            });
        } else if (msg.startsWith("ADD_TIME_CONFIRMED|")) {
            String[] parts = msg.split("\\|");
            if (parts.length == 2) {
                int extratime = Integer.parseInt(parts[1]);
                Platform.runLater(() -> {
                    if (defaultController != null) {
                        defaultController.addTime(extratime);
                    } else {
                        System.out.println("❌ DefaultController is null!");
                    }
                });
            }
        } else {
            System.out.println("📩 Server says: " + msg);
        }
    }

    public void requestAddTime(int minutes) {
        if (out != null) {
            String message = "REQUEST_ADD_TIME|" + minutes;
            out.println(message);
            System.out.println("⏳ Sent time add request: " + minutes + " seconds");
        }
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    // Additional getters
    public String getClientName() {
        return clientName;
    }
    
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}