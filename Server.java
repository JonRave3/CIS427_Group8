/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project2
*/

import java.net.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.*;
import java.util.*;
import java.lang.Thread;


public class Server {

    private static final int SERVER_PORT = 6833;
    private static ArrayList<Record> list;
    private static final String dataFile = "data.txt";
    private static final String usersFile = "Server.config";
    private static int maxRecordId;
    private static FileReader dataFileReader, userFileReader;
    private static FileWriter fWriter;
    private static ServerSocket server = null;
    private static Socket client = null;
    private static ArrayList<User> users;
    private static boolean running;
    private static Server instance = new Server();
    

    public static void main(String cmds[]){
        Write("heyo! We started!");
        if(Init()){
            Write("Initialized Server!");
            Run();
        } else {
            System.exit(1);
        }
    }//end of main()

    private static boolean Init(){
        running = true;
        Write("Initializing components!");
        //instantiate objects for use;
        try{    
            //read the file into memory
            list = new ArrayList<Record>();
            dataFileReader = new FileReader(dataFile);
            ReadDataFromFile();
            //read the user list into memory
            users = new ArrayList<User>();
            userFileReader = new FileReader(usersFile);
            ReadUsersFromFile();
            //start the server
            server = new ServerSocket(SERVER_PORT);
            return true; 
            
        } catch (IOException ioe)
        {
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }//end of Init()

    private Server() { }

    public static Server getInstance() {
        return instance;
    }//end of getInstance()
    private static void Run(){
        
        while(running){
            try {
                Write("Waiting for a connection from client(s)...");
                client = server.accept();
                Write("Connected to a Client: " + client.getInetAddress());
                ServerThread cThread = new ServerThread(client);
                cThread.start();
            } catch (Exception e) {
                
            } 
        }  
    }//end of Run()
    private static int FindMaxRecordId(){
        //if the list is not empty
        if(list != null && !list.isEmpty()){
            int max = 0;
            for(Record r : list){
                if(r.getRecordId() > max){
                    max = r.getRecordId();
                }
            }
            return max;
        }
        else {
            return 1000;
        }

    }//end of FindMaxRecodId()
   
    private static void ReadDataFromFile(){
        //get each line from the file
        BufferedReader bReader = new BufferedReader(dataFileReader);
        if (bReader != null) {
            try {
                String ln = null;
                while((ln = bReader.readLine()) != null){
                    //parse each line for tokenss
                    String tkns[] = ln.split("\\s+");
                    //store in list
                    Record nr = new Record(Integer.valueOf(tkns[0]));
                    nr._firstname = tkns[1];
                    nr._lastname = tkns[2];
                    nr._phone = tkns[3];
                    list.add(nr);
                }
            } catch (IOException ioe){
                Write("Error reading from data file!");
            } catch (NumberFormatException nfe){
                Write("unable to get record id from file");
            }
        } else {
            Write("Data file not found!");
        }
        //find the max record ID
        maxRecordId = FindMaxRecordId();
    }//end of ReadDataFromFile()

    private static void ReadUsersFromFile() {
        BufferedReader bReader = new BufferedReader(userFileReader);
        if (bReader != null) {
            try {
                String ln = null;
                while((ln = bReader.readLine()) != null){
                    //parse each line for tokenss
                    String tkns[] = ln.split("\\s+");
                    //store in list
                    User nu = new User(tkns[0], tkns[1]);
                    users.add(nu);
                }
            } catch (IOException ioe){
                Write("Error reading from user file!");
            }
        } else {
            Write("User file not found!");
        }
    }//end of ReadUsersFromFile()
    
    private static void WriteDataToFile(){
        //open the File connection
        try {
            fWriter = new FileWriter(dataFile);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            if(bWriter != null){
                //overwrite the file
                for(Record r : list){
                    fWriter.write(r.forFile());
                }
                //close the file connection
                fWriter.close();
            } else {
                Write("Data file not found!");
            }
        } catch (Exception e){
            Write("An error occurred writing to file: " + e.toString());
        }
    }//end of WriteDataToFile() 

    private static void Write(String msg){
        System.out.println(msg);
    }//end of Write()

    public static ArrayList<User> getUsers(){
        return users;
    }//end of getUsers() 
    public static ArrayList<Record> getContacts() {
        return list;
    }//end of getContacts()
    public static boolean AddRecord(String fname, String lname, String phone){
        maxRecordId++;
        Record r = new Record(maxRecordId);
        r._firstname = fname;
        r._lastname = lname;
        r._phone = phone;
        if(!list.contains(r)){
            list.add(r);
            return true;
        }
        else {
           return false;
        }
    }//end of AddRecord()
    public static boolean DeleteRecord(int rid){
        if(list.removeIf(x -> x.getRecordId() == rid)){
            FindMaxRecordId();
            return true;
        } else {
            return false;
        }
    }//end of DeleteRecord()

    public static void sd(){
        Write("Backing up contacts...");
        WriteDataToFile();
        Write("Shutting down server, Good Bye!");
        System.exit(1);
    }//end of sd()
}