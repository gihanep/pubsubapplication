1. Compile the Server and Client
   javac Server.java
   javac Client.java
2. Start the server and client in separate terminals
   java Server <PORT> eg: java Server 5000
   java Client <Server IP> <Server PORT> <ROLE> eg: java Client localhost 5000 SUBSCRIBER
   java Client <Server IP> <Server PORT> <ROLE> eg: java Client localhost 5000 PUBLISHER
3. Send the messages from client(PUBLISHER)
4. Server and client(SUBSCRIBER) will print the client(PUBLISHER) messages
5. send "terminate" to exit

javac Server.java
javac Client.java
gnome-terminal -- bash -c "java Server 5000"
sleep 2
gnome-terminal -- bash -c "java Client 127.0.0.1 5000 SUBSCRIBER"
gnome-terminal -- bash -c "java Client 127.0.0.1 5000 PUBLISHER"