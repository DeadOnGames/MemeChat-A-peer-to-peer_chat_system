import java.net.*;
import java.io.*;
import java.util.*;

public class ChatSystem 
{
    private static final String TERMINATE = "Exit";
    static String nickName, temp;
    static volatile boolean finished = false;
    private static String uniqueID = UUID.randomUUID().toString();
	private static String[] autoAdj = {"Horrible", "Sweet", "Bland", "Crazy", "Memey", "Respectful", "Cautious", "Lumpy", "Stinky", "Sparkly"};
	private static String[] autoNoun = {"Banana", "Plant", "Dog", "Box", "Bear", "Eye", "Cat", "Lawyer", "Sloth", "Doctor"};
	private static int port;
	private static MulticastSocket socket;
    public static void main(String[] args)
    {
            try
            {
                InetAddress group = InetAddress.getByName("225.4.5.6");
                port = 8123; // port to communicate over
                Scanner sc = new Scanner(System.in);
                System.out.print("Welcome to MemeChat\n");
                System.out.print("Enter username or press ENTER to skip: ");
                temp = sc.nextLine();
                
                if(temp.length() > 0) {
                	if(temp.equals("port_failure_sim")) {
                		//A simulated failure on a node
                		simulatePortFailure(8122);
                		nickName = temp + "_" + uniqueID;
                    	System.out.print("---SIMULATED FAILURE PORT---\n");
                	} else {
                		//Set nickname and attach a UUID
                		nickName = temp + "_" + uniqueID;
                    	System.out.print("Your nickname is " + nickName + "\n");
                	}               	
                } else {
                	setNickName(autoAdj, autoNoun, uniqueID);
                	System.out.print("Your nickname is " + nickName + "\n");
                }
                
                
                socket = new MulticastSocket(port);
              
                // Since we are deploying
                socket.setTimeToLive(0);
                //this on localhost only (For a subnet set it as 1)
                  
                socket.joinGroup(group);
                Thread t = new Thread(new
                ReadThread(socket,group,port));
              
                // Spawn a thread for reading messages
                t.start(); 
                  
                // sent to the current group
                System.out.println("Type a message + ENTER to send\n");
                System.out.println("Type 'exit' to leave chat\n");
                
                	sendMessage(sc, socket, group, port);
                
            }
            catch(SocketException se)
            {
                System.out.println("Error creating socket");
                se.printStackTrace();
            }
            catch(IOException ie)
            {
                System.out.println("Error reading/writing from/to socket");
                ie.printStackTrace();
            }
    }
    
    public static void setNickName(String[] adjectives, String[] nouns, String UID) {
		int rnd1 = new Random().nextInt(adjectives.length);
		int rnd2 = new Random().nextInt(adjectives.length);
		nickName = adjectives[rnd1] + nouns[rnd2] + uniqueID;
    }
    
    
    public static void sendMessage(Scanner sc, MulticastSocket socket, InetAddress group, int port) {
    	while(true)
        {
	    	String message;
	        message = sc.nextLine();
	        if(message.equalsIgnoreCase(ChatSystem.TERMINATE))
	        {
	            finished = true;
	            try {
					socket.leaveGroup(group);
				} catch (IOException gr) {
					System.out.println("Error disconnecting from socket");
					gr.printStackTrace();
				}
	            socket.close();
	            break;
	        }
	        message = nickName + ": " + message;
	        byte[] buffer = message.getBytes();
	        DatagramPacket datagram = new
	        DatagramPacket(buffer,buffer.length,group,port);
	        try {
				socket.send(datagram);
			} catch (IOException e) {
				System.out.println("Error writing to socket");
				e.printStackTrace();
			}
        }
    }
    
    public static void simulatePortFailure(int portnum) {
    	//Simulate a socket read/write failure
    	ChatSystem.port = portnum;
    }

	public static int getPort() {
		return port;
	}

}
    
class ReadThread implements Runnable {
	
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private static final int MAX_LEN = 1000;
    ReadThread(MulticastSocket socket,InetAddress group,int port)
    {
        this.socket = socket;
        this.group = group;
        this.port = port;
    }
      
    @Override
    public void run()
    {
        while(!ChatSystem.finished)
        {
                byte[] buffer = new byte[ReadThread.MAX_LEN];
                DatagramPacket datagram = new
                DatagramPacket(buffer,buffer.length,group,port);
                String message;
            try
            {
                socket.receive(datagram);
                message = new
                String(buffer,0,datagram.getLength(),"UTF-8");
                if(!message.startsWith(ChatSystem.nickName))
                    System.out.println(message);
            }
            catch(IOException e)
            {
                System.out.println("Socket closed!");
            }
        }
    }
}