import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

//Interpret method which implements the Runnable interface
public class InterpretThread implements Runnable {

    private ChatClient chatClient;
    private Socket socket;
    private BufferedReader bufferedReader;

    /**
     * Constructor for the class, setting up the input stream and buffered read objects
     * @param chatClient: ChatClient object
     * @param socket: Socket object passed as the parameter
     */
    public InterpretThread(ChatClient chatClient, Socket socket) {
        this.socket = socket;
        this.chatClient = chatClient;

        try {
            InputStream inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Overriding run method which is called when the Thread object is running
     */
    @Override
    public void run() {
        try {
            //Indefinite loop running while the client is connected to the server
            while (this.chatClient.isConnected()) {
                //Obtain the response by reading the buffered reader object.readLine()
                String response = bufferedReader.readLine();
                String username = this.chatClient.getUsername();

                if (response == null) {
                    break;
                }
                //Display the response
                System.out.println('\n' + response);
            }
            //Close the socket
            this.socket.close();
        } catch (SocketException se) {
            System.out.println("##-- You have left the chat --##");
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }
}
