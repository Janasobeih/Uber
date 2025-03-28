import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static final int PORT = 5000;  // change to 12345 if needed

    // Pre-created admin user.
    public static User admin = new User(0, "admin", "admin", "admin");

    // Lists to store different types of users and rides
    public static List<Customer> customers = new ArrayList<>();
    public static List<Driver> drivers = new ArrayList<>();
    public static List<User> users = new ArrayList<>();
    public static List<Ride> rides = new ArrayList<>();

    // Aggregated driver ratings record
    public static Map<Driver, DriverRatingRecord> driverRatings = new HashMap<>();

    // Keep track of all client handlers for notifications.
    public static List<ClientHandler> clientHandlers = new ArrayList<>();

    // Map to track which ride (by ID) a driver is currently locked to.
    public static Map<Driver, Integer> driverLockedRide = new HashMap<>();

    public static void main(String[] args) {
        // Add admin to the users list.
        users.add(admin);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler handler = new ClientHandler(socket);
                clientHandlers.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Notification methods.
    public static void broadcastToDrivers(String message) {
        for (ClientHandler handler : clientHandlers) {
            if (handler.currentUser != null &&
                    handler.currentUser.getUserType().equalsIgnoreCase("Driver")) {
                handler.out.println(message);
            }
        }
    }

    public static void notifyCustomer(String username, String message) {
        for (ClientHandler handler : clientHandlers) {
            if (handler.currentUser != null &&
                    handler.currentUser.getUserType().equalsIgnoreCase("Customer") &&
                    handler.currentUser.getUsername().equals(username)) {
                handler.out.println(message);
            }
        }
    }

    // Inner class to handle each client connection.
    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        User currentUser;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Welcome! Please login or register.");

                // Authentication loop.
                while (currentUser == null) {
                    out.println("1. Register");
                    out.println("2. Login");
                    out.println("Choose an option:");
                    String choice = in.readLine();
                    if ("1".equals(choice)) {
                        registerUser();
                    } else if ("2".equals(choice)) {
                        loginUser();
                    } else {
                        out.println("Invalid choice. Please enter 1 or 2.");
                    }
                }
                System.out.println("User logged in: " + currentUser.getUsername() +
                        " (" + currentUser.getUserType() + ") from " + socket.getInetAddress());

                // Route to the appropriate menu.
                if (currentUser.getUserType().equalsIgnoreCase("admin")) {
                    handleAdminRequests();
                } else if (currentUser.getUserType().equalsIgnoreCase("customer")) {
                    handleCustomerRequests();
                } else if (currentUser.getUserType().equalsIgnoreCase("driver")) {
                    handleDriverRequests();
                }
            } catch (IOException e) {
                System.err.println("Error with client " + socket.getInetAddress() + ": " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("Connection closed for client " + socket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void registerUser() throws IOException {
            out.println("Enter user type (Customer/Driver):");
            String userType = in.readLine();

            out.println("Enter username:");
            String username = in.readLine();

            out.println("Enter password:");
            String password = in.readLine();

            if (userType.equalsIgnoreCase("Customer")) {
                Customer customer = new Customer(users.size() + 1, username, password);
                users.add(customer);
                customers.add(customer);
                System.out.println("New customer registered: " + username + " from " + socket.getInetAddress());
            } else if (userType.equalsIgnoreCase("Driver")) {
                Driver driver = new Driver(users.size() + 1, username, password);
                users.add(driver);
                drivers.add(driver);
                System.out.println("New driver registered: " + username + " from " + socket.getInetAddress());
            } else {
                out.println("Invalid user type. Registration failed.");
                System.out.println("Registration failed for client " + socket.getInetAddress() + " due to invalid user type.");
                return;
            }
            out.println("Registration successful! You can now log in.");
        }

        private void loginUser() throws IOException {
            out.println("Enter username:");
            String username = in.readLine();

            out.println("Enter password:");
            String password = in.readLine();

            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    currentUser = user;
                    out.println("Login successful! Welcome, " + username + "!");
                    return;
                }
            }
            out.println("Error: Invalid username or password. Try again.");
            System.out.println("Failed login attempt for username: " + username + " from " + socket.getInetAddress());
        }

        // ----- Admin Menu -----
        private void handleAdminRequests() throws IOException {
            while (true) {
                out.println("\nAdmin Menu:");
                out.println("1. View Overall Status");
                out.println("2. Disconnect");
                out.println("Enter your choice:");
                String choice = in.readLine();
                if (choice == null || choice.equals("2")) {
                    out.println("Disconnecting...");
                    System.out.println("Admin " + currentUser.getUsername() + " disconnected.");
                    break;
                }
                switch (choice) {
                    case "1":
                        viewOverallStatus();
                        break;
                    default:
                        out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        }

        private void viewOverallStatus() throws IOException {
            out.println("\n--- Overall System Status ---");
            out.println("Total Customers: " + customers.size());
            out.println("Total Drivers: " + drivers.size());
            out.println("Total Rides: " + rides.size());
            if (!rides.isEmpty()) {
                for (Ride ride : rides) {
                    String driverInfo = (ride.getDriver() == null) ? "None" : ride.getDriver().getUsername();
                    out.println("Ride ID: " + ride.getId()
                            + " | Customer: " + ride.getCustomer().getUsername()
                            + " | Pickup: " + ride.getPickupLocation()
                            + " | Destination: " + ride.getDestination()
                            + " | Status: " + ride.getStatus()
                            + " | Driver: " + driverInfo);
                }
            }
            out.println("\n--- Driver Ratings ---");
            if (drivers.isEmpty()) {
                out.println("No drivers found.");
            } else {
                for (Driver d : drivers) {
                    DriverRatingRecord record = driverRatings.get(d);
                    double avgRating = (record == null) ? 0.0 : record.getAverageRating();
                    out.println("Driver: " + d.getUsername() + " | Average Rating: " + avgRating);
                }
            }
            out.println("------------------------------");
        }

        // ----- Customer Menu -----
        private void handleCustomerRequests() throws IOException {
            while (true) {
                out.println("\nCustomer Menu:");
                out.println("1. Request Ride");
                out.println("2. View Ride Status");
                out.println("3. View Offered Fares");
                out.println("4. Accept an Offer");
                out.println("5. Rate Driver");
                out.println("6. Disconnect");
                out.println("Enter your choice:");
                String choice = in.readLine();
                if (choice == null || choice.equals("6")) {
                    out.println("Disconnecting...");
                    System.out.println("Customer " + currentUser.getUsername() + " disconnected.");
                    break;
                }
                switch (choice) {
                    case "1":
                        requestRide();
                        break;
                    case "2":
                        viewRideStatus();
                        break;
                    case "3":
                        viewOfferedFares();
                        break;
                    case "4":
                        acceptOffer();
                        break;
                    case "5":
                        rateDriver();
                        break;
                    default:
                        out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        }

        // ----- Driver Menu -----
        private void handleDriverRequests() throws IOException {
            while (true) {
                out.println("\nDriver Menu:");
                out.println("1. Set Availability");
                out.println("2. View All Pending Rides");
                out.println("3. Offer Fare");
                out.println("4. Update Ride Status (In Progress/Completed)");
                out.println("5. Disconnect");
                out.println("Enter your choice:");
                String choice = in.readLine();
                if (choice == null || choice.equals("5")) {
                    out.println("Disconnecting...");
                    System.out.println("Driver " + currentUser.getUsername() + " disconnected.");
                    break;
                }
                switch (choice) {
                    case "1":
                        setDriverAvailability();
                        break;
                    case "2":
                        viewAllPendingRides();
                        break;
                    case "3":
                        offerFare();
                        break;
                    case "4":
                        updateRideStatus();
                        break;
                    default:
                        out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        }

        private void requestRide() throws IOException {
            out.println("Enter pickup location:");
            String pickupLocation = in.readLine();

            out.println("Enter destination:");
            String destination = in.readLine();

            Customer customer = (Customer) currentUser;
            Ride ride = new Ride(customer, pickupLocation, destination, "Pending");
            rides.add(ride);

            out.println("Ride request submitted. Ride ID: " + ride.getId()
                    + " from " + pickupLocation + " to " + destination);
            System.out.println("Customer " + currentUser.getUsername() + " requested ride ID "
                    + ride.getId() + " from " + pickupLocation + " to " + destination);

            // Broadcast to all drivers.
            String broadcastMessage = "New Ride Requested! Ride ID: " + ride.getId()
                    + " | Pickup: " + pickupLocation
                    + " | Destination: " + destination;
            broadcastToDrivers(broadcastMessage);
        }

        private void setDriverAvailability() throws IOException {
            out.println("Set availability (yes/no):");
            String response = in.readLine();
            if (response.equalsIgnoreCase("yes")) {
                for (Driver driver : drivers) {
                    if (driver.getUsername().equals(currentUser.getUsername())) {
                        driver.setAvailable(true);
                        out.println("You are now available for rides.");
                        System.out.println("Driver " + currentUser.getUsername() + " set as available.");
                        return;
                    }
                }
                out.println("Driver record not found.");
            } else {
                out.println("Availability not updated.");
            }
        }

        private void viewRideStatus() throws IOException {
            boolean found = false;
            for (Ride ride : rides) {
                if (ride.getCustomer().getUsername().equals(currentUser.getUsername())) {
                    out.println("Ride ID: " + ride.getId()
                            + " | Pickup: " + ride.getPickupLocation()
                            + " | Destination: " + ride.getDestination()
                            + " | Status: " + ride.getStatus());
                    found = true;
                }
            }
            if (!found) {
                out.println("No active rides found.");
            }
        }

        private void viewAllPendingRides() throws IOException {
            boolean found = false;
            for (Ride ride : rides) {
                if (ride.getStatus().equalsIgnoreCase("Pending")) {
                    out.println("Ride ID: " + ride.getId()
                            + " | Customer: " + ride.getCustomer().getUsername()
                            + " | Pickup: " + ride.getPickupLocation()
                            + " | Destination: " + ride.getDestination()
                            + " | Status: " + ride.getStatus());
                    found = true;
                }
            }
            if (!found) {
                out.println("No pending rides at the moment.");
            }
        }

        // ----- Driver: Offer Fare -----
        private void offerFare() throws IOException {
            Driver thisDriver = (Driver) currentUser;
            // Check if this driver is already locked to a ride.
            if (Server.driverLockedRide.containsKey(thisDriver)) {
                int lockedRideId = Server.driverLockedRide.get(thisDriver);
                out.println("You are already offering a fare for ride ID " + lockedRideId + ". Please wait until it is resolved.");
                return;
            }

            out.println("Enter ride ID to offer a fare for:");
            String rideIdStr = in.readLine();
            int rideId;
            try {
                rideId = Integer.parseInt(rideIdStr);
            } catch (NumberFormatException e) {
                out.println("Invalid ride ID format.");
                return;
            }
            // Find the ride.
            Ride targetRide = null;
            for (Ride ride : rides) {
                if (ride.getId() == rideId) {
                    targetRide = ride;
                    break;
                }
            }
            if (targetRide == null) {
                out.println("Ride not found.");
                return;
            }
            if (!targetRide.getStatus().equalsIgnoreCase("Pending")) {
                out.println("Cannot offer fare on a ride that is not pending.");
                return;
            }
            out.println("Enter your fare offer (e.g., 25.50):");
            double fare;
            try {
                fare = Double.parseDouble(in.readLine());
            } catch (NumberFormatException e) {
                out.println("Invalid fare amount.");
                return;
            }
            // Create a new offer.
            RideOffer offer = new RideOffer(thisDriver, fare);
            targetRide.getOffers().add(offer);
            out.println("Your fare offer of $" + fare + " for ride ID " + targetRide.getId() + " has been submitted.");
            System.out.println("Driver " + thisDriver.getUsername() + " offered $" + fare + " for ride ID " + targetRide.getId());

            // Lock this driver to this ride.
            Server.driverLockedRide.put(thisDriver, rideId);

            // Notify the customer.
            notifyCustomer(targetRide.getCustomer().getUsername(),
                    "Driver " + thisDriver.getUsername() + " has offered $" + fare + " for your ride (ID " + targetRide.getId() + ").");
        }

        // ----- Customer: View Offered Fares -----
        private void viewOfferedFares() throws IOException {
            out.println("Enter your ride ID to view offers:");
            String rideIdStr = in.readLine();
            int rideId;
            try {
                rideId = Integer.parseInt(rideIdStr);
            } catch (NumberFormatException e) {
                out.println("Invalid ride ID format.");
                return;
            }
            Ride targetRide = null;
            for (Ride ride : rides) {
                if (ride.getId() == rideId && ride.getCustomer().getUsername().equals(currentUser.getUsername())) {
                    targetRide = ride;
                    break;
                }
            }
            if (targetRide == null) {
                out.println("Ride not found or you are not the customer for this ride.");
                return;
            }
            if (targetRide.getOffers().isEmpty()) {
                out.println("No offers have been made for this ride yet.");
                return;
            }
            out.println("Offers for ride ID " + targetRide.getId() + ":");
            for (RideOffer offer : targetRide.getOffers()) {
                out.println("Driver: " + offer.getDriver().getUsername() + " | Fare: $" + offer.getFare());
            }
        }

        // ----- Customer: Accept an Offer -----
        private void acceptOffer() throws IOException {
            out.println("Enter your ride ID for which you want to accept an offer:");
            String rideIdStr = in.readLine();
            int rideId;
            try {
                rideId = Integer.parseInt(rideIdStr);
            } catch (NumberFormatException e) {
                out.println("Invalid ride ID format.");
                return;
            }
            // Find the ride.
            Ride targetRide = null;
            for (Ride ride : rides) {
                if (ride.getId() == rideId && ride.getCustomer().getUsername().equals(currentUser.getUsername())) {
                    targetRide = ride;
                    break;
                }
            }
            if (targetRide == null) {
                out.println("Ride not found or you are not the customer for this ride.");
                return;
            }
            if (targetRide.getOffers().isEmpty()) {
                out.println("No offers available for this ride.");
                return;
            }
            out.println("Offers for ride ID " + targetRide.getId() + ":");
            for (RideOffer offer : targetRide.getOffers()) {
                out.println("Driver: " + offer.getDriver().getUsername() + " | Fare: $" + offer.getFare());
            }
            out.println("Enter the username of the driver whose offer you want to accept:");
            String driverUsername = in.readLine();
            RideOffer acceptedOffer = null;
            for (RideOffer offer : targetRide.getOffers()) {
                if (offer.getDriver().getUsername().equals(driverUsername)) {
                    acceptedOffer = offer;
                    break;
                }
            }
            if (acceptedOffer == null) {
                out.println("Offer from driver " + driverUsername + " not found.");
                return;
            }
            // Accept the offer.
            targetRide.setDriver(acceptedOffer.getDriver());
            targetRide.setStatus("Accepted");
            out.println("You have accepted the offer from driver " + driverUsername + " for ride ID " + targetRide.getId() + ".");
            System.out.println("Customer " + currentUser.getUsername() + " accepted the offer from driver " + driverUsername + " for ride ID " + targetRide.getId());

            // Notify the accepted driver.
            for (ClientHandler handler : clientHandlers) {
                if (handler.currentUser != null &&
                        handler.currentUser.getUserType().equalsIgnoreCase("Driver") &&
                        handler.currentUser.getUsername().equals(driverUsername)) {
                    handler.out.println("Your fare offer for ride ID " + targetRide.getId() + " has been accepted by the customer.");
                }
            }
            // Free all other drivers who offered for this ride.
            for (RideOffer offer : targetRide.getOffers()) {
                if (!offer.getDriver().getUsername().equals(driverUsername)) {
                    Server.driverLockedRide.remove(offer.getDriver());
                    for (ClientHandler handler : clientHandlers) {
                        if (handler.currentUser != null &&
                                handler.currentUser.getUserType().equalsIgnoreCase("Driver") &&
                                handler.currentUser.getUsername().equals(offer.getDriver().getUsername())) {
                            handler.out.println("Ride ID " + targetRide.getId() + " has been assigned to another driver.");
                        }
                    }
                }
            }
            // Clear offers.
            targetRide.getOffers().clear();
        }

        // ----- Customer: Rate Driver -----
        private void rateDriver() throws IOException {
            out.println("Enter driver username to rate:");
            String driverUsername = in.readLine();

            // Check if the customer had a completed ride with this driver.
            boolean eligible = false;
            for (Ride ride : rides) {
                if (ride.getCustomer().getUsername().equals(currentUser.getUsername()) &&
                        ride.getDriver() != null &&
                        ride.getDriver().getUsername().equals(driverUsername) &&
                        ride.getStatus().equalsIgnoreCase("Completed")) {
                    eligible = true;
                    break;
                }
            }
            if (!eligible) {
                out.println("You can only rate drivers with whom you've completed a ride.");
                return;
            }

            out.println("Enter rating (1-5):");
            float rating;
            try {
                rating = Float.parseFloat(in.readLine());
            } catch (NumberFormatException e) {
                out.println("Invalid rating value.");
                return;
            }
            for (Driver driver : drivers) {
                if (driver.getUsername().equals(driverUsername)) {
                    // Retrieve or create the rating record.
                    DriverRatingRecord record = driverRatings.get(driver);
                    if (record == null) {
                        record = new DriverRatingRecord();
                        driverRatings.put(driver, record);
                    }
                    record.addRating(rating);
                    double avg = record.getAverageRating();
                    out.println("Driver " + driverUsername + " now has an average rating of " + avg + " stars.");
                    System.out.println("Customer " + currentUser.getUsername() +
                            " rated driver " + driverUsername + " with " + rating +
                            " stars. New average: " + avg);
                    return;
                }
            }
            out.println("Driver not found.");
        }
        private void updateRideStatus() throws IOException {
            out.println("Enter ride ID to update:");
            String rideIdStr = in.readLine();
            int rideId;
            try {
                rideId = Integer.parseInt(rideIdStr);
            } catch (NumberFormatException e) {
                out.println("Invalid ride ID format.");
                return;
            }
            for (Ride ride : rides) {
                // Only allow update if the ride is assigned to this driver.
                if (ride.getId() == rideId) {
                    if (ride.getDriver() == null || !ride.getDriver().getUsername().equals(currentUser.getUsername())) {
                        out.println("You are not assigned to this ride.");
                        return;
                    }
                    out.println("Enter new status (In Progress/Completed):");
                    String newStatus = in.readLine();
                    ride.setStatus(newStatus);
                    out.println("Ride status updated to: " + newStatus);
                    System.out.println("Driver " + currentUser.getUsername()
                            + " updated ride ID " + rideId + " to " + newStatus);
                    // If the ride is completed, free this driver.
                    if (newStatus.equalsIgnoreCase("Completed")) {
                        Server.driverLockedRide.remove((Driver) currentUser);
                    }
                    return;
                }
            }
            out.println("Ride not found.");
        }

    }
}
