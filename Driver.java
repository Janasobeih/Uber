public class Driver extends User {
    private boolean available = false;

    public Driver(int id, String username, String password) {
        super(id, username, password, "Driver");
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
