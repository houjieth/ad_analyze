import jpcap.JpcapCaptor;
import jpcap.packet.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdAnalyzer {
	private HashMap<Integer, String> appNameMap;
	private ArrayList<Packet> packets;
	private ArrayList<Packet> adPackets;
	
	public AdAnalyzer() {
		appNameMap = new HashMap<Integer, String>();
		packets = new ArrayList<Packet>();
		adPackets = new ArrayList<Packet>();
	}
	
	public void readNameMap(String nameMapFileName) {
		try {
			int i = 0;
			FileReader fr = new FileReader(nameMapFileName);
			BufferedReader br = new BufferedReader(fr);
			String str = br.readLine();
			while(str != null) {
				appNameMap.put(new Integer(i), str);
				i++;
				str = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readCapFile(String capFileName) {
		try {
			JpcapCaptor captor = JpcapCaptor.openFile(capFileName);
			while(true){
				Packet packet = captor.getPacket();
				if(packet == null || packet == Packet.EOF) 
					break;
				else
					packets.add(packet);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isTCPPacket(Packet p) {
		if (p instanceof TCPPacket) {
			return true;
		}
		else
			return false;
	}
	
	public boolean isUDPPacket(Packet p) {
		if (p instanceof UDPPacket) {
			return true;
		}
		else 
			return false;
	}
	
	public boolean isDNSPacket(Packet p) {
		if (p instanceof TCPPacket) {
			TCPPacket pp = (TCPPacket)p;
			if (pp.src_port == Port.DNS || pp.dst_port == Port.DNS) {
				return true;
			}
			else
				return false;
		}
		else if (p instanceof UDPPacket) {
			UDPPacket pp = (UDPPacket)p;
			if (pp.src_port == Port.DNS || pp.dst_port == Port.DNS) {
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public boolean isHTTPPacket(Packet p) {
		if (p instanceof TCPPacket) {
			TCPPacket pp = (TCPPacket)p;
			if (pp.src_port == Port.HTTP || pp.dst_port == Port.HTTP) {
				return true;
			}
			else
				return false;
		}
		else if (p instanceof UDPPacket) {
			UDPPacket pp = (UDPPacket)p;
			if (pp.src_port == Port.HTTP || pp.dst_port == Port.HTTP) {
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public boolean isUpLink(Packet p) {
		byte[] header = p.header;
		int linkType= new Integer(header[10]);
		if (linkType == 1)
			return true;
		else
			return false;
	}
	
	public Integer getAppNameIndex(Packet p) {
		byte[] header = p.header;
		int appNameIndex= new Integer(header[6]);
		return appNameIndex;
	}
	
	public String getAppNameFromIndex(Integer index) {
		return appNameMap.get(index);
	}
	
	public void printPacket(Packet p) {
		if (isTCPPacket(p)) {
			TCPPacket pp = (TCPPacket)p;
			System.out.print("[TCP]");
			if (isDNSPacket(pp)) {
				System.out.print("[DNS]\t");
			}
			else if (isHTTPPacket(pp))
				System.out.print("[HTTP]\t");
			else
				System.out.print("\t");
			System.out.print(pp.src_ip + ":" + pp.src_port + "\t");
			System.out.print(pp.dst_ip + ":" + pp.dst_port + "\t");
			System.out.print(getAppNameFromIndex(getAppNameIndex(pp)) + "\t");
			if (isUpLink(p))
				System.out.print("(UPLINK)");
			else
				System.out.print("(DOWNLINK)");
		}
		else if (isUDPPacket(p)) {
			UDPPacket pp = (UDPPacket)p;
			System.out.print("[UDP]");
			if (isDNSPacket(pp)) {
				System.out.print("[DNS]\t");
			}
			else if (isHTTPPacket(pp))
				System.out.print("[HTTP]\t");
			else
				System.out.print("\t");
			System.out.print(pp.src_ip + ":" + pp.src_port + "\t");
			System.out.print(pp.dst_ip + ":" + pp.dst_port + "\t");
			System.out.print(getAppNameFromIndex(getAppNameIndex(pp)) + "\t");
			if (isUpLink(p))
				System.out.print("(UPLINK)");
			else
				System.out.print("(DOWNLINK)");
		}
		else
			System.out.print("[OTHER]\t");
	}
	
	public void printAllPackets() {
		int i = 1;
		System.out.println("#\ttype\tsrc\t\t\tdst\t\t\tappName\t\tup/down\n");
		for (Packet p : packets) {
			System.out.print(i + "\t");
			printPacket(p);
			System.out.print("\n");
			i++;
		}
	}
	
	public void inspectPacket(Packet p) {
		if (isDNSPacket(p)) {
			if (isUpLink(p)) {
				String DNSRequest = getDNSRequest(p.data);
			}
		}
	}
}