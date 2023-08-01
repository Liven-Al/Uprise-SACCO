import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Server {
    /* database variables */
    public static final String DB_URL = "jdbc:mysql://localhost:3306/uprise-sacco";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    private static Connection connection;
    private static Statement statement;
    private static int application_no;
    private static int applicationNo;
    private static int application_number_app;
    private static int member_id_app;
    private static int amount_app;
    private static int repayment_period_app;
    private static int requestedAmount = 0;
    private static int memberDeposit = 0;
    private static int total_deposits = 0; 
    private static  int loanamount = 0;
    private static int total_loanrequested = 0;
    private static int member_ID;
    private static int accountBalance;
    private static int count = 0;
    private static ResultSet resultSet1;
    private static ResultSet resultSet2;
    private static ResultSet resultSet3;
    private static ResultSet resultSet4;
    private static ResultSet resultSet5;
    private static ResultSet resultSet6;
    private static ResultSet resultSet7;

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
       public static void requestLoan(int memberID, int amount, int repayment_period) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();
             /* generate a random application_no value-- */
            Random random = new Random();
            application_no = 1000 + random.nextInt(9999);

             /* sql to handle loan request-- */
            String sql = "INSERT INTO loan_application(application_no,member_ID, amount, repayment_period) VALUES("
                    + application_no + "," + memberID + "," + amount + "," + repayment_period + ")";
            statement.executeUpdate(sql);
            System.out.println("Your loan application has been submitted successfully. Your application nummber is "
                    + application_no);
            
            resultSet1 = statement
                    .executeQuery("SELECT COUNT(application_no) FROM loan_application WHERE status='pending'");
            int no_of_available_requests = 0;
        
            if (resultSet1.next()) {
                no_of_available_requests = resultSet1.getInt("COUNT(application_no)");
            }
            if (no_of_available_requests == 10) {
                String changeStatus = "UPDATE loan_application SET status = 'processing' WHERE status = 'pending'";
                statement.executeUpdate(changeStatus);

                loandistributionandapproval();
            }
            // Close the resources
        resultSet1.close();
        statement.close();
        connection.close();      

        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
     
          public static void loandistributionandapproval(){ 
     try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();

            resultSet2 = statement.executeQuery(
                    "SELECT SUM(account_balance),SUM(loan_balance),SUM(account_balance) - SUM(loan_balance) AS difference FROM member");
            int available_funds = 0;


            if (resultSet2.next()) {
                available_funds = resultSet2.getInt("difference");
            }
            if (available_funds > 2000000) {
                resultSet3 = statement
                        .executeQuery("SELECT * FROM loan_application WHERE status = 'processing'");
                List<Integer> loanRequests = new ArrayList<>();
                List<Integer> memberIDs = new ArrayList<>();
                List<Integer> memberIndices = new ArrayList<>();  
                Map<Integer, Integer> memberDeposits = new HashMap<>();           
                List<Integer> repaymentPeriods = new ArrayList<>();
                List<Double> finalLoanAmounts = new ArrayList<>();
              //  List<Integer> memberDeposits = new ArrayList<>();
                List<Integer> applicationNos = new ArrayList<>();
               
               
                
                
                while (resultSet3.next()) {
                    loanamount = resultSet3.getInt("amount");
                    int memberId = resultSet3.getInt("member_ID");
                    int repaymentPeriod = resultSet3.getInt("repayment_period");
                    applicationNo = resultSet3.getInt("application_no");

                    memberIDs.add(memberId);
                    repaymentPeriods.add(repaymentPeriod);
                    applicationNos.add(applicationNo);
                    loanRequests.add(loanamount);
                    total_loanrequested += loanamount;
                    memberIndices.add(memberIDs.size() - 1);
                }   
                           
                 resultSet4 = statement.executeQuery("SELECT member_ID,account_balance FROM member WHERE member_ID IN (SELECT member_ID FROM loan_application WHERE status = 'processing')");
                                         
  
                        while (resultSet4.next()) {
                            

                            member_ID = resultSet4.getInt("member_ID");
                            accountBalance = resultSet4.getInt("account_balance");
                            
                           // memberIDs.add(member_ID);
                           // memberDeposits.add(accountBalance);
                            memberDeposits.put(member_ID, accountBalance);
                            total_deposits += accountBalance;
                            
                            }
                             // destribute loan to members based on their requested loan,their deposit
                         for (int i = 0; i < loanRequests.size(); i++) {
                             requestedAmount = loanRequests.get(i);
                             member_ID = memberIDs.get(i);
                             int memberIndex = memberIndices.get(i); 

                             if (memberIndex >= 0 && memberIndex < memberDeposits.size()) {
                                int memberDeposit = memberDeposits.get(member_ID);
                                
                                double membershare = (double) requestedAmount / total_loanrequested * available_funds;
                                double maxloanAllowed = (3.0 / 4) * memberDeposit;
                        
                                double finalLoanAmount = Math.min(requestedAmount, Math.min(membershare, maxloanAllowed));
                                finalLoanAmounts.add(finalLoanAmount);
                            } else {
                               System.out.println("Out of length");
                            }
                        
                        } 
                        for (int i = 0; i < loanRequests.size(); i++) {
                            int applicationNo = applicationNos.get(i); 
                            int memberId = memberIDs.get(i);
                            double finalLoanAmount = finalLoanAmounts.get(i);
                            int repaymentPeriod = repaymentPeriods.get(i);

                            String updateLoanQuery = "INSERT INTO LoanRequest_approval(application_no, member_ID, amount, repayment_period) VALUES("
                            + applicationNo + "," + memberId + "," + finalLoanAmount + "," + repaymentPeriod + ")";

                statement.executeUpdate(updateLoanQuery);
            }
                    
                    // Close the resources
            resultSet2.close();
            resultSet3.close();
            resultSet4.close();
                
            }
          

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Close the resources
            try {
                if (statement  != null) statement.close();
                if (connection != null) connection.close();
                if (resultSet2 != null) resultSet1.close();
                if (resultSet3 != null) resultSet1.close();
                if (resultSet4 != null) resultSet1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
     public static void view_generatedloanDistribution(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();

            String sql = "SELECT * FROM LoanRequest_approval";
            resultSet5 = statement.executeQuery(sql);

            while (resultSet5.next() && count < 10){
             application_number_app = resultSet5.getInt("application_no");
             member_id_app = resultSet5.getInt("member_ID");
             amount_app  = resultSet5.getInt("amount");
             repayment_period_app = resultSet5.getInt("repayment_period");

             System.out.println("Application number: " +application_number_app);
             System.out.println("Member ID: " +member_id_app);
             System.out.println("Amount: " +amount_app);
             System.out.println("Repayment period: " +repayment_period_app );
             System.out.println("----------------------------");

             count++;

            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if (resultSet5 != null) resultSet5.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

     }
            public static void approvedRecommendedLoanDistribution() {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    statement = connection.createStatement();

                    String selectQuery = "SELECT *  FROM LoanRequest_approval WHERE status = 'Approved'";
                    resultSet6 = statement.executeQuery(selectQuery);

                    String updateQuery = "UPDATE loan_application SET status='approved' WHERE status='processing'";
                    statement.executeUpdate(updateQuery);
                    int rowsUpdated = statement.executeUpdate(updateQuery);

                    System.out.println(rowsUpdated + "Loan records have been updated");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            public static void modifyRecomendedLoanDistribution(){
                   try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    statement = connection.createStatement();

                    
                    
                   } catch (Exception e) {
                    e.printStackTrace();
                   }

            }

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
