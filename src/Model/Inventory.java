package Model;

import View_Controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 This class extends the MainController and holds both the inventory of allParts and allProducts. This class, contains methods for manipulating inventory.
 */
public class Inventory extends MainController {
    public static final ObservableList<Part> allParts = FXCollections.observableArrayList(); // Declaring a ObservableList of Parts to use globally.
    public static final ObservableList<Product> allProducts = FXCollections.observableArrayList(); // Declaring a ObservableList of Products to use globally.

    //Part methods
    /**
     This method will return the Observable allParts.
     @return the list of allParts
     */
    public static ObservableList<Part> getAllParts() { return allParts; }

    /**
     This method will add newPart to the ObservableList allParts.
     @param newPart the part to add.
     */
    public static void addPart(Part newPart) {
        System.out.println("Calling addPart");
        allParts.add(newPart);

    }

    /**
     This method checks if a selectedPart contains a part, if it does not, it reports an error but if it contains a part, it adds selectedPart to ObservableList allParts.
     @param selectedPart the selectedPart to remove.
     */
    public static void deletePart(Part selectedPart) {

        // Removes part from the observable array list named allParts
        Inventory.allParts.remove(selectedPart);
        System.out.println("Deleting" + " " + selectedPart.getName());

    }

    /**
     This method, looks up a part by partId, if the search criteria matches than a part is returned to the handler.
     @param partId an integer passed from the MainController, during a search.
     @return the part that is found by partId.
     */
    public static Part lookupPart(int partId) {
        Part filterById = null; // Declaring an empty part to hold the part that is being searched for.
        System.out.println("ID Search"); // Following my code.
        for (Part byId: allParts) {  // Looping through parts list
            if (byId.getId() == partId) { // Checking if loop variable equals search criteria passed.
                filterById = byId; // Setting found Part to filterByID Part object.
            }
        }

        System.out.println("About to return"); // Following my code.
        return filterById; // returning part to handler, to display in table.
    }

    /**
     This method, looks up a part by partName.
     @param partName a String passed from the MainController, during a search.
     @return a list of parts(filteredParts).
     */
    public static ObservableList<Part> lookupPart(String partName) {

        ObservableList<Part> filteredParts = FXCollections.observableArrayList(); // Declaring a list to hold the filtered parts

        for (Part nameSearch: allParts) {  // Looping through Parts
            if (nameSearch.getName().toLowerCase().contains(partName.toLowerCase())) { // Comparing values of nameSearch(partSearch.getText():), we change them to lowercase so the search value does not need to be case sensitive.
                filteredParts.add(nameSearch); // Adding filtered parts to Observable list named filtered parts.
                break;
            }

        }

        return filteredParts; // Returning an Observable list to handler.

    }

    /**
     This method, will update an existing part.
     @param index the index of the Part being updated.
     @param selectedPart the modified Part passed from the AddPartController.
     */
    public static void updatePart(int index, Part selectedPart) {
        System.out.println("In updatePart");
        Inventory.allParts.set(index,selectedPart);

    }

    //Product methods
    /**
     This method returns the ObservableList allProducts.
     @return the ObservableList allProducts.
     */
    public static ObservableList<Product> getAllProducts() { return allProducts; }

    /**
     This method adds a Product to the ObservableList allProducts.
     @param newProduct the Product to be added.
     */
    public static void addProduct(Product newProduct) {
        System.out.println("You are about to add a product named " + newProduct.getName());
        allProducts.add(newProduct);

    }

    /**
     This method deletes a Product from the ObservableList allProducts.
     @param selectedProduct the product to be deleted.
     */
    public static void deleteProduct(Product selectedProduct) {
        allProducts.remove(selectedProduct);
        System.out.println("You have deleted " + selectedProduct.getName());
    }

    /**
     This method will look up a Product by productID.
     @param productID a integer passed from the MainController, during a search.
     @return a list of Products(filterById).
     */
    public static Product lookupProduct(int productID) {
        Product filterById = null; // Declaring an empty part to hold the part that is being searched for.
        System.out.println("ID Search"); // Following my code.
        for (Product byId: allProducts) {  // Looping through parts list
            if (byId.getId() == productID) { // Checking if loop variable equals search criteria passed.
                filterById = byId; // Setting found Part to filterByID Part object.
            }
        }

        return filterById; // returning product to handler, to display in table.

    }

    /**
     This method will look up a Product by productID.
     @param productName a String passed from the MainController, during a search.
     @return a list of Products(filteredProducts).
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> filteredProducts = FXCollections.observableArrayList(); // Declaring a list to hold the filtered products
        System.out.println("Name Search"); //Outputting to console to follow code

        for (Product nameSearch: allProducts) {  // Looping through Products
            if (nameSearch.getName().toLowerCase().contains(productName.toLowerCase())) { // Comparing values of nameSearch(productSearch.getText():), we change them to lowercase so the search value does not need to be case sensitive.
                System.out.println("Entered loop"); // Ensuring I entered loop
                filteredProducts.add(nameSearch); // Adding filtered parts to Observable list named filtered parts.
                break;
            }

        }

        return filteredProducts; // Returning an Observable list to handler.
    }

    /**
     This method, will update an existing part.
     @param index the index of the Product being updated.
     @param selectedProduct the modified Product passed from the AddProductController.
     */
    public static void updateProduct(int index, Product selectedProduct) {
        Inventory.allProducts.set(index, selectedProduct);
    }

}

