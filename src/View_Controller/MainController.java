package View_Controller;

import Model.*;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


/**
This class is the controller which controls the MainView.fxml.
 */
public class MainController implements Initializable {
    // Parts table labels
    public TableView<Part> partsTable;
    public TableColumn partIDCol;
    public TableColumn partNameCol;
    public TableColumn partInventoryLevelCol;
    public TableColumn partCostPerUnitCol;
    public TextField partSearch;
    public Label errorFeed;
    public Button modify;

    //Product table labels
    public TableView<Product> productsTable;
    public TableColumn productIDCol;
    public TableColumn productNameCol;
    public TableColumn prodInventoryLevelCol;
    public TableColumn prodCostPerUnitCol;
    public TextField productSearch;

    //Needed for UI
    public double xOffset = 0;
    public double yOffset = 0;
    public Circle status;
    public ImageView icon;
    Timer timer;




    /**
     * Initialized items, for functionality of the table views for both Parts and Products.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setting up Part table to match getters
        partsTable.setItems(Inventory.getAllParts());
        partIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));  // setting columns fxids to model values (creating column values)
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partCostPerUnitCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Setting up application Icon for custom navigation.
        Image systemIcon = new Image(getClass().getResourceAsStream("48x48.png"));
        icon.setImage(systemIcon);

        //Setting up Product table to match getters.
        productsTable.setItems(Inventory.getAllProducts());
        productIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        prodInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        prodCostPerUnitCol.setCellValueFactory(new PropertyValueFactory<>("price"));


    }

    // Part Events

    /**
     * This links to the addPartView through the Add button below the Parts table.
     */
    public void addPartView(ActionEvent actionEvent) throws IOException {
        System.out.println("Visiting Add Part Page");
        Parent root = FXMLLoader.load(getClass().getResource("AddPartView.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene addPartView = new Scene(root, 434, 525);
        stage.setTitle("Add Parts");
        stage.setScene(addPartView);
        stage.show();

    }

    /**
     * This controls the Delete button below the Parts table. It allows for selection of a Part and prompts for confirmation before finally deleting the selected Part.
     */
    public void deletePart(ActionEvent actionEvent) {
        // Allows for selection of part
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        try {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("You are trying to delete a part. ");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("You will be deleting " + selectedPart.getName() + ".");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Inventory.deletePart(selectedPart); // Passes the selectedPart to the the delete method in the Inventory class.
                errorFeed.setText("You deleted " + selectedPart.getName());
            } else {
                System.out.println("You have decided against your action.");
            }
        } catch (NullPointerException e) {
            if (selectedPart == null) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You have not selected a part.");
                timer = new Timer();
                timer.schedule(new errorRefresh(), 5000);
            }
        }
    }


    /**
     * This controls the search text field for Parts. The text field allows for searching by partId and partName.
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
     * This controls the Modify button below the Parts table and enables navigation to AddPartView.fxml, while transferring the information of a selectedPart.
     */
    public void modifyPartView(ActionEvent actionEvent) throws IOException, InterruptedException {

        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            status.setFill(Paint.valueOf("#ff2727"));
            errorFeed.setText("You have not selected any item.");
            timer = new Timer();
            timer.schedule(new errorRefresh(), 5000);


        } else {
            AddPartController.modPart(Inventory.getAllParts().indexOf(selectedPart), selectedPart);
            System.out.println("Visiting Modify Part Page.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddPartView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene addPartView = new Scene(root, 434, 525);
            stage.setTitle("Add Parts");
            stage.setScene(addPartView);
            stage.show();
        }
    }


    // Product Events

    /**
     * This links to the addProdView through the Add button below the Product table.
     */
    public void addProductView(ActionEvent actionEvent) throws IOException {
        System.out.println("Visiting Add Product Page");
        AddProdController.newProduct(new Product(0, "null", 0.00, 0, 0, 0)); // creating blank product to store new product data and start temp list
        Parent root = FXMLLoader.load(getClass().getResource("AddProdView.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene addProductView = new Scene(root, 900, 600);
        stage.setTitle("Add Products");
        stage.setScene(addProductView);
        stage.show();
    }

    /**
     * This controls the Delete button below the Products table. It allows for selection of a Product and prompts for confirmation before finally deleting the selected Product.  It will not allow deleting a product which has associated parts.
     */
    public void deleteProduct(ActionEvent actionEvent) {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();

        try {
            if (selectedProduct.getAssociatedParts().isEmpty()) {
                System.out.println("This product does not have a Associated Part List, it will be deleted");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("You are trying to delete a product. ");
                alert.setHeaderText("Are you sure?");
                alert.setContentText("You will be deleting " + selectedProduct.getName() + ".");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Inventory.deleteProduct(selectedProduct);
                } else {
                    System.out.println("You have decided against your action.");
                }
            } else if (selectedProduct.getAssociatedParts() != null) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You can not delete a product which has associated parts, please remove the parts and then delete the product.");
                timer = new Timer();
                timer.schedule(new errorRefresh(), 5000);
            }
        } catch (NullPointerException e) {
            if (selectedProduct == null) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You have not selected a product.");
                timer = new Timer();
                timer.schedule(new errorRefresh(), 5000);
            }
        }
    }


    /**
     * This controls the search text field for Products. The text field allows for searching by productId and productName.
     */
    public void productSearch(KeyEvent keyEvent) {

        ObservableList<Product> filteredProducts = FXCollections.observableArrayList(); // Declaring a list to hold the filtered products.

        for (Product idSearch : Inventory.getAllProducts()) {
            // In this section we are checking for empty text and reporting errors to the errorFeed label.
            try {  // First searching for ID
                if (productSearch.getText().isEmpty()) {
                    productsTable.setItems(Inventory.getAllProducts()); //Setting the table to the original parts list.
                    errorFeed.setText("You must search by Product ID or Name"); // Used this for error reporting.
                    return;
                }

                // If selected part is found, pass the part Id to the lookupPart function.
                else if (Integer.parseInt(productSearch.getText()) == idSearch.getId()) { // checking if loop variable matches input.
                    Product filtered = Inventory.lookupProduct(Integer.parseInt(productSearch.getText()));// Declaring a Product to hold the return value of the lookUpPart function, which is being called.
                    System.out.println("You are returning " + filtered.getName()); // Displaying the returned value in console.
                    filteredProducts.add(filtered); //Adding part to temporary list.
                    productsTable.setItems(filteredProducts); // Displaying the temporary list in table.
                    return;

                }
            } // If the search box does not contain a integer, we catch text
            catch (NumberFormatException e) {
                ObservableList<Product> filtered = Inventory.lookupProduct(productSearch.getText());  // Declaring a Observable List of Parts, to call the lookup part function, based on Search Box text.
                productsTable.setItems(filtered); // Displaying the temporary list in table.
                return;


            }

        }
    }

    /**
     * This controls the Modify button below the Product table and enables navigation to AddProdView.fxml, while transferring the information of a selectedProduct.
     */
    public void modifyProductView(ActionEvent actionEvent) throws IOException, InterruptedException {

        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            status.setFill(Paint.valueOf("#ff2727"));
            errorFeed.setText("You have not selected any item.");
            timer = new Timer();
            timer.schedule(new errorRefresh(), 5000);

        } else {
            AddProdController.modProduct(Inventory.getAllProducts().indexOf(selectedProduct), selectedProduct);
            System.out.println("Visiting Modify Product Page.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProdView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene addProductView = new Scene(root, 900, 600);
            stage.setTitle("Modify Products");
            stage.setScene(addProductView);
            stage.show();
        }
    }


    /// Top Bar Menu Items

    /**
     * This enables dragging the window when clicking the traditional area.
     */
    public void moveWindow(MouseEvent mouseEvent) {
        status.setFill(Paint.valueOf("#2ba40f"));
        errorFeed.setText("Action feedback will be provided here.");
        Node source = (Node) mouseEvent.getSource();
        Window theStage = source.getScene().getWindow();
        xOffset = mouseEvent.getX();
        yOffset = mouseEvent.getY();

        source.setOnMouseDragged(Event -> {
            theStage.setX(Event.getScreenX() - xOffset);
            theStage.setY(Event.getScreenY() - yOffset);
        });

    }

    /**
     * This controls the Close button and enables exiting the application.
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
     * This controls the Minimize button and enables hiding the application to the start bar.
     */
    public void mini(MouseEvent actionEvent) {
        status.setFill(Paint.valueOf("#2ba40f"));
        errorFeed.setText("Action feedback will be provided here.");
        Stage stage = (Stage) ((Circle) actionEvent.getSource()).getScene().getWindow(); // Using stage object to accept action event. THIS IS THE START OF WORKING CODE
        stage.setIconified(true); //  minimize into task bar, use boolean.
    }

    /**
     * This class will reset the error feed after 5 seconds.
     */
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





