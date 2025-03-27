import java.util.ArrayList;
import java.util.Scanner;

public class Driver extends User
{
    boolean statusAvailable;
    Ride ride;

    public Driver(String username, String password) {
        super(username, password);
    }

    public ArrayList<Ride> unreservedRides( ArrayList<Ride> rides)
    {
        ArrayList<Ride> availableRides=new ArrayList<>();
        for(Ride ride : rides)
        {
            if(ride.getStatus().equals("unreserved"))
            {
                availableRides.add(ride);
            }
        }

        System.out.println("Choose one of the rides to offer a fare");

        for(int i=1;i<=availableRides.size();i++)
        {
            System.out.print(i);
            availableRides.get(i).printRideDetails();
        }

        return availableRides;

    }

public void offerFare(Ride ride , float fare)
{
    //write the logic of sending the fare to the user
}

public void updateRideStatus(Ride ride , String status)
{
        ride.setRideStatus(status);
}

}
