import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    /* database variables */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/uprise-sacco";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";

    /* login --edwin */
    public static boolean login(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM member WHERE username='" + username + "' AND password='" + password + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean recoverPassword(String memberNumber, String phoneNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement statement = connection.createStatement()) {
            String query = "SELECT * FROM member WHERE member_number='" + memberNumber + "' AND phone_number='"
                    + phoneNumber + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    /* login ends here */
    

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
            boolean isLoggedIn = false;
            int member_id = 0;
            double account_balance = 0.0;
            double loan_balance = 0.0;

            while ((inputLine = in.readLine()) != null) {
                String[] command = inputLine.split(" ");
                if (command.length > 1 && command.length < 5) {
                    switch (command[0]) {
                        case "login":
                            if (!isLoggedIn && login(command[1], command[2])) {

                                out.println("Successfully logged in");
                                isLoggedIn = true;

                                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                                        Statement statement = connection.createStatement()) {
                                    String query = "SELECT * FROM member WHERE username='" + command[1]
                                            + "' AND password='" + command[2] + "'";
                                    ResultSet resultSet = statement.executeQuery(query);
                                    while (resultSet.next()) {
                                        member_id = resultSet.getInt("member_number");
                                        account_balance = resultSet.getDouble("account_balance");
                                        loan_balance = resultSet.getDouble("loan_balance");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            } else if (isLoggedIn) {
                                out.println("You are already logged in.");
                            } else {
                                out.println("login failed");
                            }
                            break;
                        case "forgotPassword":
                            if (!isLoggedIn) {
                                if (recoverPassword(command[1], command[2])) {
                                    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER,
                                            DB_PASSWORD);
                                            Statement statement = connection.createStatement()) {
                                        String query = "SELECT * FROM member WHERE member_number='" + command[1]
                                                + "' AND phone_number='" + command[2] + "'";
                                        ResultSet resultSet = statement.executeQuery(query);
                                        while (resultSet.next()) {
                                            String password = resultSet.getString("password");
                                            out.println("Your password is: " + password
                                                    + ". Use the login command to login now.");
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    out.println("No records found. Return after a day");
                                }
                            } else {
                                out.println("You are already logged in.");
                            }
                            break;

                        case "deposit":
                            if (isLoggedIn) {
                                
                                /* call the deposit method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "CheckStatement":
                            if (isLoggedIn) {
                                /* call the CheckStatement method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "requestLoan":
                            if (isLoggedIn) {
                                /* call the requestLoan method here */
                            } else {
                                out.println("You must log in first to perform this operation.");
                            }
                            break;
                        case "LoanRequestStatus":
                            if (isLoggedIn) {
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
