package View_Controller;

import Model.InHouse;
import Model.Inventory;
import Model.Outsourced;
import Model.Part;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.Timer;

/**
 This class controller operates the AddPartView(AddPartView.fxml).
 */
public class AddPartController implements Initializable {


    public RadioButton inHouse;
    public RadioButton outSourced;
    public Label location;
    public  TextField name;
    public TextField price;
    public TextField max;
    public TextField min;
    public TextField sourced;
    private static Part mod = null; // Part object used to hold part that will be modified.
    private static int indexTransfer = 0; //Creating int to hold index of part being modified.
    public Label partType;
    public TextField idField;
    public TextField stock;
    public ImageView icon;

    //Needed for UI
    public Circle status;
    public Label errorFeed;
    public Label sceneLabel;
    Timer timer;

    /**
      This sets up the view with the appropriate data if the user is modifying a class.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (mod != null) { // Checking if we have selected a part to modify.
            if (mod instanceof InHouse){ // Checking type of selected part(mod).
                inHouse.setSelected(true); // Selecting the correct toggle group for InHouse Parts.
                sourced.setText(Integer.toString(((InHouse) mod).getMachineId())); // Setting text field to Machine ID of inHouse Part.
                location.setText("Machine ID:");
            } else { // Mod can only be an Outsourced part if isn't a InHouse part.
                outSourced.setSelected(true);//Selecting the correct toggle group if part(mod) is Outsourced.
                sourced.setText(((Outsourced) mod).getCompanyName()); // Setting text field to the Outsourced Part's company name.
                location.setText("Company Name:");
            }
            // idField.setText(String.valueOf(mod.getId())); // Currently disable but can show Part ID when modifying. You can not modify a Part ID.
            partType.setText("Modify Part"); // Setting the label to display correct text for modifying part.
            sceneLabel.setText("Modify Part Menu"); //Setting the label to display the correct window title.
            name.setText(mod.getName()); // Setting text field to part(mod).
            price.setText(String.valueOf(mod.getPrice())); // Setting price text field for part(mod).
            stock.setText(String.valueOf(mod.getStock()));
            max.setText(String.valueOf(mod.getMax())); // Setting text field for max for part(mod).
            min.setText(String.valueOf(mod.getMin())); // Setting text field for min for part(mod)
        }

        //Setting up application Icon for custom navigation.
        Image systemIcon = new Image(getClass().getResourceAsStream("48x48.png"));
        icon.setImage(systemIcon);


    }

    /**
     This allows for transferring of data of a modified part.
     @param location - This is transferring the index of the part from the Observable List allParts.
     @param part - This is transferring the values of the part selected to be modified.
     */
    public static void modPart(int location, Part part) {
        indexTransfer = location; //Setting indexTransfer to the index of the selected part.
        mod = part; // Setting part object to the part being modified.

}

// Action list starts here

    /**
     This allows the user to return to the MainView(MainView.fxml).
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
    When clicking the InHouse radio button, this changes the text of the label named location to "Machine ID".
     */
    public void onInHouse(ActionEvent actionEvent) {  // When selecting the InHouse button, it will set text of the location label.
        location.setText("Machine ID:");

    }

    /**
     When clicking the OutSourced radio button, this changes the text of the label named location to "Company Name".
     */
    public void onOutSourced(ActionEvent actionEvent) { // When selecting the Outsourced, the label text is set.
        location.setText("Company Name:");

    }

    /**
     This method, controls the Save button. It commits changes to a modified part and allows for saving a new part.
     RUNTIME ERROR: When initially programing this section, I was not quite sure how I was going to validate the form. My thoughts were that, by specifying the data type in the constructor, that it would validate its self.
     In return, when trying to save a different datatype than the constructor specified, a run time error occurred.
     */
    public void save(ActionEvent actionEvent) throws IOException {


        // Checks for error in form input
        try {

            //Throws NullPointerException if name field is empty or left blank.
            if (name.getText().isEmpty() || name.getText().isBlank()) {
                throw new NullPointerException();
            }

            //Throws NullPointerException exception if Company ID or Machine ID field is empty or left blank.
            if (sourced.getText().isEmpty() || sourced.getText().isBlank()) {
                throw new NullPointerException();
            }

            // Checks for too much inventory.
            if (Integer.parseInt(stock.getText()) > Integer.parseInt(max.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You are holding too much inventory," + "\n" + " please lower your stock levels.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            //Ensures maximum stock is greater than minimum.
            if (Integer.parseInt(max.getText()) < Integer.parseInt(min.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Maximum stock levels must " + "\n" + "be greater than minimum.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            // Ensures stock levels are greater than minimum required.
            if (Integer.parseInt(stock.getText()) < Integer.parseInt(min.getText())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Inventory levels must be greater than" + "\n" + "minimum.");
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
            else if ((Integer.parseInt(stock.getText()) >= Integer.parseInt(min.getText())) && (Integer.parseInt(stock.getText()) <= Integer.parseInt(max.getText()))) { // Checks for stock falling within Max and Min values

                // updatePart logic
                if (mod != null) {
                    // Allowing for updating of old part data.
                    if (inHouse.isSelected()) {
                        InHouse tempPart = new InHouse(mod.getId(), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt(min.getText()), Integer.parseInt(max.getText()), Integer.parseInt(sourced.getText()));
                        Inventory.updatePart(indexTransfer, tempPart); // Sending the above created part(tempPart) to the updatePart function.
                    } else

                        if (outSourced.isSelected()) {
                            Outsourced tempPart = new Outsourced(mod.getId(), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt(min.getText()), Integer.parseInt(max.getText()), sourced.getText());
                            Inventory.updatePart(indexTransfer, tempPart);// Sending the above created part(tempPart) to the updatePart function.
                        }

                } else

                    // Moving to addPart logic.
                    if (inHouse.isSelected()) {
                        InHouse inHousePart = new InHouse(assignPartId(Inventory.getAllParts()), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt(min.getText()), Integer.parseInt(max.getText()), Integer.parseInt(sourced.getText()));
                        Inventory.addPart(inHousePart);

                    } else {
                        Outsourced outSourcedPart = new Outsourced(assignPartId(Inventory.getAllParts()), name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(stock.getText()), Integer.parseInt((min.getText())), Integer.parseInt(max.getText()), sourced.getText());
                        Inventory.addPart(outSourcedPart);
                    }
                mod = null; // Resetting mod so part info does not continue to transfer
    // Returning home
                System.out.println("Returning Home");
                Parent root = FXMLLoader.load(getClass().getResource("../View_Controller/MainView.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene MainView = new Scene(root, 900, 600);
                stage.setTitle("Inventory Management System - Jake Dyke");
                stage.setScene(MainView);
                stage.show();

            }

        }
        catch (NumberFormatException e) {
            status.setFill(Paint.valueOf("#ff2727")); errorFeed.setText("Please fill in the form with the correct values.");
            timer = new Timer(); // Creating timer.
            timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).

            // Wont allow saving the form without filling out max and min values.
            if ((max.getText().isEmpty() || max.getText().isBlank()) || (min.getText().isEmpty() || min.getText().isBlank())) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Max and Min values are required.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            // Wont allow saving with out a price.
            if (price.getText().isEmpty() || price.getText().isBlank()) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You will need to set a price.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            //Setting default value or stock to 0 if left blank.
            if (stock.getText().isEmpty()) {
                stock.setText(Integer.toString(0));
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("Stock levels have been left blank," + "\n" + "so they have been set to the default value.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

        } catch (NullPointerException e) {

            //Used to catch blank string field.
            status.setFill(Paint.valueOf("#ff2727"));
            errorFeed.setText("Blank values are not allowed");
            timer = new Timer(); // Creating timer.
            timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).

            // Reporting blank part source from the null exception trow above.
            if (sourced.getText().isEmpty() || sourced.getText().isBlank()) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You have forgot to fill out a Machine ID" + "\n" + "or a Company Name");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }

            // Reporting blank name from the null exception throw above.
            if (name.getText().isEmpty() || name.getText().isBlank()) {
                status.setFill(Paint.valueOf("#ff2727"));
                errorFeed.setText("You have forgot to fill out a name.");
                timer = new Timer(); // Creating timer.
                timer.schedule(new errorRefresh(), 5000); //Calling timer task with a delay of 5000ms(5seconds).
            }
        }

    }

// Custom Menu Bar actions

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

    /**
     This controls the Minimize button and enables hiding the application to the start bar.
     */
    public void mini(MouseEvent actionEvent) {
        Stage stage = (Stage)((Circle)actionEvent.getSource()).getScene().getWindow(); // Using stage object to accept action event. THIS IS THE START OF WORKING CODE
        stage.setIconified(true); //  minimize into task bar, use boolean.
    }

    /**
     This assigns PartIds to Parts and ensures the ID is not duplicated.
     LOGIC ERROR: When creating this section, I initially thought that I could just increment the list by +1 but this did not work, part numbers were being duplicated. My solution was to bring the list of IDS
     and first check if it were empty.  If it were empty, I would start the list at 0 else wise, I would check for the max value and add 2 to it as I wanted only even part numbers.
     @param assignId - An observable list of Products.
     */
    public static int assignPartId(ObservableList<Part> assignId) {
        ArrayList<Integer> idList = new ArrayList<>(); // Declaring a Array of Integers to hold all Part IDs from allParts.
        if (assignId.isEmpty()) { return 0; } // Checks if there are any parts currently in the list. If the list is empty, automatically it starts with part 0
        else {
            for (Part temp : assignId) { //Looping through list of parts
                idList.add(temp.getId()); //Adding Part ID to idList Array.
            }
            int max = Collections.max(idList); // Getting max value from idList and assigning to max.
            return max + 2;//Creates unique even part numbers based on maximum value of previously added parts.
        }
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

