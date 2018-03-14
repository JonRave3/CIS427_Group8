/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project2
*/

import java.lang.Thread;
import java.util.*;
import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
        
    static Vector<ServerThread> handlers = new Vector<ServerThread>(20);
    private Socket client;
    private BufferedReader reader;
    private PrintStream sender;
    private Server parentThread;

    public ServerThread(Socket socket) throws IOException {
        this.client = socket;
        this.parentThread = Server.getInstance();
        this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.sender = new PrintStream(client.getOutputStream());
    }//end of Construtors()

     public void run() {
        String line;
        synchronized(handlers) {    
             // add the new client in Vector class    
             handlers.addElement(this);
             Write("Number of connections: " + handlers.size());
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
                        }
                        break;
                    case "DELETE": 
                        if(authorizedToEdit()){
                            Delete(cmds[1]);
                        } else {
                            Respond("402 User not allowed to execute this command");
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
                        }
                        break;
                    default: 
                        Respond("300 INVALID COMMAND");
                        break;
                }  
            }
        } catch(IOException ioe) {    
            Write("Connection with client has terminated!");
        } finally {    
            Quit();
        }
    }//end run()

    ///TODO get the contactList from Server
    private void Add(String fname, String lname, String phone){
        Write("Adding new record.");
        if(this.parentThread.AddRecord(fname, lname, phone)){
            Write("Added new record");
            Respond("200 OK");
        } else {
            Write("Unable to add new record");
            Respond("400 BAD REQUEST");
        }
        //sends a response to the Client
    }//end of Add()

    private void Delete(String id) {
        try{
            int rid = Integer.parseInt(id);
            if(this.parentThread.DeleteRecord(rid)){
                Respond("200 OK");
            } else {
                Respond("400 BAD REQUEST");
            }
        } catch(Exception e){
            Respond("500 INTERNAL SERVER ERROR");
        } 
    }//end of Delete()

    ///TODO get the list of authorized users
    private void Login(String username, String password) {
        Write("Attempting to locate users credentials...");
        int id = findUsersByUnPw(username, password);
        if(id > -1) {
            
            User u = this.parentThread.getUsers().get(id);
            if(u != null){
                //check if user is already logged in somehwere
                if(u._client != null && !u._client.equals(this.client)) {
                    Write("User: " + username + " is logged in elsewhere");
                    Respond("400 User is already logged in");
                } else if (u._client != null && u._client.equals(this.client)) {
                    Write("User: " + username + " is logged in to the current client");
                    Respond("400 User is already logged in");
                } else {
                    Write("Registered users session");
                    u._client = this.client;
                    Respond("200 OK");
                }        
            } else {
                Write("Error getting user from table");
            }
            
        } else {
            Respond("410 Wrong UserID or Password");
        }
        
    }//end of Login()

    private void Logout() {
        int id = findUserByConnection();
        if(id > -1){
            this.parentThread.getUsers().get(id)._client = null;
        }
        Respond("200 OK");
    }//end of Logout()

    private void Who(){
        for(User u : this.parentThread.getUsers()){
            if(u._client != null) {
                Respond(u.ToString());
            }
        }
    }//end of Who()

    private void Look(String searchBy, String param){

        ArrayList<Record> results = new ArrayList<Record>();
        ArrayList<Record> contacts = this.parentThread.getContacts();
        switch(searchBy){
            case "1": //Search by firstname
                for(Record r: contacts) {
                    if(r._firstname.toUpperCase().contains(param.toUpperCase())){
                        results.add(r);
                    }
                }
                break;
            case "2"://Search by lastname
                for(Record r: contacts) {
                    if(r._lastname.toUpperCase().contains(param.toUpperCase())){
                        results.add(r);
                    }
                }
                break;
            case "3"://Search by phone number
                for(Record r: contacts) {
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
        } else if (results == null){
            Respond("404 Your search did not match any records");
        }
    }//end of Look()

    private void List() {
        //send each record back the client as a response
        Respond("200 OK");
        Respond("The list of records in the book:");
        for(Record r : this.parentThread.getContacts()){
            Respond(r.ToString());
        }
    }//end of List()

    private void Quit(){
        Logout();
        //close connection with client
        try{
            EndTx();
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
        Write("Notifying active connections of shutdown!");
        Broadcast("210 the server is about to shutdown ......");
        Broadcast("ENDTX");
        try{
            Quit();
            synchronized(handlers) {
                handlers.removeAllElements();
                Write("Number of connections: " + handlers.size());
                this.parentThread.sd();  
            }
        } catch (Exception e) {
            System.err.println("Ayyy you fucked up!");
        }
    }//end of ShutDown()

    private int findUsersByUnPw(String un, String pw){
        for(User u : this.parentThread.getUsers()){
            if(u._username.compareTo(un) == 0 && u._password.compareTo(pw) == 0) {
                return this.parentThread.getUsers().indexOf(u);
            }
        }
        return -1;
    }//end of findUsersByUnPw

    private int findUserByConnection(){
        for(User u : this.parentThread.getUsers()) {
            if(u._client != null && u._client.equals(this.client)){
                return this.parentThread.getUsers().indexOf(u);
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
            User u = this.parentThread.getUsers().get(id);
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
        this.sender.println(msg);
    }//end of Respond()

    private void EndTx(){
        this.sender.println("ENDTX");
    }//end of EndTx()

    private void Broadcast(String msg){
        // Broadcast it to everyone! 
        for(ServerThread c : handlers) {
            synchronized(handlers) {
                if(c != this){
                    c.sender.println(msg);
                    c.sender.flush();
                }
            }
        }
    }//end of Broadcast()

}//end of ChildThread class