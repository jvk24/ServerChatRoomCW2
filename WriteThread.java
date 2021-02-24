import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//Writing class implementing the Runnable interface for the client
public class WriteThread implements Runnable {

    private ChatClient chatClient;
    private Socket socket;
    private PrintWriter printWriter;
    private boolean userNameEntered;

    /**
     * constructor method setting the instance variables, and setting the output stream
     * @param chatClient: ChatClientObject passed as parameter
     * @param socket: Socket object passed as a parameter
     */
    public WriteThread(ChatClient chatClient, Socket socket) {
        this.chatClient = chatClient;
        this.socket = socket;

        //Status to check if the client has properly entered their username
        this.userNameEntered = false;

        try {
            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * boolean method to validate the username
     * @param userName: String username value
     * @return true/false based on the validity of the username
     */
    public boolean userNameIsValid(String userName) {

        //Make sure that the length of the username is greater than 0 and not null
        if (userName.length() > 0 && userName != null) {
            return true;
        } return false;
    }

    /**
     * String method to promp the client user to enter their username
     * when they first join the chat
     * @return String username value
     */
    public String getUsername() {
        boolean valid = false;

        //Scanner object instantiated to read keyboard input
        Scanner scanner = new Scanner(System.in);
        String username = null;

        //Loop to retry until the username is valid
        while (!valid) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            //Validate the username
            valid = userNameIsValid(username);
        }
        this.userNameEntered = true;
        return username;
    }

    /**
     * boolean method to validate if the user is leaving the chat or not
     * @param clientInput: String input from the client
     * @return true/false based on the client input
     */
    public boolean isLeavingChat(String clientInput) {
        if (clientInput.equals("__QUIT")) {
            return true;
        } return false;
    }

    /**
     * Overriding run method from the implemented Runnable interface
     * Runs when the Thread is run
     */
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String username = null;
            while (!this.userNameEntered) {
                username = getUsername();
            }
            //Set the username of the client, based on the now-validated input
            this.chatClient.setUsername(username);

            //send on the output stream
            printWriter.println(username);

            //Display personal user messages to inform them of their successful addition
            //to the chat
            System.out.println("\n##-- You joined the chat --##");
            System.out.println("You can begin to chat! ");

            //Indefinite loop while the client is connected
            while (this.chatClient.isConnected()) {
                //Input is taken in
                String input = scanner.nextLine();
                if (input == null) {
                    break;
                }
                //Input is displayed via the printWriter
                printWriter.println(input);
                if (isLeavingChat(input)) {

                    //Broken from loop when leaving from the chat
                    break;
                }
            }
            //close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
