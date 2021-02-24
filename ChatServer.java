import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

//Define the main class for the chat server, which implements from the Broadcasting interface
public class ChatServer implements Broadcasting {

    //Define the private instances of the variables used within the class
    private ServerSocket serverSocket;
    private ArrayList<ClientThread> clientThreads;
    private ArrayList<String> clientUsernames;
    private boolean connected;

    /**
     * public constructor method, attaches the instances to the instance variable
     * @param port: Integer value of the passed port number
     */
    public ChatServer(int port) {
        try {
            //Instance of socket object, with the parameter port passed
            this.serverSocket = new ServerSocket(port);

            //arrayLists instantiated to hold the client's threads, and usernames respectively
            this.clientThreads = new ArrayList<ClientThread>();
            this.clientUsernames = new ArrayList<String>();

            //Boolean connection status flag
            this.connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Overriding synchronized method to broadcast the string parameter
     * to all members in the chat online at that time.
     * @param message: String message to broadcast
     */
    @Override
    public synchronized void broadcastToAllMembers(String message) {

        //Loop through each client thread in the clientThread arrayList structure,
        //then call method to display message
        for (ClientThread clientThread: this.clientThreads) {
            clientThread.displayMessage(message);
        }
    }

    /**
     * Overriding synchronized method to broadcast the string parameter
     * to all OTHER members in the chat (everyone except from a certain user).
     * @param message: String message to broadcast
     * @param selfClientThread: clientThread object which marks the client to avoid
     */
    @Override
    public synchronized void broadcastToAllOtherMembers(String message, ClientThread selfClientThread) {

        //Loop defined similar to the iteration in the above function, but excludes the clientThread object passed
        for (ClientThread clientThread: this.clientThreads) {
            //if condition to only send the message to others, so that
            // the client's message does not ping back to the client itself.
            if (selfClientThread != clientThread) {
                clientThread.displayMessage(message);
            }
        }
    }

    /**
     * Overriding method to broadcast the string parameter
     * to a single specified member of chat currently online
     * @param message: String message to broadcast
     * @param selfClientThread: clientThread object which marks the client to avoid
     */
    @Override
    public void broadcastToParticularMember(String message, ClientThread selfClientThread) {
        for (ClientThread clientThread: this.clientThreads) {
            //if condition to only send the message to others, so that
            // the client's message does not ping back to the client itself.
            if (selfClientThread == clientThread) {
                clientThread.displayMessage(message);
                break;
            }
        }
    }

    /**
     * Overriding method to broadcast the string parameter
     * to a single specified member of chat currently online
     * @param message: String message passed to broadcast to the bot
     */
    @Override
    public void broadcastToBot(String message) {
        int index = this.clientUsernames.indexOf("Chat_Bot");
        ClientThread botThread = this.clientThreads.get(index);
        botThread.displayMessage(message);
    }

    /**
     * Synchronized method to display a given message to the server
     * @param message: String message
     */
    protected synchronized void displayMessageToServer(String message) {
        System.out.println(message);
    }

    /**
     * Synchronized method to add a new member to the chat
     * Indicate that the member (with their respective username) has joined
     * @param userName: String value of username
     */
    protected synchronized void addNewMemberToChat(String userName) {
        this.clientUsernames.add(userName);
        System.out.println("##–– "+userName+" has joined the chat! --##\n");
    }

    /**
     * Synchronized method to remove a member from the chat
     * Indicate that the member (with their respective username) has left
     * @param userName: String value of username
     */
    protected synchronized void removeMemberFromChat(String userName) {
        this.clientUsernames.remove(userName);
        System.out.println("##–– "+userName+" has left the chat! --##\n");
    }

    /**
     * Accessor method to obtain the clientThread arrayList object
     * @return clientThreads arrayList
     */
    protected ArrayList<ClientThread> getClientThreadArray() {
        return this.clientThreads;
    }

    /**
     * Accessor method to obtain the clientUsernames ArrayList object
     * @return clientUsernames object
     */
    protected ArrayList<String> getClientUsernames() {
        return this.clientUsernames;
    }

    /**
     * Subroutine to simply display the title message at the very beginning of the chat application
     */
    private void displayOpeningMessage() {
        System.out.println("##=========================================##");
        System.out.println("##   WELCOME TO THE TCP LOCAL CHAT-ROOM!   ##");
        System.out.println("##=========================================##\n");
    }

    /**
     * Subroutine method to disconnect all users connected (including the bot(s))
     */
    protected void disconnectAllClients() {
        //Define an iterator object, iterate through clientThreads, and remove them
        Iterator<ClientThread> iterator = this.clientThreads.iterator();
        while (iterator.hasNext()) {
            ClientThread clientThread = iterator.next();
            iterator.remove();
        }
    }

    /**
     * Subroutine to (safe) shutdown the server, which consists of several smaller methods
     * which constitutes to to the disconnection process.
     */
    protected void safeShutdownServer() {
        try {
            String shutDownMessage = "##-- SERVER SHUT DOWN! --##";
            this.broadcastToAllMembers(shutDownMessage);
            this.displayMessageToServer(shutDownMessage);
            this.disconnectAllClients();
            this.connected = false;
            this.serverSocket.close();
        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
        }
    }

    /**
     * Method to begin all the processes:
     * - Starting up the server,
     * - Indefinite iteration to keep accepting clients to the chat
     */
    public void begin() {
        displayOpeningMessage();
        System.out.println("Waiting for participants to join...");
        try {
            //Instantiate a server thread object and begin the thread
            ServerThread serverThread = new ServerThread(this);
            Thread threadForServer = new Thread(serverThread);
            threadForServer.start();

            //Indefinite iteration while the connection is active to keep accepting new users
            //and assigning each one of those users a thread via the instantiation of the clientThread object
            while (connected == true) {
                Socket clientSocket = serverSocket.accept();

                ClientThread clientThread = new ClientThread(clientSocket, this);
                this.clientThreads.add(clientThread);
                Thread threadN = new Thread(clientThread);

                threadN.start();
            }
        } catch (SocketException se) {
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //Close the server socket when everything is over;
                //This is in a 'finally' block, meaning that it is always executed
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main method to begin the entire process when this class is run or called from the command line
     * @param args: None
     */
    public static void main(String[] args) {
        final int serverPort = 14001;
        ChatServer echoServer = new ChatServer(serverPort);
        echoServer.begin();
    }
}
