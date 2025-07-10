
package internet_cafe_client;

import controller.TestController;
import internet_cafe_client.Internet_Cafe_client;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class client {
    Parent root;
    private Stage primaryStage;
    private static final String clientName = "pc1"; // Set your PC name here
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private Runnable message;

//    public static void main(String[] args) {
//        client c = new client();
//        c.startClient("localhost", 5000);
//    }

    public void startClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            // Send client name to server
            out.println(clientName);
            System.out.println("✅ Connected as '" + clientName + "'");

            // Receiving thread
            new Thread(() -> receiveMessages()).start();

            // Sending commands from console
            while (true) {
                System.out.print(">> ");
                String input = scanner.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("exit")) break;
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                handleCommand(msg);
                System.out.print(">> ");
            }
        } catch (IOException e) {
            System.out.println("❌ Disconnected from server.");
        }
    }

    // 🧠 Handle commands from SERVER (admin)
    private void handleCommand(String msg) throws IOException {
        if (msg.startsWith("UNLOCK|")) {
            System.out.println("🔓 UNLOCK command received!");
            String[] parts = msg.split("\\|");
            if (parts.length >= 4) {
                System.out.println("👉 Room: " + parts[2]);
                System.out.println("👉 PC: " + parts[1]);
                System.out.println("👉 Duration: " + parts[3]);
            }
            
            // 👇 You can trigger JavaFX methods here using Platform.runLater if needed
            Platform.runLater(() -> {
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/test2.fxml"));
    
                try {
                    root = loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }

                TestController controller = loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                primaryStage.close();
                });

        } else if (msg.startsWith("SHUTDOWN")) {
            System.out.println("🛑 Shutdown requested!");
        } else {
            System.out.println("📩 Server says: " + msg);
        }
    }

    // 🔧 Set a message (like a callback)
    public void setMessage(Runnable message) {
        this.message = message;
    }
    
    
    public void setPrimaryStage(Stage stage) {
    this.primaryStage = stage;
}

   
}
