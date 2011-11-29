import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.HashMap;

import jpcap.*;
import jpcap.packet.*;

public class Main {

	public static void main(String[] args) {
		int i = 0;
		try {
			FileReader fr = new FileReader("/Users/jiehou/F11/Projects/Network/357814048631090/1321485257_bg_2011-11-16-18-36-30-appname.pcap");
			BufferedReader br = new BufferedReader(fr);
			HashMap<Integer, String> appNameMap = new HashMap<Integer, String>();
			String str = br.readLine();
			while(str != null) {
				appNameMap.put(new Integer(i), str);
				i++;
				str = br.readLine();
			}
			
			JpcapCaptor captor = JpcapCaptor.openFile("/Users/jiehou/F11/Projects/Network/357814048631090/1321485257_bg_2011-11-16-18-36-30.pcap");
			String[] ad_servers = {"141.212.113.211"};
			AdPacketFilter filter = new AdPacketFilter(ad_servers, appNameMap);
			i = 1;
			while(true){
				//read a packet from the opened file
				Packet packet=captor.getPacket();
				//if some error occurred or EOF has reached, break the loop
				if(packet == null || packet == Packet.EOF) break;
				//otherwise, print out the packet	  
				System.out.print(i++ + " ");
				filter.display(packet);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}