
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class UDPClient {
	
	private static String uniqueID = UUID.randomUUID().toString();
	private static String[] autoAdj = {"Horrible", "Sweet", "Bland", "Crazy", "Memey", "Respectful", "Cautious", "Lumpy", "Stinky", "Sparkly"};
	private static String[] autoNoun = {"Banana", "Plant", "Dog", "Box", "Bear", "Eye", "Cat", "Lawyer", "Sloth", "Doctor"};
	static String nickName = " ";

	public static void main(String[] args) throws IOException {
		
		setNickName(autoAdj, autoNoun, uniqueID);
		Scanner sc = new Scanner(System.in);
		int port = 8123; // port to communicate over
		  
        // Create a datagram socket
        DatagramSocket ds = new DatagramSocket();
  
        InetAddress ip = InetAddress.getByName("localhost");
        byte buf[] = null;
  
        boolean quit = false;
        while (!quit) // loop until a quit condition
        {
        	System.out.print("Send (quit to exit): "); // prompt
        	// read the input from the user
            String input = sc.nextLine();
            
            String message = nickName + " " + input;
  
            // convert the string into a byte array
            buf = message.getBytes();
  
            // create a datagram packet to send
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
  
            // and send it
            ds.send(packet);
  
            // break the loop if user enters "bye"
            if (input.equalsIgnoreCase("quit"))
                quit=true;
        }
        
        ds.close();
        sc.close();
	}
	
	public static void setNickName(String[] adjectives, String[] nouns, String UID) {
		int rnd1 = new Random().nextInt(adjectives.length);
		int rnd2 = new Random().nextInt(adjectives.length);
		nickName = adjectives[rnd1] + nouns[rnd2] + uniqueID;
	}

}