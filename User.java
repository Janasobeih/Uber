public class User
{
    String username;
    String password;
    UserType userType;

    public User(String username , String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
}


enum UserType {
    CUSTOMER("customer"),
    DRIVER("driver");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserType fromInt(int value) {
        return switch (value) {
            case 1 -> CUSTOMER;
            case 2 -> DRIVER;
            default -> throw new IllegalArgumentException("Invalid input! Enter 1 for Customer or 2 for Driver.");
        };
}
}


