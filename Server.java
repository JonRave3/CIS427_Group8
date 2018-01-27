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
            return true;
        } catch (IOException ioe){
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }

    private static void Run(){
        while(true){
            try {
                
            }
        }
    }

}