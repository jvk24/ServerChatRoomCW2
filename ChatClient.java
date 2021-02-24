import java.io.*;
import java.net.Socket;

public class ChatClient {

    //Define the private instances of the variables used within the class
    private Socket socket;
    private String userName;
    private boolean connected;

    /**
     * Constructor method to 'begin' the chatClient, assigned socket via port and address
     * @param address: String value of IP address
     * @param port: Integer value of port
     */
    public ChatClient(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.connected = true;
            System.out.println("Connected to the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Status condition function to check if the client is connected or not
     * @return boolean value of the above proposition
     */
    protected boolean isConnected() {
        return this.connected;
    }

    /**
     * Mutator method to set the value of the client's username
     * @param username: String username value
     */
    public void setUsername(String username) {
        this.userName = username;
    }

    /**
     * Accessor method to obtain the value of the client username
     * @return String username value
     */
    public String getUsername() {
        return userName;
    }

    /**
     * Method to begin the chat client by instantiating two threads: one for interpreting the input,
     * and the other for reading the input
     */
    public void begin() {
        //Instantiate the interpreting and writing threads
        //while passing the current object (this), and assigned socket as parameters
        InterpretThread interpreting = new InterpretThread(this, this.socket);
        WriteThread writing = new WriteThread(this, this.socket);
        Thread interpretThread = new Thread(interpreting);
        Thread writingThread = new Thread(writing);

        //Begin the respective threads
        interpretThread.start();
        writingThread.start();
    }

    /**
     * Main method to start all the processes when this class is called.
     * Default address and port are set, if no command line arguments to modify them are specified
     * @param args: Two command line arguments possible are: address and port
     */
    public static void main(String[] args) {

        //Default set values for the address and the port respectively
        //These values will be set if no parameters are passed into the args
        String address = "127.0.0.1";
        int port = 14001;

        //For loop to iterate through the args string array and obtain the values placed for 'cca' and 'cca'
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ccp")) { port = Integer.parseInt(args[i+1]); System.out.println("Overriding ccp with:"+String.valueOf(port) ); }
            if (args[i].equals("-cca")) { address = args[i+1]; System.out.println("Overriding cca with:"+address); }
        }

        ChatClient echoClient = new ChatClient(address, port);
        echoClient.begin();
    }
}
