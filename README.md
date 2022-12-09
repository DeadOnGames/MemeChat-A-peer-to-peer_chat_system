# MemeChat: A peer-to-peer_chat_system
A distributed computing system which distributes data and processing throughout the nodes of a connected system in a peer-to-peer architecture.

## Installing and running

Install MemeChat by git cloning from the repository as normal
```
git clone git@github.com:DeadOnGames/Peer-to-peer_chat_system.git
```

From a bash shell, compile the code as normal:
```
javac Node.java
```
Then to run MemeChat, you will need to specify a port number:
```
java Node.java <port number>
java Node.java 5001
```

Note: Multiple bash shells on the same machine can run the program on different port numbers e.g.
```
java Node.java 5002
java Node.java 5003
```
## How to use MemeChat
Upon logging into MemeChat, you will be told what port you are running on, there will be a printed greeting and you will be assinged a random username e.g.
```
Starting on port 5000
-------Welcome to MemeChat-------
Username: StinkyCat_82482236-cabb-4dc8-9ac9-2aa15b06c178
```
MemeChat supports a the following commands which can be viewed from a node by typing 'help'
```
help                open commands list
exit                logout
ping <ip> <port>    share ip and port with another node
pingall             share ip and port of all friends
add <ip> <port>     add ip and port to friends list
listfriends 	    print out friends list
printlogs     	    print out local logs
sendlogs     	    send local logs to all friends
updatelogs     	    send local logs to all friends
clearfriends        clear friends list
clearlogs     	    clear local logs
simMalformedData    simulate a malformed data send
laughbot            turn this node into a laughbot to laugh at everyone
meme                send a ascii art meme
```
More specific information on each command / feature can be found in the software document.
