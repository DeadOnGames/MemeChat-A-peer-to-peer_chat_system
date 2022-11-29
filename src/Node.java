import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class Node {

    public class RemoteNode {
        public String ip;
        public int port;
        public String nickName;
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
                node.keyboard(line);
            }
        }
    }
    
    public NetworkListener nlistener = null;
    public KeyboardListener klistener = null;
    public int port = 5000;
    public List<RemoteNode> nodes = new ArrayList<RemoteNode>();
	public String nickName;

    public static void setNickName(Node n) {
        String uniqueID = UUID.randomUUID().toString();
    	String[] autoAdj = {"Horrible", "Sweet", "Bland", "Crazy", "Memey", "Respectful", "Cautious", "Lumpy", "Stinky", "Sparkly"};
    	String[] autoNoun = {"Banana", "Plant", "Dog", "Box", "Bear", "Eye", "Cat", "Lawyer", "Sloth", "Doctor"};
		int rnd1 = new Random().nextInt(autoAdj.length);
		int rnd2 = new Random().nextInt(autoAdj.length);
		n.nickName = autoAdj[rnd1] + autoNoun[rnd2] + "_" + uniqueID;
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
        
        System.out.print("Welcome to MemeChat\n");
        System.out.println("Type a message + ENTER to send\n");
        System.out.println("Type 'exit' to leave chat\n");
        
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
            e.printStackTrace();
        }
    }

    public void keyboard(String line) // keyboard line entered
    {
        System.out.println(nickName + ": " +line);
        String[] cmd = line.split(" ");
        if (cmd[0].equalsIgnoreCase("exit"))
            System.exit(0);
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
            RemoteNode rn = new RemoteNode();
            rn.ip = cmd[1];
            rn.port = Integer.parseInt(cmd[2]);
            //setNickName(rn);
            nodes.add(rn);
        }
        else if (cmd[0].equalsIgnoreCase("list")) {
            for(RemoteNode n: nodes)
            {
                System.out.println(n.nickName+" "+ n.ip+":"+n.port);
            }
        }
        else if (cmd[0].equalsIgnoreCase("broadcast")) {
            for(RemoteNode n: nodes)
            {
                String remoteip = n.ip;
                int remoteport = n.port;
                System.out.println("Sending a PING to "+remoteip+":"+remoteport);
                send(nickName,"PING "+port,remoteip,remoteport);
            }
        
        } else {
        	/*
        	if(cmd.length != 3) System.out.println("message IP PORT");
        	else {
        	String remoteip = cmd[1];
            int remoteport = Integer.parseInt(cmd[2]);
            System.out.println(cmd[0] + " to "+remoteip+":"+remoteport);
            send(nickName, cmd[0],remoteip,remoteport);
            */
        	if(nodes.size() == 0) {
        		System.out.println("Uh oh, you don't have any friends yet :(");
        	} else {
        		for(RemoteNode n: nodes) {
                    String remoteip = n.ip;
                    int remoteport = n.port;
                    //System.out.println("Sending a PING to "+remoteip+":"+remoteport);
                    send(nickName,line,remoteip,remoteport);
                }
        	}
        	
        	
        	
        	}
        }
    

    public void receive(String line, String remoteip) // received data
    {
        //System.out.println(line+" from "+remoteip);
    	System.out.println(line);
        String[] parts = line.split(" ");
        if (parts[1].equalsIgnoreCase("PING")) {
        	String remotenickname = parts[0];	
            int remoteport = Integer.parseInt(parts[2]);
            RemoteNode rn = new RemoteNode();
            rn.ip = remoteip;
            rn.port = remoteport;
            rn.nickName = remotenickname;
            nodes.add(rn);
            send(nickName,"PONG" +" "+ port,remoteip,remoteport);
        } else if (parts[1].equalsIgnoreCase("PONG")) {
        	//Might not work bc PONG might not send port
        	String remotenickname = parts[0];	
        	int remoteport = Integer.parseInt(parts[2]);
        	RemoteNode rn = new RemoteNode();
            rn.ip = remoteip;
            rn.port = remoteport;
            rn.nickName = remotenickname;
            nodes.add(rn);
        }
    }

}