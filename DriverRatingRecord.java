public class DriverRatingRecord {
    private double totalRating;
    private int count;

    public DriverRatingRecord() {
        this.totalRating = 0.0;
        this.count = 0;
    }

    public void addRating(double rating) {
        totalRating += rating;
        count++;
    }

    public double getAverageRating() {
        return (count == 0) ? 0.0 : totalRating / count;
    }
}
