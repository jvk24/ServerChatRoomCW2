import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatBot {

    //Private object variables for the chatBot
    private String userName;
    private Socket socket;
    private boolean connected;

    /**
     * Constructor method to create a bot object; similar to how a client works
     * However the inner workings differ
     * @param address: IP address string
     * @param port: port integer
     */
    public ChatBot(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.connected = true;

            //Set username to arbitrary name, since the bot is automated
            this.userName = "Chat_Bot";
            System.out.println("Connected to the chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor boolean method to obtain the connection status of the bot
     * @return true/false connection flag
     */
    protected boolean isConnected() {
        return this.connected;
    }

    /**
     * Accessor String method to obtain the username
     * @return String username
     */
    public String getUsername() {
        return this.userName;
    }

    /**
     * method to begin the process, by defining the threads
     * (Since the bot is completely automatic, it requires only a single thread for interpretation and writing
     * compared to actual clients which read input from what is entered from the keyboard)
     */
    public void begin() {
        //Instantiate the interpreting and writing threads
        //while passing the current object (this), and assigned socket as parameters
        BotInterpretThread interpreting = new BotInterpretThread(this, this.socket, this.userName);

        Thread botInterpretThread = new Thread(interpreting);

        //Bot thread begins
        botInterpretThread.start();

    }

    /**
     * Main method for the ChatBot class; command line parameters are available
     * @param args: command line arguments for IP address and port number specified using 'cca' and 'ccp' respectively
     */
    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 14001;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-ccp")) { port = Integer.parseInt(args[i+1]); }
            if (args[i].equals("-cca")) { address = args[i+1]; }
        }

        ChatBot bot = new ChatBot(address, port);
        bot.begin();
    }
}
