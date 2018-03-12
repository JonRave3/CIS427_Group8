CC=javac

# The target
all: Server.class ChildThread.class Client.class

# To generate the class files
Server.class: Server.java
	$(CC) Server.java

ChildThread.class: ChildThread.java
	$(CC) ChildThread.java

Client.class: Client.java
	$(CC) Client.java

# clean out the dross
clean:
	-rm *.class
