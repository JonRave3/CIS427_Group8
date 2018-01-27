/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project1
*/

import java.net.*;
import java.io.*;

public class Server {
    public static final int SERVER_PORT = 6833;

    public static int main(String cmds[]){

        ServerSocket server = null;//
        String line;
        BufferedReader reader;
        PrintStream sender;
        Socket client=  null;

        if(Init()){
           Run();
        } else {
            System.exit(1);
        }
    } 
    private static boolean Init(){
        try{
            server = new ServerSocket(SERVER_PORT);
            client = server.accept();
            reader = new BufferedReader(
                new InputStreamReader(server.getInputStream())
            );
            sender = new PrintStream(server.getOutputStream());
            return true;
        } catch (IOException ioe){
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }

    private static void Run(){
        
        try {
            while((line = reader.readLine()) != null){
                System.out.println(line);
                sender.println(line);
            }
        } catch(IOException ioe) {
            System.err.println("IO Exception encountered reading/sending input from the client.");
        } catch (Exception e) {
            System.err.println("An unexpected error has occurred.");
        } finally {
            ShutDown();
        }
    }

    private static void ShutDown() {
        //output list to file

        //close connections
        sender.close();
        reader.close();
        server.close();           
    }

}