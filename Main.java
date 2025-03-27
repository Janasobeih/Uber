import java.util.ArrayList;
import java.util.Scanner;

 //implement multi-threading

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        User loggedInUser = null;
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Driver> drivers = new ArrayList<>();
        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<Ride> rides = new ArrayList<>();

        boolean continueLoop = true;

        while (continueLoop) {
            System.out.println("Welcome!");
            System.out.println("Please enter 1 to login or 2 to register");

            int userInput = getValidInt(scanner);

            // Registering a new user
            if (userInput == 2) {
                boolean isRunning = true;
                String username = "";

                while (isRunning) {
                    System.out.println("Enter your username:");
                    username = scanner.nextLine().trim();

                    boolean usernameExists = false;
                    for (User user : users) {
                        if (user.username.equals(username)) {
                            System.out.println("This username already exists. Try another one.");
                            usernameExists = true;
                            break;
                        }
                    }

                    if (!usernameExists) {
                        isRunning = false;
                    }
                }

                System.out.println("Enter your password:");
                String password = scanner.nextLine().trim();

                System.out.println("Enter your type: 1 for customer, 2 for driver");
                int type = getValidInt(scanner);

                try {
                    User newUser;
                    UserType userType = UserType.fromInt(type);

                    if (userType == UserType.CUSTOMER) {
                        newUser = new Customer(username, password);
                        customers.add((Customer) newUser);
                    }
                    else
                    {
                        newUser = new Driver(username, password);
                        drivers.add((Driver) newUser);

                    }

                    users.add(newUser);
                    System.out.println("User Created Successfully!");

                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }


            // Logging in
            else if (userInput == 1) {
                boolean isRunning = true;

                while (isRunning) {
                    System.out.println("Enter your username:");
                    String username = scanner.nextLine().trim();
                    System.out.println("Enter your password:");
                    String password = scanner.nextLine().trim();

                    boolean userFound = false;

                    for (User user : users) {
                        if (user.username.equals(username)) {
                            userFound = true;
                            if (user.password.equals(password)) {
                                System.out.println("You've logged in successfully!");
                                loggedInUser = user;
                                isRunning = false;
                                continueLoop = false;
                                break;
                            } else {
                                System.out.println("Wrong password, please try again.");
                            }
                        }
                    }

                    if (!userFound) {
                        System.out.println("User not found, register first.");
                        isRunning = false;
                    }
                }
            }

            // Invalid input
            else {
                System.out.println("Invalid input, please enter 1 or 2.");
            }
        }

        //Customer Functionalities in case logged-in user is a customer

        if (loggedInUser.userType == UserType.CUSTOMER) {
            boolean isRunning=true;
            Ride requestedRide = new Ride();
            while(isRunning)
            {
                System.out.println("1. Request a ride by entering a pickup location and destination. \n" +
                        "2. View the current status of the requested ride. \n" +
                        "3. Disconnect from the server. ");
                int userInput = getValidInt(scanner);
                if(userInput==1)
                {
                    System.out.println("Pickup Location :");
                    String pickup =scanner.nextLine();
                    System.out.println("Destination Location :");
                    String destination =scanner.nextLine();
                    requestedRide = ((Customer) loggedInUser).requestRide(pickup,destination);

                    //send it to the server
                    //ha recieve men el server fares
                    //then send the server the accepted fare
                    //server ye3ml ride assignment
                    //driver ye2dar ye update status

                   // rides.add(ride);

                }

                else if(userInput==2)
                {
                    System.out.println(requestedRide.getStatus());
                    //atlobha men el server
                }

                else
                {
                    //disconnect from server
                }
            }
        }


        // Driver Functionalities incase logged-in user is a Driver

        if (loggedInUser.userType == UserType.DRIVER) {
            Driver driver = (Driver) loggedInUser;
            Ride ride = new Ride();
            boolean isRunning=true;
            while(isRunning)
            {
                System.out.println("1. Offer a fare for a ride request. \n" +
                        "2. Send status updates of the ride they have been assigned to (start or \n" +
                        "finish ride). \n" +
                        "3. Disconnect from the server. ");
            }

            int userInput = getValidInt(scanner);
            if(userInput==1)
            {
                //waiting for el available rides
                System.out.println("Your preferable ride is : ");
                int rideNumber=scanner.nextInt();
                //Ride chosenRide= availableRides.get(rideNumber + 1);
                System.out.println("Enter your preferred fee");
                float fare= scanner.nextFloat();
                //sned the server the chosen ride bel number beta3ha we el fare
                driver.offerFare(rideNumber,fare);

            }
            else if (userInput==2)
            {
                System.out.println("Enter the new Status of your ride");
                String updatedRideStatus= scanner.nextLine();
                driver.updateRideStatus(/*accepted ride */ride,updatedRideStatus);
                //should send to the server status so that lama el client ye request status tegelo men el server
            }

            else
            {
                //disconnect from server
            }

        }


        scanner.close(); // Close scanner at the end
    }





    // Method to handle integer input validation using try-catch
    private static int getValidInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}



