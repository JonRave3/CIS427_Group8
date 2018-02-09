/* Me
 * Client.java
 */

import java.io.*;
import java.net.*;

public class Client 
{
	public static final int SERVER_PORT = 6833;
	private static final int CLIENT_PORT = 7900;
	public static Socket clientSocket;
	public static PrintStream os;
	public static BufferedReader is, stdInput;
	public static OutputStreamWriter osw;
	public static BufferedWriter bw;
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
			userInput =  ReadInput();
		} else {
			Write("Unable to initialize client. Exiting...");
			userInput = ReadInput();	
			System.exit(1);
		}
	}//end of main()

	private static boolean init(String ip) {
		boolean status = false;
		Write("Attempting to initialize client...");
		console = System.console();
		try 
		{
			// Try to open a socket 
			clientSocket = new Socket(ip, CLIENT_PORT);
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
			Write("Unable to create socket on host: " + ip);
		} 
		catch (IOException e) 
		{
			Write("Couldn't get I/O for the connection to: " + ip);
		}
		return status;
	}//end of init()
	
	private static void run(){
		while(true){
			try {
				String userInput;
				while ((userInput = ReadInput()) != null)
				{
					if(userInput.contains("ADD")) {
						//ADD FNAME(8) LNAME(8) PHONE(12)\n
						if(checkAdd(userInput)){
							sendCommand(userInput);
						} else {
							Write("Invalid Add command. Please try again.");
						}
					}
			
					else if(userInput.contains("DELETE")) {
						if(checkDelete(userInput)){
							sendCommand(userInput);
						} else {
							Write("Invalid Delete command. Please try again");
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
			catch (Exception e) 
			{
				Write("Exception:  " + e);
			}
		}//END WHILE
		

	}//end of run()
	private static boolean sendCommand(String validCmd) {
		try {
			bw.write(validCmd);
			return true;
		} catch (Exception e){
			Write("Error: " + e);
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
					Write("Invalid record id.");
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
