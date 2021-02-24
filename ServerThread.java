import java.io.*;
import java.net.ServerSocket;
import java.util.Scanner;

//ServerThread class which implements the Runnable interface
public class ServerThread implements Runnable {

    ServerSocket serverSocket;
    ChatServer chatServer;

    /**
     * Constructor method setting the chatServer object to the instance variable
     * @param chatServer: passed object
     */
    public ServerThread(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    /**
     * Exclusive boolean method to check if an 'Admin' user has requested to view the
     * online clients on the server by typing in 'ONLINE_CLIENTS'
     * @param input: String input from the admin user
     * @return true/false if the admin requested the information or not
     */
    private boolean checkInputForOnlineDisplay(String input) {
        if (input.toUpperCase().equals("ONLINE_CLIENTS")) {
            return true;
        } return false;
    }

    /**
     * method to display the online clients
     */
    protected void displayOnlineClients() {
        this.chatServer.displayMessageToServer("##-- CURRENTLY ONLINE: --##");

        //Iterate through the usernames and display them in a user-friendly, listed format
        int i = 1;
        for (String username : this.chatServer.getClientUsernames()) {
            this.chatServer.displayMessageToServer(i+".] "+username);
            i += 1;
        }
        this.chatServer.displayMessageToServer("##-----------------------##\n");
    }

    /**
     * boolean method to validate if the admin user has requested for the server to be shut down
     * (when the command 'EXIT' is inputted by the admin, the entire server will shut down, notifying
     * all connected users)
     * @param input: input from the admin user
     * @return true/false based on the input
     */
    protected boolean isShuttingDown(String input){
        if (input.toUpperCase().equals("EXIT")) {
            return true;
        } return false;
    }

    /**
     * Overriding run method which will run when the Thread method is running
     */
    @Override
    public void run() {

        //Indefinite loop to iterate until the admin calls for a shutdown
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            //Check for a shutdown
            if (isShuttingDown(input)) {
                this.chatServer.safeShutdownServer();
                break;
            }

            //Check if the admin wants to view the clients online
            if (checkInputForOnlineDisplay(input)) {
                displayOnlineClients();

            //Otherwise broadcast the message to all members connected
            } else {
                String serverMessage = "[SERVER]: "+input+"";
                this.chatServer.broadcastToAllMembers(serverMessage);
            }
        }
    }
}
