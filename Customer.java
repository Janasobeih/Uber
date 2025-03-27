import java.util.ArrayList;

public class Customer extends User {

    ArrayList<Ride> rides =new ArrayList<>();

    public Customer(String username, String password) {
        super(username, password);
    }

    public boolean acceptFare(Ride ride)
    {
        //accepts the fare
        return false;
    }

}
