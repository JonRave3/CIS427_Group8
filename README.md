# CIS427_Project1_Group8
#Jonnathen Ravelo
#Ayeha Saleem

#Description
- A simple client/server project that allows the user to input basic information in the client which is sent and processed by the server

#Installation
for Windows:
- ensure that the java compiler is mapped as an environment variable
- in windows command prompt or powershell, navigate to folder ravelo_j_p1 and run the command:
* "javac Server.java"
* "javac Client.java"

for Unix/iOS:
- In the terminal, navigate to the folder ravelo_j_p1
- run Makefile in the folder containing Server.java and Client.java or enter the commands "javac Server.java" and "javac Client.java"

#Starting
for Windows:
- ensure that the java JVM is mapped as an environment variable
- in windows commmand prompt or powershell, navigate to the folder "ravelo_j_p1" and run the commands:
* "java Server"
- starting anothter instance of command prompt or powershell, and in the same folder, run the commands:
* "java Client [ip_address]", it is recommended to use "127.0.0.1" (localhost)

for Unix/Linux
- in the Terminal, navigate to the folder ravelo_j_p1
- start the server by entering the commands "java Server"
- in a new Terminal in the same folder, start the client by enter the commands "java Client [ip_address]"; it is recommended to use "127.0.0.1" (localhost)

#Known Bug/Issues
- Unable to start server: The server is mapped to port 6833 and may not initialize if the port is already in use.

#Client Commands: 
* All commands are case-insensitive

"ADD" - allows the user to add a new record on the server if it doesn't already exist
e.g. 
ADD FirstName LastName 123-123-1234 
- 200 OK              //Successfully able to add the record
- 400 BAD REQUEST     //Unable to add a duplicate entry

"DELETE - allows the user to delete a record from the server if it exists by specifying the 4-digit record ID
e.g. 
+ DELETE 1234
+ 200 OK                        //if record was removed from server
+ 400 BAD REQUEST              //if the record does not exist
+ 500 INTERNAL SERVER ERROR    //if the recordId is invalid

"LIST" - allows user to view all records stored on server, command is parameterless
e.g. 
LIST
1001 Fname1 Lname1 123-123-1230
1002 Fname2 Lname2 123-123-1231
1003 Fname3 Lanme3 123-123-1232
//nothing if empty

"QUIT" - close connection with the server, command is parameterless
e.g.
QUIT
"Press any key to close the application..."

"SHUTDOWN" - allows the user to remotely shutdown the server and closes the current connection
e.g.
SHUTDOWN
200 OK
"Press any key to close the application"
 

