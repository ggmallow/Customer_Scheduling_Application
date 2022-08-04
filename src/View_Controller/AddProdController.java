package View_Controller;

import Model.Inventory;
import Model.Part;
import Model.Product;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 This class controls the view AddProdView.fxml.
 */
public class AddProdController implements Initializable {
    public TextField name;
    public TextField stock;
    public TextField price;
    public TextField max;
    public TextField min;

    private static Product mod = null; // Part object used to hold part that will be modified.
    private static Product newProduct = null;
    private static int indexTransfer = 0; //Creating int to hold index of part being modified.
    public Label productType;

    //Setting up table labels for Parts table
    public TableView<Part> partsTable;
    public TableColumn partIDCol;
    public TableColumn partNameCol;
    public TableColumn partInventoryLevelCol;
    public TableColumn partCostPerUnitCol;
    public TextField partSearch;
    public Label errorFeed;
    public Button add;

    //Setting up table labels for Associated Part Table
    public static ObservableList<Part> tempAssociated;
    public TableView<Part> assoPartTable;
    public TableColumn assoPartIDCol;
    public TableColumn assoPartNameCol;
    public TableColumn assoPartInventoryLevelCol;
    public TableColumn assoPartCostPerUnitCol;

    //Needed for UI
    public ImageView icon;
    public Circle status;
    public Label sceneLabel;
    Timer timer;


    /**
     This sets up the table to the correct values and checks whether you came from the Mod Part button.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


      //Setting Parts Table fxid to Part values
       partsTable.setItems(Inventory.allParts);
       partIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
       partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
       partInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
       partCostPerUnitCol.setCellValueFactory(new PropertyValueFactory<>("price"));

       //Setting Associated Parts Table fxid to AssociatedTable
        assoPartTable.setItems(tempAssociated); // Setting table to the temporary list
        assoPartIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        assoPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        assoPartInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        assoPartCostPerUnitCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Setting up application Icon for custom navigation.
        Image systemIcon = new Image(getClass().getResourceAsStream("48x48.png"));
        icon.setImage(systemIcon);

        if (mod != null ) {
            productType.setText("Modify Product:");
            sceneLabel.setText("Modify Product Menu");
            name.setText(mod.getName());
            stock.setText(String.valueOf(mod.getStock()));
            price.setText(String.valueOf(mod.getPrice()));
            max.setText(String.valueOf(mod.getMax()));
            min.setText(String.valueOf(mod.getMin()));


        }

    }

    /**
     This allows for transferring of Product data from the MainController, if selecting the Modify button.
     @param location - The index of the selected Product.
     @param product - The information of the selected Product.
     */
    public static void modProduct(int location, Product product) {
        indexTransfer = location; //Setting indexTransfer to the index of the selected part.
        mod = product; // Setting product object to the part being modified.
        tempAssociated = mod.getAssociatedParts(); // Setting temporary list to associated parts with transferred Product

    }

    /**
     This allows for creating Product by creating a placeholder.
     @param product - A temporary product created to assign values.
     */
    public static void newProduct(Product product) {
        newProduct = product;
        tempAssociated = newProduct.getAssociatedParts(); // Setting temporary list to associated parts with transferred Product

    }


    //Action Events
    /**
     This allows returning to the MainView(MainView.fxml) when clicking the Cancel button.
     */
    public void returnMainView(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Do you want to return to the main menu? ");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("You are about to return to the main menu");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Returning Home");
            Parent root = FXMLLoader.load(getClass().getResource("../View_Controller/MainView.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene MainView = new Scene(root, 900, 600);
            stage.setTitle("Inventory Management System - Jake Dyke");
            stage.setScene(MainView);
            stage.show();
            mod = null; //Resetting mod so data does not transfer.
        } else { System.out.println("You have decided to cancel your action");}
    }

    /**
    This allows for saving a Product and returns to the MainView(MainView.fxml) when clicking the Save button.
     */
    public void save(ActionEvent actionEvent) throws IOException {

        try {

            // Lines 161 - 166 are checking that the user is filling out the form correctly.

            //Throws null exception if name field is empty or left blank.
            if (name.getText().isEmpty() || name.getText().isBlank()) { throw null;}

            // Checks for too much inventory.
            if (Integer.parseInt(stock.getText()) > Integer.parseInt(max.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You are holding too much inventory, please lower your stock levels.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            //Ensures maximum stock is greater than minimum.
            if (Integer.parseInt(max.getText()) < Integer.parseInt(min.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Maximum stock levels must be greater than minimum.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            // Ensures stock levels are greater than minimum required.
            if (Integer.parseInt(stock.getText()) < Integer.parseInt(min.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Inventory levels must be greater than minimum or lower the minimum value.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            //This does not allow for the Max inventory level to be set to or below 0.
            if (Integer.parseInt(max.getText()) <= 0) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Max must be greater than 0.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            // This allows for Min to be set to 0 or higher.
            if (Integer.parseInt(min.getText()) < 0) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Min must be 0 or greater.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }
            else if ((Integer.parseInt(stock.getText()) >= Integer.parseInt(min.getText())) && (Integer.parseInt(stock.getText()) <= Integer.parseInt(max.getText()))) {

            //int id, String name, double price, int stock, int min, int max
            if (mod != null) {
                Product tempProduct = new Product(mod.getId(), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));
                tempProduct.getAssociatedParts().clear();
                tempProduct.getAssociatedParts().addAll(tempAssociated);
                Inventory.updateProduct(indexTransfer, tempProduct);

            }
            // Add product logic
            else {
                Product newProduct = new Product(assignProdId(Inventory.getAllProducts()), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));
                newProduct.getAssociatedParts().addAll(tempAssociated);
                Inventory.addProduct(newProduct);
            }
            //Returning home
            Parent root = FXMLLoader.load(getClass().getResource("../View_Controller/MainView.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene MainView = new Scene(root, 900, 600);
            stage.setTitle("Inventory Management System - Jake Dyke");
            stage.setScene(MainView);
            stage.show();
            mod = null; //Resetting mod so data does not transfer.
        }
        }
        // If the wrong data type is supplied, we catch with these error messages.
        catch (NumberFormatException e) {
            //This is a generic catch when a user has multiple errors.
            status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("Please fill in the form with the correct values.");
            timer = new Timer(); // Creating timer.
            timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).

            // Wont allow saving the form without filling out max and min values.
            if ((max.getText().isEmpty() || max.getText().isBlank()) || (min.getText().isEmpty() || max.getText().isBlank())) { status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("Max and Min values are required.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }
            // Wont allow saving with out a price.
            if (price.getText().isEmpty() || price.getText().isBlank()) { status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("You will need to set a price.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }
            //Setting default value or stock to 0 if left blank.
            if (stock.getText().isEmpty()) { stock.setText(Integer.toString(0)); status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("Stock levels have been left blank, so they have been set to the default value of 0.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            } catch (NullPointerException e) {
            //Generic catch for blank values.
            status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("Blank values are not allowed");
            timer = new Timer(); // Creating timer.
            timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            // Reporting blank name from the null exception trow above.
            if (name.getText().isEmpty() || name.getText().isBlank()) { status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("You have forgot to fill out a name.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

        }
    }

    /**
     This allows for adding an associated part to a Product when clicking the Add button.
     */
    public void add(ActionEvent actionEvent) {
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        Product tempProduct =  newProduct;
        if  (Inventory.allParts.isEmpty()) { status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("You will need to create parts if you want to add Associated Parts."); }
        else if (selectedPart == null) {
            status.setFill(Paint.valueOf("#ff2727"));
            errorFeed.setText("You must select a part.");
            timer = new Timer(); // Creating timer.
            timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
        }
            else {
            if (mod != null) {
                mod.addAssociatedPart(selectedPart); }
            else {
                tempProduct.addAssociatedPart(selectedPart); }
            }

    }

    /**
     This allows for searching the Part table from text box named partSearch.
     */
    public void partSearch(KeyEvent keyEvent) {
        ObservableList<Part> filteredParts = FXCollections.observableArrayList(); // Declaring a list to hold the filtered parts

        for (Part idSearch : Inventory.getAllParts()) {
            // In this section we are checking for empty text and reporting errors to the errorFeed label.
            try {  // First searching for ID
                if (partSearch.getText().isEmpty()) {
                    partsTable.setItems(Inventory.getAllParts()); //Setting the table to the original parts list.
                    errorFeed.setText("You must search by Part ID or Name"); // Used this for error reporting.
                    return;
                }

                // If selected part is found, pass the part Id to the lookupPart function.
                else if (Integer.parseInt(partSearch.getText()) == idSearch.getId()) { // checking if loop variable matches input.
                    Part filtered = Inventory.lookupPart(Integer.parseInt(partSearch.getText()));// Declaring a Part to hold the return value of the lookUpPart function, which is being called.
                    System.out.println("You are returning " + filtered.getName()); // Displaying the returned value in console.
                    filteredParts.add(filtered); //Adding part to temporary list.
                    partsTable.setItems(filteredParts); // Displaying the temporary list in table.
                    return;

                }
            } // If the search box does not contain a integer, we catch text
            catch (NumberFormatException e) {
                ObservableList<Part> filtered = Inventory.lookupPart(partSearch.getText());  // Declaring a Observable List of Parts, to call the lookup part function, based on Search Box text.
                partsTable.setItems(filtered); // Displaying the temporary list in table.
                return;

            }
        }
    }

    /**
     This allows for removing an associated Part from a Product when clicking the Remove button.
     */
    public void removePart(ActionEvent actionEvent) {
        Part selectedPart = assoPartTable.getSelectionModel().getSelectedItem();
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You are trying to delete an associated part. ");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("You will be deleting " + selectedPart.getName() + ".");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.out.println("You are removing " + selectedPart.getName());
                tempAssociated.remove(selectedPart);
            } else {
                System.out.println("You have decided against your action.");
            }
        } catch (NullPointerException e) {
            if (selectedPart == null) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You have not selected a part.");}
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

        }

    /**
     This assigns ProductIds to Products an ensures the ID is not duplicated.
     @param assignId - An observable list of Products.
     */
    public static int assignProdId(ObservableList<Product> assignId) {
        ArrayList<Integer> idList = new ArrayList<>(); // Declaring a Array of Integers to hold all Part IDs from allProducts.
        if (assignId.isEmpty()) { return 1; } // Checks if there are any products currently in the list. If the list is empty, automatically it starts with product 1 (This creates odd product numbers)
        else {
            for (Product temp : assignId) { //Looping through list of parts
                idList.add(temp.getId()); //Adding Part ID to idList Array.
            }
            int max = Collections.max(idList); // Getting max value from idList and assigning to max.
            return max + 2;//Creates unique even part numbers based on maximum value of previously added parts.
        }
    }

    //Custom Menu bar.
    /**
     This controls the Close button and enables exiting the application.
     */
    public void close(MouseEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You are trying to close the application. ");
        alert.setHeaderText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Closing");
            System.exit(0);
        } else {
            System.out.println("You have decided against your action.");
        }
    }

    /**
     This controls the Minimize button and enables hiding the application to the start bar.
     */
    public void mini(MouseEvent actionEvent) {
        Stage stage = (Stage)((Circle)actionEvent.getSource()).getScene().getWindow(); // Using stage object to accept action event. THIS IS THE START OF WORKING CODE
        stage.setIconified(true); //  minimize into task bar, use boolean.
    }

    /**
     This enables dragging the window when clicking the traditional area.
     */
    public void moveWindow(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        Window theStage = source.getScene().getWindow();
        double xOffset = mouseEvent.getX();
        double yOffset = mouseEvent.getY();

        source.setOnMouseDragged(Event -> {
            theStage.setX(Event.getScreenX() - xOffset);
            theStage.setY(Event.getScreenY() - yOffset);
        });
    }

    class errorRefresh extends TimerTask {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    status.setFill(Paint.valueOf("#2ba40f"));
                    errorFeed.setText("Action feedback will be provided here.");
                    timer.cancel();
                }
            });

        }
    }

}

