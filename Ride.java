import java.util.ArrayList;

public class Ride {
    //int id;
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
        this.rideStatus="pending";
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
