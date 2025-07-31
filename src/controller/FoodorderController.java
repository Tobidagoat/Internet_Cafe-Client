/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import database.DbConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.Circle;
import model.cartItem;

/**
 * FXML Controller class
 *
 * @author Linn Hein Htet
 */
public class FoodorderController implements Initializable {
    

    @FXML
    private TextField txtFoodSearch;
    @FXML
    private Button btnAllitems;
    @FXML
    private Button btnMeals;
    @FXML
    private Button btnSnacks;
    @FXML
    private Button btnDrinks;
    @FXML
    private Button btnDesserts;
    @FXML
    private FlowPane foodflowpane;
    @FXML
    private Button btnCart;
    @FXML
    private AnchorPane orderContainer;
    @FXML
    private Circle noti;
    @FXML
    private ScrollPane scrollp;
    
    public ArrayList<cartItem> cartlist = new ArrayList<>();
    
    OrderListController controller;
    
    private boolean isModalOpen = true;
    
     private Button selectedToggle = null; //For Food tag toggle
     
     private boolean notiStatus ;
     
     private String category = "";
     
     

    
    
    
    

    
    
    Connection con;
    Statement st;
    ResultSet rs;
    PreparedStatement pst;
    
    String food_type = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DbConnection db = new DbConnection();
        try {
            con = db.getConnection();
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomepageController.class.getName()).log(Level.SEVERE, null, ex);
            
            
        }
        btnAllitems.fire();
        
        btnCart.fire();
        noti.setVisible(false);
    }    

    @FXML
    private void HandleFoodSearchAction(ActionEvent event) throws SQLException, IOException {
        loadFoodData();
        
    }

   @FXML
    void HandleFoodToggle(ActionEvent event) throws SQLException, IOException {

        Button clicked = (Button) event.getSource();
    setToggle(clicked); 
    }

    @FXML
    private void btnViewCartAction(ActionEvent event) throws IOException {
        
        
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/orderList.fxml"));
        AnchorPane orderpane = loader.load();
        orderContainer.getChildren().clear();
        orderContainer.getChildren().add(orderpane);
        AnchorPane.setTopAnchor(orderpane, 0.0);
        AnchorPane.setLeftAnchor(orderpane, 0.0);
        AnchorPane.setBottomAnchor(orderpane, 0.0);
        AnchorPane.setRightAnchor(orderpane, 0.0);
          
         controller = loader.getController();
        controller.initCart(cartlist, this);
        
        CartOnOff();
                noti.setVisible(false);

        
        
        
        
        
    }
    public void loadFoodData() throws SQLException, IOException {
    foodflowpane.getChildren().clear();

    

    // Build SQL query
    String sql;
    PreparedStatement st =null;
    

        if(category.isEmpty()){
            sql = "select food_name, food_type, stock, price ,img from foods where food_name like ? ";
        pst = con.prepareStatement(sql);
            pst.setString(1,"%"+ txtFoodSearch.getText()+"%");
        }else{
            sql = "SELECT food_name, food_type, stock, price, img FROM foods WHERE food_type = ? && food_name like ?";
        pst = con.prepareStatement(sql);
            pst.setString(1, category);
            pst.setString(2, "%"+txtFoodSearch.getText()+"%");
            
        }
            rs = pst.executeQuery();
            while (rs.next()) {
        String foodname = rs.getString("food_name");
        String food_type = rs.getString("food_type");
        int stock = rs.getInt("stock");
        double price = rs.getDouble("price");
        String image = rs.getString("img");

        // Load each food card FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/foodcard.fxml"));
        AnchorPane foodcard = loader.load();
        foodflowpane.getChildren().add(foodcard);

        // Set data in the card
        FoodcardController foodcardController = loader.getController();
        foodcardController.setdata(foodname, stock, price, image);
        foodcardController.setMainController(this);
    }

    rs.close();
      

    

    

    
}

    
   

    public void addToCart(cartItem newItem) {
    if (cartlist == null) {
        System.out.println("❌ cartList is NULL");
        cartlist = new ArrayList<>();
    } else {
        System.out.println("✅ cartList is fine. Size before add: " + cartlist.size());
    }

    boolean found = false;
    for (cartItem existingItem : cartlist) {
        if (existingItem.getFoodname().equalsIgnoreCase(newItem.getFoodname())) {
            // Item already exists, increase qty
            existingItem.setQty(existingItem.getQty() + newItem.getQty());
            found = true;
            System.out.println("🔁 Increased quantity of: " + newItem.getFoodname());
            break;
        }
    }

    if (!found) {
        cartlist.add(newItem);
        System.out.println("➕ Added new item: " + newItem.getFoodname());
    }

    // Show updated cart content
    for (cartItem i : cartlist) {
        System.out.println("🛒 " + i.getFoodname() + " | Qty: " + i.getQty());
    }

    controller.initCart(new ArrayList<>(cartlist), this); // Clone list to be safe
}

    public void removeFromCart(cartItem item){
        cartlist.remove(item);
    }
    public void removeAllCart(){
        cartlist.clear();
    }
    
     private void setToggle(Button btn) throws SQLException, IOException {
    if (selectedToggle != null) {
        selectedToggle.getStyleClass().remove("selected-food-tag");
    }

    selectedToggle = btn;
    selectedToggle.getStyleClass().add("selected-food-tag");


    // Trigger custom logic
    if (btn == btnAllitems) {
        scrollp.setVvalue(0);
        setcategory(Granularity.allItems);
        loadFoodData();
    } else if (btn == btnMeals) {
        scrollp.setVvalue(0);
        setcategory(Granularity.meals);
        loadFoodData();
        
    } else if (btn == btnSnacks) {
        scrollp.setVvalue(0);
        setcategory(Granularity.snacks);
        loadFoodData();
        
    }else if(btn==btnDrinks){
        scrollp.setVvalue(0);
        setcategory(Granularity.drinks);
        loadFoodData();
       
            
    }else if(btn==btnDesserts){
        scrollp.setVvalue(0);
        setcategory(Granularity.desserts);
        loadFoodData();
            
    }
          
     }
     
     public enum Granularity {
    allItems, meals, snacks , drinks, desserts
 }
     
     
     public void setcategory(Granularity granularity){
         
           // Determine category based on granularity
     category = switch (granularity) {
        case allItems -> "";          // Load all items
        case meals -> "meal";
        case snacks -> "snack";
        case drinks -> "drink";
        case desserts -> "dessert";
    };
         
     }
   public void ShowNoti(){
       if(isModalOpen==false){
           noti.setVisible(true);
       }
       else{
           noti.setVisible(false);
       }
           
   }
   public void CartOnOff(){
    if(!isModalOpen ){
          
        orderContainer.setVisible(true);
        isModalOpen=true;
        }
        else{
            orderContainer.setVisible(false);
            isModalOpen=false;
        }

}
      
     public void syncCartFromOrderList(List<cartItem> updatedCart) {
    this.cartlist.clear();
    this.cartlist.addAll(updatedCart);
}

     
    }

    


