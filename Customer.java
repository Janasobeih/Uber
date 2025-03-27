import java.util.ArrayList;

public class Customer extends User {

    //ArrayList<Ride> rides =new ArrayList<>();

    public Customer(String username, String password) {
        super(username, password);
    }

    public boolean acceptFare(int ride)
    {
        //accepts the fare
        // ill hategly fel terminal list textual of drivers with fares

        // i need to send the accepted driver's number
        // Ride assignment server will do it

        return false;
    }

    public Ride requestRide(String pickup, String destination) {

        Ride ride = new Ride(pickup,destination,this);

        return ride;
    }
}
