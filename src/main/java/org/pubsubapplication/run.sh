#!/bin/bash

# Compile the Server and Client classes
javac Server.java
javac Client.java

# Start the server in a new terminal
gnome-terminal -- bash -c "java Server 5000"

# Delay to ensure the server starts before clients connect
sleep 2

# Start 3 subscribers in separate terminals
for i in {1..3}
do
    gnome-terminal -- bash -c "java Client 127.0.0.1 5000 SUBSCRIBER"
done

# Start 3 publishers in separate terminals
for i in {1..3}
do
    gnome-terminal -- bash -c "java Client 127.0.0.1 5000 PUBLISHER"
done
