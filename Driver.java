import java.util.ArrayList;
import java.util.Scanner;

public class Driver extends User
{
    boolean statusAvailable;
    Ride ride;

    public Driver(String username, String password) {
        super(username, password);
    }


public void offerFare(int rideID , float fare)
{
    //write the logic of sending the fare to the user
}

public void updateRideStatus(Ride ride , String status)
{
        ride.setRideStatus(status);
}

}
