

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

	public static void main(String[] args) throws IOException
    {
		int port = 8123; // port to communicate over
		
        // Create a datagram socket on the port
        DatagramSocket ds = new DatagramSocket(port);
        // create a byte array of size 65535 to receive data
        byte[] receive = new byte[65535];
  
        // packet for receiving data
        DatagramPacket packet = null;
        
        boolean quit = false;
        while (!quit) // keep going until quit
        {
        	System.out.println("SERVER waiting for data");
        	
            // create the packet to receive data
            packet = new DatagramPacket(receive, receive.length);
  
            // receive the data into the packet
            ds.receive(packet);
  
            // convert byte array to string
            String data = ByteToString(receive);
            
            System.out.println("SERVER received data");
            
            System.out.println("SERVER received: " + data);
            
            // Quit if the client sends "quit"
            if (data.equalsIgnoreCase("quit"))
            {
                System.out.println("SERVER Client requested to exit");
                break;
            }
  
            // Create new empty buffer after each packet
            receive = new byte[65535];
        }
    }
  
    // Convert byte array into string
    public static String ByteToString(byte[] a)
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