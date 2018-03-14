/*
Jonnathen Ravelo, Ayesha Saleem
CIS427 Project2
*/

import java.io.*;
import java.net.*;

public class Client 
{
	public static final int SERVER_PORT = 6833;
	private static final int CLIENT_PORT = 7900;
	public static Socket clientSocket;
	public static PrintStream sender;
	public static Console console;
	
	public static void main(String[] commands) 
    {
		String userInput = null;
		String serverInput = null;
		
		//Check the number of command line parameters
		if (commands.length > 0 && init(commands[0]))
		{
			Write("Initialized Client!");
			run();
			Write("Press any key to exit.");
		} else {
			Write("Unable to initialize client. Press any key to exit.");
		}
		userInput = ReadInput();	
		System.exit(1);

	}//end of main()

	private static boolean init(String ip) {
		boolean status = false;
		Write("Attempting to initialize client...");
		console = System.console();
		try 
		{
			// Try to open a socket 
			clientSocket = new Socket(ip, SERVER_PORT);
			// Try to open input and output streams
			sender = new PrintStream(clientSocket.getOutputStream());
			status = true;
		} 
		catch (UnknownHostException e) 
		{
			Write("Unable to create socket on host: " + ip);
		} 
		catch (IOException e) 
		{
			Write("Couldn't get I/O for the connection to: " + ip);
		}
		return status;
	}//end of init()
	
	private static void run(){
		boolean end = false;
		//Start listening for Server messages...
		ResponseThread rThread = new ResponseThread(clientSocket);
		rThread.start();

		while(!clientSocket.isClosed()){

			try {
				Write("Enter a command: ");
				String userInput = ReadInput();

				//Write("Received input from user: " + userInput);
				if(userInput.toUpperCase().contains("ADD")) {
					//ADD FNAME(8) LNAME(8) PHONE(12)\n
					if(checkAdd(userInput)){
						sendCommand(userInput);
					} else {
						Write("Invalid Add command.");
					}
				}//END ADD
				else if(userInput.toUpperCase().contains("DELETE")) {
					if(checkDelete(userInput)){
						sendCommand(userInput);
					} else {
						Write("Invalid Delete command.");
					}
				}//END DELETE
				else if(userInput.toUpperCase().contains("LOGIN")) {
					if(checkLogin(userInput)){
						sendCommand(userInput);
					} else {
						Write("Invalid LOGIN command.");
					}
				}//END LOGIN
				else if(userInput.toUpperCase().contains("LOOK")) {
					if(checkLook(userInput)){
						sendCommand(userInput);
					} else {
						Write("Invalid LOOK command.");
					}
				}//END LOOK
				else if(userInput.toUpperCase().equalsIgnoreCase("LOGOUT") || 
						userInput.toUpperCase().equalsIgnoreCase("WHO") || 
						userInput.toUpperCase().equalsIgnoreCase("LIST") || 
						userInput.toUpperCase().equalsIgnoreCase("SHUTDOWN")) {
					sendCommand(userInput);
				}//END LOGOUT, WHO, LIST, SHUTDOWN
				else if(userInput.toUpperCase().equalsIgnoreCase("QUIT")) {
					if(sendCommand(userInput)){
						Thread.sleep(200);
						end();
					}
				}//END QUIT
				else if(userInput != null && userInput.length() > 0){
					Write("Invalid command. Please try again.");
				}
				Thread.sleep(200);			
			} 
			catch (Exception e) 
			{

			}
		}//END WHILE
		Write("Connection to server has terminated...");
	}//end of run()

	private static boolean sendCommand(String validCmd) {
		//Write("Sending cmd to server.");
		try {
			sender.println(validCmd);
			return true;
		} catch (Exception e){
			Write("Error: " + e);
			return false;
		}
	}//end of sendCommand()

	///TODO
	private static boolean checkLogin(String cmd){
		//Write("checking if ADD command is valid...");
		boolean valid = false;
		String[] args = cmd.split("\\s+");
		//arg[0] == ADD
		if(args[0].equalsIgnoreCase("LOGIN"))
		if(args.length == 3 && args[1] != null && args[2] != null){
			String username = args[1];
			String password = args[2];
			if(username.length() > 0 && password.length() > 0) {
				valid = true;
			}
		}
		return valid;
	}//end of checkLogin()

	///TODO
	private static boolean checkLook(String cmd){
		//Write("checking if ADD command is valid...");
		boolean valid = false;
		String[] args = cmd.split("\\s+");
		//arg[0] == ADD
		if(args[0].equalsIgnoreCase("LOOK"))
		if(args.length == 3 && args[1] != null && args[2] != null){
			String searchBy = args[1];
			String param = args[2];
			if(searchBy.length() == 1 && param.length() > 0) {
				valid = true;
			}
		}
		return valid;
	}//end of checkLook()

	private static boolean checkAdd(String cmd){
		//Write("checking if ADD command is valid...");
		boolean valid = false;
		String[] args = cmd.split("\\s+");
		//arg[0] == ADD
		if(args[0].equalsIgnoreCase("ADD"))
		if(args.length == 4 && args[1] != null && args[2] != null &&  args[3] != null){
			String fname = args[1];
			String lname = args[2];
			String phone = args[3];
			if(fname.length() <= 8 && fname.length() > 0 &&
				lname.length() <= 8 && lname.length() > 0 &&
				phone.length() <= 12 && phone.length() > 0) {
				valid = true;
			}
		}
		return valid;
	}//end of checkAdd()

	private static boolean checkDelete(String cmd){
		boolean valid = false;
		//Write("Checking if DELETE command is valid...");
		String[] args = cmd.split("\\s+");
		if(args[0].equalsIgnoreCase("DELETE")){
			if(args.length == 2 && args[1] != null && args[1].length() == 4) {
				try{
					int recordId = Integer.parseInt(args[1]);
					valid = true;
				} catch (Exception e){
					Write("Invalid record id.");
				}
			}
		}
		return valid;
	}//end of checkDelete()

	private static boolean end(){
		try {
			sender.close();
			clientSocket.close();
		} catch(Exception e){
			 //Do nothing
		}
		return true;
	}//end of end()

	private static void Write(String msg){
		System.out.println(msg);
	}

	private static String ReadInput(){
		String input = "";
		try{
			input = console.readLine();
		} catch (Exception e){

		}
		return input; 
	}

}
