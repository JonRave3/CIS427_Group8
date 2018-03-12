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
    private static final String usersFile = "users.txt";
    private static int maxRecordId;
    private static FileReader dataFileReader, userFileReader;
    private static FileWriter fWriter;
    private static ServerSocket server = null
    private static Socket client = null;
    private static ArrayList<User> users;
    

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
            fWriter = new FileWriter(dataFile);
            //start the server
            server = new ServerSocket(SERVER_PORT);
            return true; 
            
        } catch (IOException ioe)
        {
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }//end of Init()

    private static void Run(){
        
        while(true){
            try {
                if(client == null) {
                    Write("Waiting for a connection from client(s)...");
                    client = server.accept();
                    ChildThread cThread = new ChildThread(client);
                    cThread.start();
                }
                else if(!client.isOutputShutdown() && !client.isInputShutdown()){
                    
                    Write("Connected to client! Waiting for commands...");
                    while((line = reader.readLine()) != null){
                        //Split the string into tokens, tokens are validated by clients
                        Write("Recieved msg from client: " + line);
                        String cmds[] = line.split("\\s+");
                        switch(cmds[0].toUpperCase()){
                            case "ADD":  
                                if(authorizedToEdit(client)){
                                    Add(cmds[1], cmds[2], cmds[3]);
                                } else  {
                                    Respond("402 User not allowed to execute this command");
                                    EndTx();
                                }
                                break;
                            case "DELETE": 
                                if(authorizedToEdit(client)){
                                    Delete(cmds[1]);
                                } else {
                                    Respond("402 User not allowed to execute this command");
                                    EndTx();
                                }
                                break;
                            case "LOGIN":
                                Login(cmds[1], cmds[2], client);
                                break;
                            case "LOGOUT": 
                                Logout(client);
                                break;
                            case "WHO": 
                                Who();
                                break;
                            case "LOOK": 
                                Look(cmds[1], cmds[2]);
                                break;
                            case "LIST":
                                List();
                                break;
                            case "QUIT":
                                Quit();
                                break;
                            case "SHUTDOWN":
                                if(authorizedToShutDown(client)){
                                    ShutDown(); 
                                } else {
                                    Respond("402 User not allowed to execute this command");
                                    EndTx();
                                }
                                break;
                            default: 
                                Respond("300 INVALID COMMAND");
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                if(client != null && (!client.isConnected() || !client.isClosed())){
                    client = null;
                } else if (server.isClosed()) {
                    return;
                }
            } 
        }  
    }//end of Run()
    private static int FindMaxRecordId(){
        //if the list is not empty
        if(list != null && !list.isEmpty()){
            int max = 0;
            for(Record r : list){
                if(r._recordId > max){
                    max = r._recordId;
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
        BufferedWriter bWriter = new BufferedWriter(fWriter);
        if(bWriter != null){
            try{
                //overwrite the file
                for(Record r : list){
                    fWriter.write(r.forFile());
                }
                //close the file connection
                fWriter.close();
            } catch(Exception e){
                Write("An error occurred writing to file: " + e.toString());
            }
        } else {
            Write("Data file not found!");
        }
        
    }//end of WriteDataToFile() 

    

    private static void Add(String fname, String lname, String phone){
        Write("Adding new record.");
        maxRecordId++;
        Record r = new Record(maxRecordId);
        r._firstname = fname;
        r._lastname = lname;
        r._phone = phone;
        if(!list.contains(r)){
            list.add(r);
            Write("Added new record");
            Respond("200 OK");
            EndTx();        
        }
        else {
            Write("Unable to add new record");
            Respond("400 BAD REQUEST");
            EndTx();
        }
        //sends a response to the Client
    }//end of Add()

    private static void Delete(String id) {
        try{
            int rid = Integer.parseInt(id);
            if(list.removeIf(x -> x._recordId == rid)){
                Respond("200 OK");
                EndTx();
                FindMaxRecordId();
            } else {
                Respond("400 BAD REQUEST");
                EndTx();
            }
        } catch(Exception e){
            Respond("500 INTERNAL SERVER ERROR");
            EndTx();
        }
        //find the record in the list
        //remove the record from the list
        
    }//end of Delete()

    private static void Login(String username, String password, Socket conn_client) {
        Write("Attempting to locate users credentials...");
        int id = findUsersByUnPw(username, password);
        if(id > -1) {
            
            User u = users.get(id);
            if(u != null){
                //check if user is already logged in somehwere
                if(u._client != null && !u._client.equals(conn_client)) {
                    Write("User: " + username + " is logged in elsewhere");
                    Respond("400 User is already logged in");
                    EndTx();
                } else if (u._client != null && u._client.equals(conn_client)) {
                    Write("User: " + username + " is logged in to the current client");
                    Respond("400 User is already logged in");
                    EndTx();
                } else {
                    Write("Registered users session");
                    u._client = conn_client;
                    Respond("200 OK");
                    EndTx();
                }        
            } else {
                Write("Error getting user from table");
            }
            
        } else {
            Respond("410 Wrong UserID or Password");
            EndTx();
        }
        
    }//end of Login()

    private static void Logout(Socket conn_client) {
        int id = findUserByConnection(conn_client);
        users.get(id)._client = null;
        Respond("200 OK");
        EndTx();
    }//end of Logout()

    private static void Who(){
        for(User u : users){
            if(u._client != null) {
                Respond(u.ToString());
            }
        }
        EndTx();
    }//end of Who()

    private static void Look(String searchBy, String param){

        ArrayList<Record> results = new ArrayList<Record>();

        switch(searchBy){
            case "1": //Search by firstname
                for(Record r: list) {
                    if(r._firstname.toUpperCase().contains(param.toUpperCase())){
                        results.add(r);
                    }
                }
                break;
            case "2"://Search by lastname
                for(Record r: list) {
                    if(r._lastname.toUpperCase().contains(param.toUpperCase())){
                        results.add(r);
                    }
                }
                break;
            case "3"://Search by phone number
                for(Record r: list) {
                    if(r._phone.toUpperCase().contains(param.toUpperCase())){
                        results.add(r);
                    }
                }
                break;
            default:
                results = null;
                break;
        }
        
        if(results != null && results.size() > 0) {
            Respond("200 OK");
            String recordsCount = "Found " + results.size() + " matches";  
            Respond(recordsCount);
            
            for(Record r : results) {
                Respond(r.ToString());
            }
            EndTx();
        } else if (results == null){
            Respond("404 Your search did not match any records");
            EndTx();
        }
    }//end of Look()

    private static void List() {
        //send each record back the client as a response
        Respond("200 OK");
        Respond("The list of records in the book:");
        for(Record r : list){
            Respond(r.ToString());
        }
        EndTx();
    }//end of List()

    private static void Quit(){
        //close connection with client
        try{
            reader.close();
            sender.close();
            client.close();
        } catch (Exception e){
            reader = null;
            sender = null;
        }
        client = null;
        
    }//end of Quit()

    private static void ShutDown() {
        Respond("200 OK");
        EndTx();
        //output list to file
        WriteDataToFile();
        //close all connections
        try{
            Quit();
            server.close();               
        } catch (Exception e) {
            System.err.println("Ayyy you fucked up!");
        }
    }//end of ShutDown()

    private static int findUsersByUnPw(String un, String pw){
        for(User u : users){
            if(u._username.compareTo(un) == 0 && u._password.compareTo(pw) == 0) {
                return users.indexOf(u);
            }
        }
        return -1;
    }//end of findUsersByUnPw

    private static int findUserByConnection(Socket soc){
        for(User u : users) {
            if(u._client != null && u._client.equals(soc)){
                return users.indexOf(u);
            }
        }
        return -1;
    }//end of finduserByConnection();

    private static boolean authorizedToEdit(Socket soc){
        int id = findUserByConnection(soc);
        if(id > -1) {
            return true;
        } 
        return false;
    }//end of authorizedToEdit()

    private static boolean authorizedToShutDown(Socket soc) {
        Write("Client " + soc.getInetAddress() + " is attempting to login!");
        int id = findUserByConnection(soc);
        if(id > -1) {
            User u = users.get(id);
            if(u._username.compareTo("root") == 0){
                return true;
            }
        }
        return false;
    }//end of authorizedToShutDown()

    private static void Write(String msg){
        System.out.println(msg);
    }//end of Write()

    private static void Respond(String msg) {
        sender.println(msg);
    }//end of Respond()

    private static void EndTx(){
        sender.println("ENDTX");
    }//end of EndTx()
}