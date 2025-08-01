/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import internet_cafe_client.client;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import model.cartItem;




/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class OrderListController implements Initializable {

    @FXML
    private FlowPane orderFlowPane;
      @FXML
    private Button btnOrder;

    @FXML
    private Button btnReset;
    
    private FoodorderController mainController;
    
    public List<cartItem> cartlist = new ArrayList<>();
    
    private client c;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if(cartlist.isEmpty()){
            System.out.println("null");
        }
        else{
            
        }
        
       
        
       
    }
    
      @FXML
    void HandleOrderAction(ActionEvent event) {
       StringBuilder sb = new StringBuilder();
        for (cartItem item : cartlist) {
            sb.append(item.getFoodname()).append(",")
              .append(item.getPrice()).append(",")
              .append(item.getQty()).append(";");
    }

// Remove trailing semicolon
if (sb.length() > 0) sb.setLength(sb.length() - 1);




    String message = "FoodOrder|" + sb.toString();
    client.getInstance().sendToServer(message);

    btnReset.fire();
    mainController.CartOnOff();


    }

    @FXML
    void HandleResetAction(ActionEvent event) {
        mainController.removeAllCart();      // Clear main controller list
    cartlist.clear();                    // Clear local list too!
    orderFlowPane.getChildren().clear(); // Refresh UI
        orderFlowPane.getChildren().clear();
    }

    private HBox createItemBox(cartItem item) {
        // Labels for item details
        Label nameLabel = new Label( item.getFoodname());
        Label qtyLabel = new Label( Integer.toString(item.getQty()));
        Label unitPriceLabel = new Label(Double.toString(item.getPrice()));
        Label totalLabel = new Label(  Double.toString(item.getPrice()*item.getQty()));
        
        nameLabel.setStyle("-fx-pref-width:80px;");
        qtyLabel.setStyle("-fx-pref-width:30px;");
        unitPriceLabel.setStyle("-fx-pref-width:60px;");
        totalLabel.setStyle("-fx-pref-width:60px;");
        
        //trash
        Image trashImage = new Image(getClass().getResource("/img/red-trash-can-icon.png").toExternalForm());
        ImageView trashView = new ImageView(trashImage);
        trashView.setFitWidth(20);
        trashView.setFitHeight(20);

// Make the image ignore mouse clicks
trashView.setMouseTransparent(true);

        // Delete button
        Button deleteBtn = new Button();
        deleteBtn.setOnAction(e -> {
        removeItemFromOrderList(item);
        mainController.syncCartFromOrderList(cartlist); // Sync back
         updateCartView();
        });
        
        deleteBtn.setStyle("-fx-pref-width:30px; -fx-background-color: transparent; -fx-border-color: transparent; -fx-padding:0;");
        deleteBtn.setGraphic(trashView);

        HBox box = new HBox(5, nameLabel, qtyLabel, unitPriceLabel, totalLabel, deleteBtn);
        box.setPadding(new Insets(10));
        box.setPrefWidth(340);
        box.setMaxWidth(340);
        box.setStyle("-fx-background-color: white; -fx-border-width:0 0 1px 0; -fx-border-color:black;  -fx-spacing:14px;");

        return box;
    }

     public void updateCartView() {
        orderFlowPane.getChildren().clear(); // Clear old items

        for (cartItem item : cartlist) {
            HBox itemBox = createItemBox(item);
            orderFlowPane.getChildren().add(itemBox);
        }
    }
     
    public void initCart(ArrayList<cartItem> cartItems, FoodorderController mainController) {
    this.cartlist = mergeDuplicateItems(cartItems);
    this.mainController = mainController;
    updateCartView();
}

    
    private List<cartItem> mergeDuplicateItems(List<cartItem> items) {
    List<cartItem> merged = new ArrayList<>();
    
    for (cartItem current : items) {
        boolean found = false;
        for (cartItem existing : merged) {
            if (existing.getFoodname().equalsIgnoreCase(current.getFoodname())) {
                existing.setQty(existing.getQty() + current.getQty());
                found = true;
                break;
            }
        }
        if (!found) {
            merged.add(new cartItem(current.getFoodname(), current.getPrice(), current.getQty()));
        }
    }
    
    return merged;
}
public void removeItemFromOrderList(cartItem item) {
    cartlist.removeIf(i -> i.getFoodname().equalsIgnoreCase(item.getFoodname()));
    updateCartView();
}


  


    
}
