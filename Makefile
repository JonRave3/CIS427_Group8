CC=javac

# The target
all: Record.java User.java Server.class ServerThread.class Client.class ResponseThread.java

# To generate the class files
Record.class: Record.java
	$(CC) Record.java
User.class: User.java
	$(CC) User.java	
Server.class: Server.java
	$(CC) Server.java
ServerThread.class: ServerThread.java
	$(CC) ServerThread.java
Client.class: Client.java
	$(CC) Client.java
ResponseThread.class: ResponseThread.java	
	$(CC) ResponseThread.java

# clean out the dross
clean:
	-rm *.class
