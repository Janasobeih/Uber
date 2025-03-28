import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public Client(String host, int port) {
        try {
            socket = new Socket(host, port);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to UberApp server");
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        try {
            // Print the welcome message from the server.
            System.out.println(in.readLine());
            // Run the authentication loop until login is successful.
            handleAuthentication();
            // Continue with the application.
            handleApplication();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    private void handleAuthentication() throws IOException {
        boolean authenticated = false;
        while (!authenticated) {
            for (int i = 0; i < 3; i++) {
                System.out.println(in.readLine());
            }
            System.out.print("> ");
            String choice = scanner.nextLine();
            out.println(choice);

            if (choice.equals("1")) {
                handleRegistration();
                // After registration, we simply notify the user and continue the loop.
                System.out.println("Registration complete. Please choose option 2 to log in.");
            } else if (choice.equals("2")) {
                authenticated = handleLogin();
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private boolean handleLogin() throws IOException {
        System.out.println(in.readLine()); // "Enter username:"
        System.out.print("> ");
        out.println(scanner.nextLine());

        System.out.println(in.readLine()); // "Enter password:"
        System.out.print("> ");
        out.println(scanner.nextLine());

        String result = in.readLine(); // Login result message from server.
        System.out.println(result);
        return result.startsWith("Login successful");
    }

    private void handleRegistration() throws IOException {
        System.out.println(in.readLine()); // "Enter user type (Customer/Driver):"
        System.out.print("> ");
        out.println(scanner.nextLine());

        System.out.println(in.readLine()); // "Enter username:"
        System.out.print("> ");
        out.println(scanner.nextLine());

        System.out.println(in.readLine()); // "Enter password:"
        System.out.print("> ");
        out.println(scanner.nextLine());

        System.out.println(in.readLine());
    }

    private void handleApplication() throws IOException {
        while (true) {
            String line = in.readLine();
            if (line == null) break;
            System.out.println(line);
            if (line.startsWith("Enter") ||
                    line.startsWith("Set availability") ||
                    line.startsWith("Enter pickup location") ||
                    line.startsWith("Enter destination") ||
                    line.startsWith("Enter ride ID") ||
                    line.startsWith("Enter new status") ||
                    line.startsWith("Enter driver username") ||
                    line.startsWith("Enter rating")) {
                System.out.print("> ");
                String input = scanner.nextLine();
                out.println(input);
            }

            if (line.contains("Disconnecting...")) {
                break;
            }
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", Server.PORT);
        client.start();
    }
}
