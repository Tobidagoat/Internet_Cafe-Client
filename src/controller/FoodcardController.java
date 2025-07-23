/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.cartItem;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class FoodcardController implements Initializable {

    @FXML
    private AnchorPane FoodcardPane;
    @FXML
    private ImageView imgFood;
    @FXML
    private Label txtName;
    @FXML
    private Label txtPrice;
    @FXML
    private Button btnAddCart;
    @FXML
    private Button btnIncrease;
    @FXML
    private Label txtAmount;
    @FXML
    private Button btnDecrease;
    
    
    private String name;
    private int stock;
    private double price;
    private String img;
    private int amount =1;
    
    private FoodorderController mainController;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    

    @FXML
    private void HandleAddCartAction(ActionEvent event) {
        
        
        int qty = Integer.parseInt(txtAmount.getText());
        mainController.addToCart(passOrder(name,price, qty));
        amount = 1;
        txtAmount.setText(Integer.toString(amount));
        mainController.ShowNoti();
        
        
     
        
        
        
    }

    @FXML
    private void HandleIncreaseAction(ActionEvent event) {
        
        
        if(Integer.parseInt(txtAmount.getText()) < stock ){
            amount++;
            txtAmount.setText(Integer.toString(amount));
        }
    }

    @FXML
    private void HandleDecreaseAction(ActionEvent event) {
         
        
        if(Integer.parseInt(txtAmount.getText()) < stock && Integer.parseInt(txtAmount.getText()) > 1){
            amount--;
            txtAmount.setText(Integer.toString(amount));
        }
    }
    
    public void setdata (String name, int stock, double price, String img){
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.img = img;
        txtName.setText(name);
        txtPrice.setText(Double.toString(price));
        File file = new File("src/img/"+img);
        Image image = new Image(file.toURI().toString());
        imgFood.setImage(image);
        
    }
    public cartItem passOrder (String name,double price, int qty){
        cartItem c = new cartItem(name,price, qty);
        return c;
        
    }
    
    public void setMainController(FoodorderController controller) {
    this.mainController = controller;
}
    
   
       
}
