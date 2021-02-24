import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class LanguageProcessing {

    //Private object variables required for file management
    private File file;
    private int length;

    /**
     * Constructor method to setup the LanguageProcessing class when called,
     * by scanning through the file and calculating the length of the file, which is used later
     * @param filePath: String variable for the relative filepath of the strings
     */
    public LanguageProcessing(String filePath) {
        try {
            this.file = new File(filePath);
            Scanner myReader = new Scanner(this.file);
            int i = 1;

            //Iterate through the file and update i
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                i += 1;
            }
            this.length = i-1; //Set length as one less than i, since indexing starts at 0
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * String method to obtain a line from the file, given an index
     * @param index: randomly generated index value passed
     * @return data stored at that index value in the file
     * @throws FileNotFoundException
     */
    public String getItemAtIndex(int index) throws FileNotFoundException {
        Scanner myReader = new Scanner(this.file);
        int i = 0;

        //Iterate through the file, and perform linear search
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (i == index) {
                myReader.close();

                //Return the data once found
                return data;
            }
            i += 1;
        }
        myReader.close();
        return null;
    }

    /**
     * Synchronized String method to generate the reply to the client user who addressed the bot
     * @return String value of the response, based on a random index generated and passed into the
     * getItemAtIndex() method
     */
    public synchronized String generateReply() {
        Random rand = new Random();

        //Random number generated (also takes length of the file into account)
        int randNum = rand.nextInt(this.length);
        try {
            String reply = getItemAtIndex(randNum);
            return reply;
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception; "+e.getMessage());
            return null;
        }
    }
}
