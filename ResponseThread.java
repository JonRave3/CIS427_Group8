
import java.net.*;
import java.io.*;

public class ResponseThread extends Thread {
    
    Socket server;
    BufferedReader listener;

    ResponseThread(Socket soc){
        this.server = soc;
    }

    public void run(){
        try {
            listener = new BufferedReader(new InputStreamReader(server.getInputStream()));
            getResponse();
        } catch (Exception e) {
        }
    }
    public void cancel() { 
        interrupt(); 
    }

    private void getResponse() throws Exception {
		//Write("Awaiting response from server...");
		String responseLine = "";
		try{
			do{
				responseLine = listener.readLine();
				if(responseLine.equalsIgnoreCase("ENDTX")){
                    this.server.close();
                    Write("The server has been shutdown");
            		break;
				} else {
                    Write(responseLine);
                }
			}
			while(true);
		} catch(Exception e){
            
        }
    }//end of getResponse()
    private void Write(String msg){
		System.out.println(msg);
	}
}