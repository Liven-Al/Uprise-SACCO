import java.io.*;
import java.net.*;

public class Client {
    public static String displayMenu() {
        String menu = "\n\t\tCommands\n-----------------------------------------------\n- deposit <amount> <date_deposited> <receipt_number>\n- CheckStatement <dateFrom> <dateTo>\n- requestLoan <amount> <paymentPeriod_in_months>\n- LoanRequestStatus <loan_application_number>\n- exit";
        return menu;
    }
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 1234);

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String userInput;
            while ((userInput = in.readLine()) != null) {
                out.println(userInput);
                String serverResponse = serverIn.readLine();
                if (serverResponse.equals("login failed")) {
                    System.out.println("Server response: " + serverResponse + ". Try again later!");
                } else {
                    System.out.println("Server response: " + serverResponse);
                    System.out.println(Client.displayMenu());
                }
                
            }

            in.close();
            serverIn.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
