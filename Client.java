import java.io.*; 
import java.net.*;

public class Client
{
    public static final int SERVER_PORT = 7900;

    public static int main(String[] cmds){
        Socket server = null;//socket connection to the server
        PrintStream sender = null;//object used to send messages to the server
        BufferedReader  reader = null;//object used to read messages from server 
        BufferedReader stdInput = null;//object used to read messages from cli
        String userInput = null; //used to store input from the client-user
        String serverResponse = null;//used to store messages from the server

        //check for commands
        if(cmds.length < 1){
            System.out.println("Usage: client <Server IP Address>");
            System.exit(1);
        }
        // setup connection's and enabling stream reading/writing (instantiate objects)
        try {
            server = new Socket(cmds[0], SERVER_PORT);
            //object used to sen
            sender =  new PrintStream(server.getInputStream());
            reader = new BufferedReader(
                new InputStreamReader(server.getInputStream())
            );
            stdInput = new BufferedReader(
                new InputStreamReader(System.in)
            );
        } catch(UnknownHostException uhe){
                System.err.println("Host unknown!");
        } catch(IOException ioe){
            System.err.println("Unable to establish IO for the connection!");
        } catch(Exception e) {
            System.err.println("An unexpected error has occurred! please restart the application!");
            System.exit(1);
        }
        //null checks
        if(server != null  && sender != null && reader ! = null){
            try{
                while((userInput != stdInput.readLine()) != null) {
                    sender.println(userInput);
                    serverResponse = reader.readLine();
                    System.out.println("Server: "  + serverResponse);
                } 
            } catch (IOException ioe){
                System.err.println("An io exception has occurred: " + ioe);
            } catch (Exception e){
                System.err.println("An unexpected error has occurred: " + e);
            }

        }
    }
}