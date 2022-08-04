package Model;


/**
 This class extends the Part class, to allow for the additional field companyName.
 */
public class Outsourced extends Part {
    private String companyName;

    public Outsourced(int id, String name, double price, int stock, int min, int max, String companyName) {
        super(id, name, price, stock, min, max);
        this.companyName = companyName; }

    /**
     @return the companyName
     */
    public String getCompanyName() { return companyName; }

    /**
     @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) { this.companyName = companyName; }

}
