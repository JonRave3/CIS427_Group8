/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project1
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    private static final int SERVER_PORT = 6833;
    private static final ArrayList<Record> list;
    private static final String dataFile = "data.txt";
    private static int maxRecordId;
    private static FileReader fReader;
    private static ServerSocket server = null;//
    private static String line;
    private static BufferedReader reader;
    private static PrintStream sender;
    private static Socket client=  null;


    public static int main(String cmds[]){

        if(Init()){
           Run();
        } else {
            System.exit(1);
        }
    } 
    private static boolean Init(){
        try{
            //instantiate objects for use;
            server = new ServerSocket(SERVER_PORT);
            client = server.accept();
            reader = new BufferedReader(
                new InputStreamReader(server.getInputStream())
            );
            sender = new PrintStream(server.getOutputStream());
            //read the file into memory
            fReader = new FileReader(dataFile);
            
            ReadDataFromFile();
            
            return true;
        } catch (IOException ioe){
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }

    private static int FindMaxRecordId(){
        //if the list is not empty
        if(list != null && !list.isEmpty()){
            int max = 0;
            foreach(Record r : list){
                if(r._recordId > max){
                    max = r._recordId;
                }
            }
            return max;
        }
        else {
            return 1000;
        }

    }
    private static void ReadDataFromFile(){
        //get each line from the file
        //parse each line for tokens
        //store in list
        //find the max record ID
        maxRecordId = FindMaxRecordId();
    }
    private static void WriteDateToFile(){

    }

    private static void Run(){
        
        try {
            while((line = reader.readLine()) != null){
                //parse string
                String cmd = line[0];
                switch(cmd){
                    case "add" :  break;
                    case "delete" : break;
                    case "list" : break;
                    case "shutdown" : ShutDown(); 
                    default: break;
                }
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
        WriteDateToFile();
        //close connections
        sender.close();
        reader.close();
        server.close();           
    }
    private static void Add(String fname, String lname, String phone){
        maxRecordId++;
        Record r = new Record(maxRecordId);
        r._firstname = fname;
        r._lastname = lname;
        r._phone = phone;
    }
    private static void Delete(int id) {

    }
    private static void List() {
        
    }
    private class Record {
        public String _firstname, _lastname, _phone;
        private final int _recordId;

        public Record(int id) {
            _recordId = id;
        }
        public int getRecordId(){
            return this._recordId;
        }
        public String ToString(){
            return String.format("%d\t%s %s\t%s", getRecordId(), _firstname, _lastname, _phone);
        }
    }
}