public class RideOffer {
    private Driver driver;
    private double fare;

    public RideOffer(Driver driver, double fare) {
        this.driver = driver;
        this.fare = fare;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getFare() {
        return fare;
    }
}
