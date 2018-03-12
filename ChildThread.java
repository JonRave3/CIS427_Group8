import java.lang.Thread;
import java.util.Vector;
import Java.util.ArrayList;
import java.io.*;
import java.net.*;

public class ChildThread extends Thread {
        
    static  Vector<ChildThread> handlers = new Vector<ChildThread>(20);
    private Socket client;
    private BufferedReader reader;
    private PrintWriter sender;

    public ChildThread(Socket socket) throws IOException {
        this.client = socket;
        this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.sender = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
    }//end of Construtors()

     public void run() {
        String line;
        synchronized(handlers) {    
             // add the new client in Vector class    
             handlers.addElement(this);
        } 
        try {
            while ((line = reader.readLine()) != null) {
                //Split the string into tokens, tokens are validated by clients
                Write("Recieved msg from client: " + line);
                String cmds[] = line.split("\\s+");
                switch(cmds[0].toUpperCase()){
                    case "ADD":  
                        if(authorizedToEdit()){
                            Add(cmds[1], cmds[2], cmds[3]);
                        } else  {
                            Respond("402 User not allowed to execute this command");
                            EndTx();
                        }
                        break;
                    case "DELETE": 
                        if(authorizedToEdit()){
                            Delete(cmds[1]);
                        } else {
                            Respond("402 User not allowed to execute this command");
                            EndTx();
                        }
                        break;
                    case "LOGIN":
                        Login(cmds[1], cmds[2]);
                        break;
                    case "LOGOUT": 
                        Logout();
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
                        if(authorizedToShutDown()){
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
        } catch(IOException ioe) {    
            ioe.printStackTrace();
        } finally {    
            Quit();
        }
    }//end run()

    ///TODO get the contactList from Server
    private void Add(String fname, String lname, String phone){
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

    private void Delete(String id) {
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

    ///TODO get the list of authorized users
    private void Login(String username, String password) {
        Write("Attempting to locate users credentials...");
        int id = findUsersByUnPw(username, password);
        if(id > -1) {
            
            User u = users.get(id);
            if(u != null){
                //check if user is already logged in somehwere
                if(u._client != null && !u._client.equals(this.client)) {
                    Write("User: " + username + " is logged in elsewhere");
                    Respond("400 User is already logged in");
                    EndTx();
                } else if (u._client != null && u._client.equals(this.client)) {
                    Write("User: " + username + " is logged in to the current client");
                    Respond("400 User is already logged in");
                    EndTx();
                } else {
                    Write("Registered users session");
                    u._client = this.client;
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

    private void Logout() {
        int id = findUserByConnection();
        if(id > -1){
            users.get(id)._client = null;
        }
        Respond("200 OK");
        EndTx();
    }//end of Logout()

    private void Who(){
        for(User u : users){
            if(u._client != null) {
                Respond(u.ToString());
            }
        }
        EndTx();
    }//end of Who()

    private void Look(String searchBy, String param){

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

    private void List() {
        //send each record back the client as a response
        Respond("200 OK");
        Respond("The list of records in the book:");
        for(Record r : list){
            Respond(r.ToString());
        }
        EndTx();
    }//end of List()

    private void Quit(){
        //close connection with client
        try{
            this.reader.close();
            this.sender.close();
            this.client.close();

        } catch (Exception e){
            
        } finally {
            synchronized(handlers) {    
                handlers.removeElement(this);
            }
        }
        
    }//end of Quit()

    private void ShutDown() {
        Respond("200 OK");
        EndTx();
        //output list to file
        WriteDataToFile();
        //close all connections
        try{
            Quit();
            synchronized(handlers) {
                handlers.removeAllElements();   
            }             
        } catch (Exception e) {
            System.err.println("Ayyy you fucked up!");
        }
    }//end of ShutDown()

    private int findUsersByUnPw(String un, String pw){
        for(User u : users){
            if(u._username.compareTo(un) == 0 && u._password.compareTo(pw) == 0) {
                return users.indexOf(u);
            }
        }
        return -1;
    }//end of findUsersByUnPw

    private int findUserByConnection(){
        for(User u : users) {
            if(u._client != null && u._client.equals(this.client)){
                return users.indexOf(u);
            }
        }
        return -1;
    }//end of finduserByConnection();

    private boolean authorizedToEdit(){
        int id = findUserByConnection();
        if(id > -1) {
            return true;
        } 
        return false;
    }//end of authorizedToEdit()

    private boolean authorizedToShutDown() {
        Write("Client " + this.client.getInetAddress() + " is attempting to Shutdown server!");
        int id = findUserByConnection();
        if(id > -1) {
            User u = users.get(id);
            if(u._username.compareTo("root") == 0){
                return true;
            }
        }
        return false;
    }//end of authorizedToShutDown()

    private void Write(String msg){
        System.out.println(msg);
    }//end of Write()

    private void Respond(String msg) {
        sender.println(msg);
    }//end of Respond()

    private void EndTx(){
        sender.println("ENDTX");
    }//end of EndTx()

    private void Broadcast(String msg){
        // Broadcast it to everyone!  You will change this.
        // Most commands do not need to broadcast
        for(ChildThread c : handlers){
            synchronized(handlers) {
                if(c != this){
                    c.sender.println(msg);
                    c.sender.flush();
                }
            }
        }
        /* old implementation
        for(int i = 0; i < handlers.size(); i++) {    
            synchronized(handlers) {
                ChildThread handler = (ChildThread) handlers.elementAt(i);
                if (handler != this) {
                    handler.sender.println(msg);    
                    handler.sender.flush();
                }    
            }
        }
        */  
    }
}//end of ChildThread class