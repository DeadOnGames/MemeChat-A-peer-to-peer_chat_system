import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class Node{
	public NetworkListener nlistener = null;
    public KeyboardListener klistener = null;
    public int port = 5000;
    public List<RemoteNode> nodes = new ArrayList<RemoteNode>();
    public List<ChatItem> chatLog = new ArrayList<ChatItem>();
	public String nickName;
	boolean isRobot = false;

    public class RemoteNode {
        public String ip;
        public int port;
        public String nickName;
    }
    
    public class ChatItem implements Comparable<ChatItem>{
        public String timeStamp;
        public String senderNickName;
        public String chatId = UUID.randomUUID().toString();
        public String logContent;
        
        
		@Override
		public int compareTo(ChatItem o) {
			return getTimeStamp().compareTo(o.getTimeStamp());
		}


		public String getTimeStamp() {
			return timeStamp;
		}
    }

    public class NetworkListener extends Thread {
        Node node = null;
        public NetworkListener(Node n) { node = n; }
        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket(node.port);
                byte[] receive = new byte[65535];
                DatagramPacket packet = null;
                while(true)
                {
                    packet = new DatagramPacket(receive, receive.length);
                    socket.receive(packet);
                    String data = ByteToString(receive);
                    String remoteip = packet.getAddress().toString().substring(1);
                    node.receive(data, remoteip);
                    receive = new byte[65535];
                }
            } catch(Exception e) { e.printStackTrace(); }
        }

        public String ByteToString(byte[] a)
        {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret.toString();
        }
    }

    public class KeyboardListener extends Thread {
        Node node = null;
        public KeyboardListener(Node n) { node = n; }
        public void run() {
            Scanner sc = new Scanner(System.in);
            while(true)
            {
                String line = sc.nextLine();
                node.commandHandler(line);
            }
        }
    }
    
    

    public static void setNickName(Node n) {
    	String uniqueID = UUID.randomUUID().toString();
    	if(n.isRobot == true) {
    		n.nickName = "LaughtBot" + "_" + uniqueID;
    	} else {
    	String[] autoAdj = {"Horrible", "Stray", "Crunchy", "Sweet", "Bland", "Crazy", "Memey", "Respectful", "Cautious", "Lumpy", "Stinky", "Sparkly"};
    	String[] autoNoun = {"Banana", "Plant", "Doge", "Face", "Bear", "Eye", "Cat", "Lawyer", "Sloth", "Doctor"};
		int rnd1 = new Random().nextInt(autoAdj.length);
		int rnd2 = new Random().nextInt(autoNoun.length);
		n.nickName = autoAdj[rnd1] + autoNoun[rnd2] + "_" + uniqueID;
    	}
    }
    
    public static void main(String[] args)
    {
        int tport = 5000;
        if (args.length>0) tport = Integer.parseInt(args[0]);
        System.out.println("Starting on port "+tport);
        Node n = new Node();
        setNickName(n);
        n.port = tport;
        n.go();
        
        System.out.print("-------Welcome to MemeChat-------\n");
        System.out.print("Username: " + n.nickName+"\n");
        System.out.println("Type a message + ENTER to send or type 'help' for commands list");
        
    }

    public void go()
    {
        klistener = new KeyboardListener(this);
        klistener.start();
        nlistener = new NetworkListener(this);
        nlistener.start();
    }

    public void send(String nickName, String msg, String ip, int remoteport) {
        try {
        	String conCat = nickName +": "+ msg;
            byte[] bytes = conCat.getBytes();
            InetAddress inet = InetAddress.getByName(ip);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inet, remoteport);
            ds.send(packet);
        } catch(Exception e) {
        	System.out.println("Error sending packet");
            e.printStackTrace();
        }
    }
    
    public void sendMalformedData(String nickName, String msg, String ip, int remoteport) {
        try {
        	String conCat = nickName +": "+ msg;
            byte[] bytes = conCat.getBytes();
            //Just mess up some of the bytes
            for(int i = 5; i <= bytes.length-1; i++) {
            	bytes[i] += 27;
            }
            InetAddress inet = InetAddress.getByName(ip);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inet, remoteport);
            System.out.println("Sending : " + bytes.toString());
            ds.send(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void commandHandler(String line) // keyboard line entered
    {
        System.out.println(nickName + ": " +line);
        String[] cmd = line.split(" ");
        if (cmd[0].equalsIgnoreCase("exit"))
            System.exit(0);
        else if(cmd[0].equals("help")) {
        	printCommands();
        }
        else if(cmd[0].equalsIgnoreCase("ping"))
        {
            if(cmd.length != 3) System.out.println("ping IP PORT");
            else {
                String remoteip = cmd[1];
                int remoteport = Integer.parseInt(cmd[2]);
                System.out.println("Sending a PING to "+remoteip+":"+remoteport);
                send(nickName, "PING "+port,remoteip,remoteport);
            }
        }
        else if (cmd[0].equalsIgnoreCase("add")) {
        	if(cmd.length != 3) System.out.println("add IP PORT");
            else {
	            RemoteNode rn = new RemoteNode();
	            rn.ip = cmd[1];
	            rn.port = Integer.parseInt(cmd[2]);
	            //setNickName(rn);
	            nodes.add(rn);
            }
        }
        else if (cmd[0].equalsIgnoreCase("listfriends")) {
        	System.out.println("-------Friends List-------");
        	if(nodes.size() == 0) {
        		System.out.println("Uh oh, you don't have any friends yet :(");
        	} else {
	            for(RemoteNode n: nodes)
	            {
	                System.out.println(n.nickName+" "+ n.ip+":"+n.port);
	            }
        	}
            System.out.println("---------------------------");
        }
        else if (cmd[0].equalsIgnoreCase("printlogs")) {
            printLocalChatLogs();
        }
        else if (cmd[0].equalsIgnoreCase("sendlogs")) {
        	//Send everyone in your friendslist your chatItems from local logs
        	for(RemoteNode j: nodes) {
        		String remoteip = j.ip;
                int remoteport = j.port;
        		for(ChatItem c : chatLog) {
        		send(nickName,"sendlogs "+ c.chatId + " " + c.timeStamp +" "+ c.logContent,remoteip,remoteport);
        		}
        	}
        } else if (cmd[0].equalsIgnoreCase("clearlogs")) {
        	chatLog.clear();
        } else if (cmd[0].equalsIgnoreCase("clearfriends")) {
        	nodes.clear();
        }
        else if (cmd[0].equalsIgnoreCase("PINGALL")) {
        	//Send everyone a ping which will share ip and port
            for(RemoteNode n: nodes)
            {
                String remoteip = n.ip;
                int remoteport = n.port;
                for(RemoteNode j: nodes)
                {
                	String nodeDeets = j.nickName + " " + j.ip + " " + j.port;
                	send(nickName,"PINGALL "+nodeDeets,remoteip,remoteport);
                }
                
            }
        
        } else if (cmd[0].equalsIgnoreCase("updatelogs")) {
        	
        	for(RemoteNode j: nodes) {
        		String remoteip = j.ip;
                int remoteport = j.port;
        		for(ChatItem c : chatLog) {
        			//Send everyone in nodes list local chat log
        			send(nickName,"sendlogs "+ c.chatId + " " + c.timeStamp +" "+ c.logContent,remoteip,remoteport);
        		}
        		
        		//Send a request for them to send you their local logs
            	send(nickName,"updatelogs ", remoteip, remoteport);
        	}
        	
        	
        } else if(cmd[0].equalsIgnoreCase("simLostPacket")) {
        	
        } else if(cmd[0].equalsIgnoreCase("simMalformedData")) {
        	//Send some incorrect data over the network
        	for(RemoteNode j: nodes)
            {
        		String remoteip = j.ip;
                int remoteport = j.port;
                sendMalformedData(nickName,"This is a test for malformed data - message success!",remoteip,remoteport);
            }
        } else if(cmd[0].equalsIgnoreCase("simDisconnectNode")) {
        	
        } else if(cmd[0].equalsIgnoreCase("laughbot")) {
        	if(nodes.size() == 0) {
        		System.out.print("Uh oh, looks like this chatbot needs some friends first!");
        	} else {
	        	isRobot = true;
	        	setNickName(this);
	        	chatBotSend();
        	}
        }
        else {
        	if(nodes.size() == 0) {
        		System.out.println("Uh oh, you don't have any friends yet :(");
        	} else {
        		//Broadcast message across the network
        		for(RemoteNode n: nodes) {
                    String remoteip = n.ip;
                    int remoteport = n.port;
                    send(nickName,line,remoteip,remoteport);
                    
                }
        		logChat(line);
        	}}
        }
    

    private void printCommands() {
		// TODO Auto-generated method stub
    	System.out.println("-------Commands List-------");
    	System.out.println("help                open commands list");
    	System.out.println("exit                logout");
    	System.out.println("ping <ip> <port>    share ip and port with another node");
    	System.out.println("pingall             share ip and port of all friends");
    	System.out.println("add <ip> <port>     add ip and port to friends list");
    	System.out.println("listfriends 	    print out friends list");
    	System.out.println("printlogs     	    print out local logs");
    	System.out.println("sendlogs     	    send local logs to all friends");
    	System.out.println("updatelogs     	    send local logs to all friends");
    	System.out.println("clearfriends        clear friends list");
    	System.out.println("clearlogs     	    clear local logs");
    	System.out.println("simMalformedData    simulate a malformed data send");	
    	System.out.println("laughbot            turn this node into a laughbot to laugh at everyone");
    	
    	System.out.println("--------------------------");
	}

	private void logChat(String chatItem) {
		
    	LocalDateTime date = LocalDateTime.now();
	    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	    String formattedDate = date.format(dateFormat);
        
        ChatItem c = new ChatItem();
    	c.timeStamp = formattedDate;
    	c.logContent = chatItem;
    	c.senderNickName = nickName;
    	chatLog.add(c);
		
	}

	public void printLocalChatLogs() {
		System.out.println("-------CHAT LOG-------");
		for(Node.ChatItem c : chatLog) {	
			System.out.println(c.timeStamp +" "+ c.senderNickName +" "+ c.logContent);
		}
		System.out.println("----------------------");
	}
	
	public void syncChatLog() {
		//Sort chatItems in ChatLog by timestamps
        Collections.sort(chatLog);
		
	}
	
	public void receive(String line, String remoteip){	
        String[] parts = line.split(" ");
        //Malformed data check
        if(parts.length >= 2) {		//Parts will always be at least 2: username + content 
        	System.out.println(line);
        	if(isRobot) {
        		chatBotLaugh();
        	} 
        if (parts[1].equalsIgnoreCase("PING")) {
            addContact(remoteip, Integer.parseInt(parts[2]), parts[0]);
            send(nickName,"PONG" +" "+ port,remoteip,Integer.parseInt(parts[2]));
        } else if (parts[1].equalsIgnoreCase("PONG")) {
            addContact(remoteip, Integer.parseInt(parts[2]), parts[0]);
        } else if(parts[1].equalsIgnoreCase("PINGALL")) {
        	addContact(remoteip, Integer.parseInt(parts[4]), parts[2]);
        } else if(parts[1].equalsIgnoreCase("updatelogs")) {
	       int remoteport = 0;
        	//Get remoteport from nodes list
        	for(RemoteNode n: nodes) {
        		if(parts[0].equalsIgnoreCase(n.nickName)){
        			remoteport = n.port;
        		}
        	}
        	
        	//Send back local log to node
			for(ChatItem c : chatLog) {
				send(nickName,"sendlogs "+ c.chatId + " " + c.timeStamp +" "+ c.logContent,remoteip,remoteport);
			}
        	
        } else if(parts[1].equalsIgnoreCase("sendlogs")) {
        	String content = "";
        	for(int i=5; i <= parts.length -1; i++) {
        		content += parts[i] + " ";
        	}
        	updateChatLog(parts[0], parts[2], parts[3] +" "+ parts[4], content );
        }}
    }
    
    public void addContact(String remoteip, int remoteport, String remotenickname) {
    		Boolean isDuplicate = false;
	    	RemoteNode rn = new RemoteNode();
	    	rn.ip = remoteip;
	        rn.port = remoteport;
	        
	        if(remotenickname.charAt(remotenickname.length() - 1) == ':') {
	        	//Remove ":"
	        	remotenickname = remotenickname.substring(0, remotenickname.length() - 1);  
	        }
	        	rn.nickName = remotenickname;
	        
	        //Check if it is this node's nickname
	        if(rn.nickName.equalsIgnoreCase(nickName)) {
	        	isDuplicate = true;
	        } else {
		        for(RemoteNode r : nodes) {
		        	if (r.nickName.equalsIgnoreCase(rn.nickName) ) {	
		        		//If node is already in nodes list 
		        		isDuplicate = true;	
		        	}
		        }
	        }
	        //Check if the node is already in nodes list
	        if(!isDuplicate) {
	        	nodes.add(rn);
	        }
    }
    
    public void updateChatLog(String nickName, String chatId, String timestamp, String content) {
    	//Create a new chat item and add to the chatlog
    	Boolean isDuplicate = false;
    	ChatItem rc = new ChatItem();
    	rc.chatId = chatId;
    	rc.senderNickName = nickName;
    	rc.timeStamp = timestamp;
    	rc.logContent = content;
        
        for(ChatItem ch : chatLog) {
        	if (ch.chatId.equalsIgnoreCase(rc.chatId)) {	//If chat item is already in local chat log
        		isDuplicate = true;	
        	}
        }
        if(!isDuplicate) {
        	chatLog.add(rc);
        }
    	
        syncChatLog();
    }
    
    public void chatBotSend() {
    	String line = "Hi I'm a laughbot! How is everyone?";
    	for(RemoteNode n: nodes) {
            String remoteip = n.ip;
            int remoteport = n.port;
            send(nickName,line,remoteip,remoteport);
            logChat(line);
        }
    	System.out.println(nickName + ": " + line);
    }
    
    public void chatBotLaugh() {
    	String[] laughs = {"Haha", "XD", "Hahaha", "Heehee", "Good meme", "lol", "MHAHAHAHAHAHA", "HAHAHA", ":D"};
		int rnd1 = new Random().nextInt(laughs.length);
		String message = laughs[rnd1];
    	for(RemoteNode n: nodes) {
            String remoteip = n.ip;
            int remoteport = n.port;
            send(nickName,message,remoteip,remoteport);
            logChat(message);
        }
    	System.out.println(nickName + ": " + message);
    }

}