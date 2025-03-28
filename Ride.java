import java.util.*;

public class Ride {
    private static int counter = 1;
    private int id;
    private Customer customer;
    private String pickupLocation;
    private String destination;
    private String status;
    private Driver driver;  // The accepted driver (if any)
    private List<RideOffer> offers = new ArrayList<>(); // List of fare offers

    public Ride(Customer customer, String pickupLocation, String destination, String status) {
        this.id = counter++;
        this.customer = customer;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<RideOffer> getOffers() {
        return offers;
    }
}
