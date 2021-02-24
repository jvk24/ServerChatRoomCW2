import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class BotInterpretThread implements Runnable {

    private ChatBot chatBot;
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private LanguageProcessing languageProcessing;
    private String botName;
    private String filePath;

    /**
     * Constructor method to define the connection and streams available
     * for reading and writing
     * @param bot: ChatBot object passed
     * @param socket: socket object
     * @param botName: bot name String value
     */
    public BotInterpretThread(ChatBot bot, Socket socket, String botName) {
        this.chatBot = bot;
        this.socket = socket;
        this.botName = botName;
        this.filePath = "src/replies.txt";

        try {
            //Attempt to open the streams for input and output
            InputStream inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);

            languageProcessing = new LanguageProcessing(this.filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Private, static method to parse a message string to obtain the actual message sent
     * @param message: String value of raw message from server
     * @return message which was sent originally by the client sender
     */
    private static String getMessageFromString(String message) {
        //Initial message string will be in the form "[name]: message"
        try {
            int stopIndexName = message.indexOf("]");
            int startIndexMessage = stopIndexName+3;
            int stopIndexMessage = message.length()-1;
            message = message.substring(startIndexMessage, stopIndexMessage);
            return message;
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Private, static method to parse a message string to obtain the username of the client
     * @param message: String value of raw message from the server
     * @return client username String value
     */
    private static String getUsernameFromString(String message) {
        try {
            int stopIndexName = message.indexOf("]");
            String username = message.substring(1,stopIndexName);
            return username;
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Overriding method from the Runnable interface.
     * Runs when the thread is running
     */
    @Override
    public void run() {
        try {
            //Send the bot name to the server to be assigned as a client (on the pipelined channel to the server
            //via the printWriter)
            printWriter.println(this.botName);

            //Indefinite while loop, runs while the bot has a connection to the server
            while (this.chatBot.isConnected()) {
                //String response is read from the buffered reader object
                String response = bufferedReader.readLine();

                if (response == null) {
                    break;

                //If the response from the client contains the bot addressing string 'HEY_BOT!', a reply is generated and sent
                } else if (response.contains("HEY_BOT!")) {

                    //Obtain the username and client message from the raw combined string received
                    String parsedMessage = getMessageFromString(response);
                    String username = getUsernameFromString(response);

                    //Automated reply is generated using the generateReply() method from the instantiated languageProcessing object
                    String automatedReply = languageProcessing.generateReply();

                    //The bot makes sure to address the client who addressed the bot with and '@' and their name (i.e. @John)
                    printWriter.println("@"+username+" "+automatedReply);
                }
            }
            //Close the socket
            this.socket.close();
        } catch (SocketException se) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
