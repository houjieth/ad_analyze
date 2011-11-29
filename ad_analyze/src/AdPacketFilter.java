import jpcap.packet.*;
import java.util.HashMap;

public class AdPacketFilter {
	private String[] ad_servers;
	private HashMap<Integer, String>appNameMap;
	
	AdPacketFilter(String[] addrs, HashMap<Integer, String> map) {
		ad_servers = addrs;
		appNameMap = map;
	}
	public void display(Packet p) {
		byte[] header = p.header;
		Integer appNameIndex= new Integer(header[6]);
		System.out.print(appNameIndex + " ");
		if (p instanceof TCPPacket) {
			//System.out.println("TCP");
			System.out.print(((TCPPacket) p).src_ip + ":");
			System.out.print(((TCPPacket) p).src_port + " ");
			System.out.print(((TCPPacket) p).dst_ip + ":");
			System.out.print(((TCPPacket) p).dst_port + " ");
			String appName = appNameMap.get(appNameIndex);
			if(appName != null)
				System.out.print(appName + "\n");
			else
				System.out.print("\n");
		} else
			System.out.print("NON-TCP\n");
	}
}