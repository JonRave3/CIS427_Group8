/* Me
 * Client.java
 */

import java.io.*;
import java.net.*;

public class Client 
{
    public static final int SERVER_PORT = 6833;
	public static Socket clientSocket;
	public static PrintStream os;
	public static BufferedReader is, stdInput;
	public static OutputStreamWriter osw;
	public static BufferedWriter bw;

	public static void main(String[] commands) 
    {
		String userInput = null;
		String serverInput = null;
		Console console = System.console();
		//Check the number of command line parameters
		if (commands.length > 0 && init(commands[0]))
		{
			run();
			userInput =  console.readLine();
		} else {
			System.out.println("Unable to initialize client. Exiting...");
			userInput = console.readLine();	
			System.exit(1);
		}
	}//end of main()

	private static boolean init(String ip) {
		boolean status = false;
		System.out.println("Attempting to initialize client...");
		try 
		{
			// Try to open a socket on SERVER_PORT
			clientSocket = new Socket(ip, SERVER_PORT);
			// Try to open input and output streams
			is = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
			stdInput = new BufferedReader(new InputStreamReader(System.in));
			
			os = new PrintStream(clientSocket.getOutputStream());
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			status = true;
		} 
		catch (UnknownHostException e) 
		{
			System.err.println("Don't know about host: hostname");
		} 
		catch (IOException e) 
		{
			System.err.println("Couldn't get I/O for the connection to: hostname");
		}
		return status;
	}//end of init()
	
	private static void run(){
		try {
			String userInput;
			while ((userInput = stdInput.readLine()) != null)
			{
				if(userInput.contains("ADD")) {
					//ADD FNAME(8) LNAME(8) PHONE(12)\n
					if(checkAdd(userInput)){
						sendCommand(userInput);
					} else {
						System.out.println("Invalid Add command. Please try again.");
					}
				}
		
				else if(userInput.contains("DELETE")) {
					if(checkDelete(userInput)){
						sendCommand(userInput);
					} else {
						System.out.println("Invalid Delete command. Please try again");
					}
					
				}
					
				else if(userInput.equalsIgnoreCase("LIST")) {
					sendCommand(userInput);
				}// END LIST
				else if(userInput.equalsIgnoreCase("QUIT")) {
					end();
				}//END QUIT
				else if(userInput.equalsIgnoreCase("SHUTDOWN")) {
					sendCommand(userInput);
				}// END SHUTDOWN
				
			}// END WHILE
			end();
		} 
		catch (IOException e) 
		{
			System.err.println("IOException:  " + e);
		}

	}//end of run()
	private static boolean sendCommand(String validCmd) {
		try {
			bw.write(validCmd);
			return true;
		} catch (Exception e){
			System.err.println("Error: " + e);
			return false;
		}
	}//end of sendCommand()
	private static boolean checkAdd(String cmd){
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
		String[] args = cmd.split("\\s+");
		if(args[0].equalsIgnoreCase("DELETE")){
			if(args.length == 2 && args[1] != null && args[1].length() == 4) {
				try{
					int recordId = Integer.parseInt(args[1]);
					valid = true;
				} catch (Exception e){
					System.out.println("Invalid record id.");
				}
			}
		}
		return valid;
	}//end of checkDelete()

	private static void end(){
		try {
			os.close();
			is.close();
			clientSocket.close();   	
		 } catch(Exception e){
			 //Do nothing
		 }
	}
	private static boolean quit(){ return true;}//end of quit()
	private static void shutdown(){}//end of shutdown()
	
}
