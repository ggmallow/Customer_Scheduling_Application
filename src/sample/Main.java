package sample;

import Model.*;
import View_Controller.AddPartController;
import View_Controller.AddProdController;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



/**
  This is the main method.  This method launches the initial view(MainView.fxml).
 FUTURE ENHANCEMENTS: Through out the creation of this application, I continuously thought that there was would be a way to create an invoice for a potential customer. To my surprise, there was not and I feel this would be a valuable future enhancement.
 By adding calculations of different data types like float or double, you can see how they behave differently with out much additional work.
 */
public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{


        System.out.println("Welcome to the program.");
        Parent root = FXMLLoader.load(getClass().getResource("../View_Controller/MainView.fxml"));
        primaryStage.setTitle("Inventory Management System - Jake Dyke");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.initStyle(StageStyle.UNDECORATED); // removes menu bar for custom menu.
        Image getIcon = new Image("View_Controller/48x48.png"); //Grabbing the custom icon.
        primaryStage.getIcons().add(getIcon); // Setting the custom icon.
        root.getStylesheets().add(getClass().getResource("../View_Controller/style.css").toString());
        primaryStage.show();


    }

}
