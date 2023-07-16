import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    /* database variables */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/uprise-sacco";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";

    /* login --edwin */
    public static ResultSet login(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM member WHERE username='" + username + "' AND password='" + password + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet recoverPassword(String memberNumber, String phoneNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM member WHERE member_number='" + memberNumber + "' AND phone_number='"
                    + phoneNumber + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* deposit method --Vanessa */

    /* checkStatement --pius */

    /* loan request -- allan */

    /* loan request status -- taras */

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server is running. Waiting for a client to connect...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            boolean isLoggedIn = false; // Track user login status
            int member_id = 0;
            double account_balance = 0.0;
            double loan_balance = 0.0;

            while ((inputLine = in.readLine()) != null) {
                String[] command = inputLine.split(" ");
                if (command.length > 1 && command.length < 5) {
                    switch (command[0]) {
                        case "login":
                            if (!isLoggedIn && login(command[1], command[2]) != null) {
                                ResultSet resultSet = login(command[1], command[2]);
                                try {
                                    if (resultSet.next()) {
                                        member_id = resultSet.getInt("member_number");
                                        account_balance = resultSet.getDouble("account_balance");
                                        loan_balance = resultSet.getDouble("loan_balance");
                                    }
                                    ;
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                out.println("Successfully logged in");
                                isLoggedIn = true;
                            } else if (isLoggedIn) {
                                out.println("You are already logged in.");
                            } else {
                                out.println("login failed");
                            }
                            break;
                        case "forgotPassword":
                            if(!isLoggedIn) {
                                if (recoverPassword(command[1], command[2]) != null) {
                                    out.println("Your password is: " + recoverPassword(command[1], command[2])
                                            + ". Use the login command to login now.");
                                } else {
                                    out.println("No records found. Return after a day");
                                }
                            } else {
                                out.println("You are already logged in.");
                            }
                            break;
                        case "deposit":
                            if (isLoggedIn) {
                                out.println("Processing deposit for member ID: " + member_id);
                                /* call the deposit method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "CheckStatement":
                            if (isLoggedIn) {
                                out.println("Checking statement for member ID: " + member_id);
                                /* call the CheckStatement method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "requestLoan":
                            if (isLoggedIn) {
                                out.println("Processing loan request for member ID: " + member_id);
                                /* call the requestLoan method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "LoanRequestStatus":
                            if (isLoggedIn) {
                                out.println("Checking loan request status for member ID: " + member_id);
                                /* call the LoanRequestStatus method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        default:
                            out.println("Invalid command");
                    }
                } else {
                    out.println("Invalid  command");
                }
            }

            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
