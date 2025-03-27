import java.util.ArrayList;

public class Ride {
    String pickup;
    String destination;
    String rideStatus;
    Driver driver;
    Customer customer;
    float rideFare;

    public Ride(String pickup, String destination, Customer customer) {
        this.pickup = pickup;
        this.destination = destination;
        this.customer = customer;
        this.rideStatus="unreserved";
    }
    public Ride(){
        this.pickup = "";
        this.destination = "";
        this.customer = null;
        this.rideStatus=null;
    }
    public String getStatus()
    {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public void requestRide(User loggedInUser, String pickup, String destination, ArrayList<Driver> availableDrivers) {
        this.pickup = pickup;
        this.destination = destination;

        // Check if drivers are available
        if (availableDrivers.isEmpty()) {
            System.out.println("No drivers are currently available. Please try again later.");
            return;
        }

        // Notify available drivers
        System.out.println("Ride request from " + loggedInUser.username + ":");
        System.out.println("Pickup: " + pickup);
        System.out.println("Destination: " + destination);
        System.out.println("Notifying available drivers...");

        for (Driver driver : availableDrivers) {
            System.out.println("Driver " + driver.username + " has been notified.");
        }
    }

    public void rideAssignment(Driver driver , Customer customer , String pickup , String destination)
    {
        driver.statusAvailable=false;

        //sends to both that ride has been assigned

    }

    public ArrayList<Driver> availableDrivers(ArrayList<Driver>drivers)
    {
        ArrayList<Driver> availableDrivers = new ArrayList<>();
        for(Driver driver : drivers)
        {
            if(driver.statusAvailable)
            {
                availableDrivers.add(driver);
            }
        }

        return availableDrivers;
    }



    public void printRideDetails()
    {
        System.out.print("Customer Name : " + customer.getUserName());
        System.out.print("Pickup Location : " + pickup);
        System.out.print("Destination Location : " + destination);


    }
}
