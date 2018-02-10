/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project1
*/

import java.net.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.*;
import java.util.*;


public class Server {

    private static final int SERVER_PORT = 6833;
    private static final int CLIENT_PORT = 7900;
    private static ArrayList<Record> list;
    private static final String dataFile = "data.txt";
    private static int maxRecordId;
    private static FileReader fReader;
    private static FileWriter fWriter;
    private static ServerSocket server = null;//
    private static String line;
    private static BufferedReader reader;
    private static PrintStream sender;
    private static Socket client=  null;


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
            fReader = new FileReader(dataFile);
            ReadDataFromFile();
            fWriter = new FileWriter(dataFile);
            server = new ServerSocket(SERVER_PORT);
            return true; 
            
        } catch (IOException ioe)
        {
            System.err.println("Unable to build server-socket: \n" + ioe);
            return false;
        }
    }//end of Init()

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
        BufferedReader bReader = new BufferedReader(fReader);
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

    private static void Run(){
        
        while(true){
            try {
                if(client == null) {
                    Write("Waiting for a connection from client(s)...");
                    client = server.accept();
                    reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream())
                    );
                    sender = new PrintStream(client.getOutputStream());
                }
                else if(!client.isOutputShutdown() && !client.isInputShutdown()){
                    
                    Write("Connected to client! Waiting for commands...");
                    while((line = reader.readLine()) != null){
                        //Split the string into tokens, tokens are validated by clients
                        String cmds[] = line.split("\\s+");
                        switch(cmds[0].toUpperCase()){
                            case "ADD":  
                                Add(cmds[1], cmds[2], cmds[3]);
                                break;
                            case "DELETE": 
                                Delete(cmds[1]);
                                break;
                            case "LIST":
                                List();
                                break;
                            case "QUIT":
                                Quit();
                                break;
                            case "SHUTDOWN": 
                                ShutDown(); 
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
        
    }

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

    private static class Record {
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
        public String forFile(){
            return String.format("%d %s %s %s\n", getRecordId(), _firstname, _lastname, _phone);
        }
    }//end of Record class

    private static void Write(String msg){
        System.out.println(msg);
    }//end of Write()

    private static void Respond(String msg) {
        sender.println(msg);
    }//end of Respond()
    private static void EndTx(){
        sender.println("ENDTX");
    }
}