import java.io.*;
import java.net.*;

public class Server {
    /* login --edwin */
    public static boolean login(String username, String password) {
        if (username.equals("erwakasiisi@gmail.com") && password.equals("1992")) {
            return true;
        } else {
            return false;
        }
    }
    /*deposit method --Vanessa */

    
    /* checkStatement --pius */


    /* loan request -- allan */


    /* loan request status -- taras*/
    

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server is running. Waiting for a client to connect...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] command = inputLine.split(" ");
                if (command.length > 1 && command.length < 5) {
                   switch (command[0]) {
                    case "login": 
                        if(login(command[1], command[2]) == true) {
                            out.println("Successfully logged in");
                        } else {
                            out.println("login failed");
                        }
                    case "deposit":
                        /* call the deposit method here */

                    case "CheckStatement":
                        /* call the CheckStatement method here */

                    case "requestLoan":
                        /* call the requestLoan method here */

                    case "LoanRequestStatus":
                        /* call the LoanRequestStatus method here */

                   }
                } else {
                    out.println("Invalid command");
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
