# CIS427_Project2_Group8
#Jonnathen Ravelo
#Ayeha Saleem

#Description
- A multi-client/server project that allows the user to input basic information in the client which is sent and processed by the server.

#Installation

for Windows:
- 1.) ensure that the java compiler is mapped as an environment variable
- 2.) in windows command prompt or powershell, navigate to folder ravelo_j_p2 and run the command:
* "javac Server.java"
* "javac Client.java"

for Unix/iOS:
- 1.) In the terminal, navigate to the folder ravelo_j_p1
- 2.) run Makefile in the folder containing Server.java and Client.java or enter the commands "javac Server.java" and "javac Client.java"

### Starting

for Windows:
- 1.) ensure that the java JVM is mapped as an environment variable
- 2.) in windows commmand prompt or powershell, navigate to the folder "ravelo_j_p2" and run the commands:
* "java Server"
3.) starting anothter instance of command prompt or powershell, and in the same folder, run the commands:
* "java Client [server_ip_address]".

for Unix/Linux
- 1.) in the Terminal, navigate to the folder ravelo_j_p2
- 2.) start the server by entering the commands "java Server"
- 3.) in a new Terminal in the same folder, start the client by enter the commands "java Client [server_ip_address]".

### Known Bug/Issues
- The server is mapped to port 6833 and may not initialize if the port is already in use.

#Client Commands: 
* All commands are case-insensitive

"LOGIN"
+ LOGIN <UserName> <Password> - allows a user to "log in" to the system. Users will then be able to use "ADD", "DELETE", and "SHUTDOWN" commands. The UserName and Password sent to the server are validated against the known usernames and passwords stored in "Server.config"
+ e.g.
+ LOGIN jon doe01
+ 200 OK
- 200 OK                            //User has been successfully logged into the server
- 400 User is alreaady logged in    //Another client instance has tried to log into the server
- 410 Wrong UserID or Password      //The user input the wrong username and password combination

"LOGOUT" - Logout from the server.  A user is not allowed to send ADD, DELETE, and SHUTDOWN 
commands after logout, but it can still send LIST, LOOK, WHO, and QUIT commands.
+ e.g.
+ LOGOUT
+ 200 OK

"ADD" - allows an authenticated/authorized user to add a new record on the server if it doesn't already exist
+ e.g. 
+ ADD FirstName LastName 123-123-1234 
- 200 OK              //Successfully able to add the record
- 400 BAD REQUEST     //Unable to add a duplicate entry

"DELETE - allows an authenticated/authorized user to delete a record from the server if it exists by specifying the 4-digit record ID
+ DELETE <RecordID>
+ e.g. 
+ DELETE 1234
- 200 OK                        //if record was removed from server
- 400 BAD REQUEST              //if the record does not exist
- 500 INTERNAL SERVER ERROR    //if the recordId is invalid

"LIST" - allows user to view all records stored on server, command is parameterless
+ e.g. 
+ LIST
+ 1001 Fname1 Lname1 123-123-1230
+ 1002 Fname2 Lname2 123-123-1231
+ 1003 Fname3 Lanme3 123-123-1232
+ //nothing if empty

"WHO" - allows the user to view all active loggedin connections, command is parameterless
+ e.g.
+ WHO
+ user1     /ipaddress:port
+ user2     /ipaddress:port

"LOOK" - allows the user to view a collection of records matching the Category (int) and search criteria (string)
+ LOOK <[1-3]> <StrPattern>
+ e.g.
+ LOOK 3 313373
+ 200 OK
+ Found 2 matches
+ 1001 Lname1 Lname1 3133731111
+ 1003 Fname2 Lname2 3133734444
 
"QUIT" - close connection with the server. If the user is logged into the server they are automatically logged out. command is parameterless.
+ e.g.
+ QUIT
+ 200 OK
+ "Press any key to close the application..."

"SHUTDOWN" - allows the authorized(root) user to remotely shutdown the server and closes all current connection(s)
+ e.g.
+ (initiating Client)
+ SHUTDOWN
+ 200 OK 
+ (other clients)
+ 210 the server is about to shutdown ......        
+ "Press any key to close the application"
- 200 OK                                        //The client has successfully initiated the shutdown proceedure.
- 210 the server is about to shutdown ......    //notification sent to other clients connected to the server
- 402 User not allowed to execute this command  //The user is not logged into the correct account to initiate a remote shutdown of the server
 

