import java.io.*;
import java.net.Socket;
import java.net.SocketException;

//Class implements the Runnable interface in order to include Threads
public class ClientThread implements Runnable {

    //Private object variables for the client thread
    private Socket socket;
    private ChatServer chatServer;
    private PrintWriter printWriter;
    private boolean connected;

    private String username;

    /**
     * Constructor method to set the passed socket and chatServer objects
     * @param socket: Socket object passed as one of the parameters
     * @param server: ChatServer object passed as the other parameter
     */
    public ClientThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.chatServer = server;

        //Connection flag set to true
        this.connected = true;
    }

    /**
     * Subroutine method to display a message passed as the parameter
     * @param message: String message to be displayed
     */
    public void displayMessage(String message) {
        printWriter.println(message);
    }

    /**
     * Boolean method which validates whether or not the client user is leaving the chat
     * by checking the input of the user (should enter __QUIT to leave the chat)
     * @param clientInput: String value of client input
     * @return true/false if they are leaving or not
     */
    public boolean isLeavingChat(String clientInput) {
        if (clientInput.equals("__QUIT")) {
            return true;
        } return false;
    }

    /**
     * Boolean method to validate if a client message sent is directed exclusively to the bot or not
     * (Clients should address the bot with 'HEY_BOT!' if they wish to converse with the bot)
     * @param input: String value of input
     * @return true/false if the message sent is indeed directed to the bot or not
     */
    public boolean messageDirectedToBot(String input) {
        if (input.startsWith("HEY_BOT!")) {
            return true;
        } return false;
    }

    /**
     * Boolean method to validate if the message send is from the bot or not
     * @param message: String value of the message
     * @return true/false if from bot or not
     */
    public boolean messageFromBot(String message) {
        if (message.contains("@"+this.username)) {
            return true;
        } return false;
    }

    /**
     * Method to force leave the user, by setting the connection status to false
     * then closing the socket
     */
    public void forceLeaveUser() {
        //this.socket.close();
        this.connected = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.exit(0);
    }

    /**
     * Overriding method from the Runnable interface to run the Thread
     */
    @Override
    public void run() {
        try {
            //Obtain the input stream
            InputStream input = this.socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

            //Obtain the output stream
            OutputStream output = this.socket.getOutputStream();
            printWriter = new PrintWriter(output, true);

            //Obtain the username
            this.username = bufferedReader.readLine();
            chatServer.addNewMemberToChat(this.username);

            //Display to all other members (excluding the user itself), that user of entered username has joined the chat
            String joiningMessage = "##-- "+this.username+" has joined the chat --##";
            this.chatServer.broadcastToAllOtherMembers(joiningMessage, this);

            String clientMessage;

            //Indefinite while loop iterates while the connection is active
            while (this.connected == true) {

                //Reads from the the bufferedReader object to obtain the clientMessage
                clientMessage = bufferedReader.readLine();
                if (clientMessage == null) {
                    break;
                }
                //Display the message
                displayMessage(clientMessage);

                //Check if the user is leaving the chat or not
                if (isLeavingChat(clientMessage)) {

                    //Broadcast leaving message to all other users and remove user from chat
                    String leavingMessage = "##-- "+this.username+" has left the chat --##";
                    this.chatServer.broadcastToAllOtherMembers(leavingMessage, this);
                    this.chatServer.removeMemberFromChat(this.username);
                    break;
                }
                //Check if the message is directed to the bot
                else if (messageDirectedToBot(clientMessage)) {
                    //Make server broadcast the message to the bot, so it appears in the bot's input stream
                    //Then the bot will reply to that message
                    this.chatServer.displayMessageToServer("[" + this.username + "]: " + clientMessage);
                    this.chatServer.broadcastToAllOtherMembers("[" + this.username + "]: " + clientMessage, this);
                //Otherwise it simply displays the message as a normal message to the server and all other members of the chat
                } else {
                    String serverMessage = "["+this.username+"]: "+clientMessage;
                    this.chatServer.broadcastToAllOtherMembers(serverMessage, this);
                    this.chatServer.displayMessageToServer(serverMessage);
                }
            }
            //Close the chat
            this.socket.close();
        } catch (SocketException e) {
            //Catch any SocketExceptions raised when a user leaves the chat using the '__quit' command
            try {
                this.socket.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Error while processing message sent...");
        }
    }
}
