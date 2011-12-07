import jpcap.JpcapCaptor;
import jpcap.packet.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class AdAnalyzer {
	private HashMap<Integer, String> appNameMap;
	private ArrayList<Packet> packets;
	private ArrayList<Packet> adPackets;
	private ArrayList<Packet> adDNSPackets;
	private AdList adlist;
	private ArrayList<String> adDNSRequests;
	private ArrayList<InetAddress> adAddrs;

	private int ad_size = 0;
	private int all_size = 0;
	private PrintWriter sizePw;
	private PrintWriter timePw;
	public AdAnalyzer() throws FileNotFoundException {
		appNameMap = new HashMap<Integer, String>();
		packets = new ArrayList<Packet>();
		adPackets = new ArrayList<Packet>();
		adDNSPackets = new ArrayList<Packet>();
		adDNSRequests = new ArrayList<String>();
		adAddrs = new ArrayList<InetAddress>();
		adlist = new AdList();
		String sizeFileName = "/root/programming/AdLibrary/ad_analyze/size.txt";
		String timeFileName = "/root/programming/AdLibrary/ad_analyze/time.txt";
		File sizeFile = new File(sizeFileName);
		File timeFile = new File(timeFileName);
		FileOutputStream sizeOs = new FileOutputStream(sizeFile);
		FileOutputStream timeOs = new FileOutputStream(timeFile);
		OutputStreamWriter sizeOsw = new OutputStreamWriter(sizeOs);
		OutputStreamWriter timeOsw = new OutputStreamWriter(timeOs);
		sizePw = new PrintWriter(sizeOsw);
		timePw = new PrintWriter(timeOsw);
	}

	public void init(String[] args) {
		readNameMap(args[1]);
		readCapFile(args[0]);
		String[] hostFiles = new String[args.length - 2];
		for (int i = 2, j = 0; i < args.length; i++) {
			hostFiles[j] = args[i];
			j++;
		}
		try {
			adlist.init(hostFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readNameMap(String nameMapFileName) {
		try {
			int i = 0;
			FileReader fr = new FileReader(nameMapFileName);
			BufferedReader br = new BufferedReader(fr);
			String str = br.readLine();
			while (str != null) {
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
			while (true) {
				Packet packet = null;
				try {
					packet = captor.getPacket();
				} catch (Exception e) {
					continue;
				}
				if (packet == null || packet == Packet.EOF)
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
		} else
			return false;
	}

	public boolean isUDPPacket(Packet p) {
		if (p instanceof UDPPacket) {
			return true;
		} else
			return false;
	}

	public boolean isDNSPacket(Packet p) {
		if (p instanceof TCPPacket) {
			TCPPacket pp = (TCPPacket) p;
			if (pp.src_port == Port.DNS || pp.dst_port == Port.DNS) {
				return true;
			} else
				return false;
		} else if (p instanceof UDPPacket) {
			UDPPacket pp = (UDPPacket) p;
			if (pp.src_port == Port.DNS || pp.dst_port == Port.DNS) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public boolean isHTTPPacket(Packet p) {
		if (p instanceof TCPPacket) {
			TCPPacket pp = (TCPPacket) p;
			if (pp.src_port == Port.HTTP || pp.dst_port == Port.HTTP) {
				return true;
			} else
				return false;
		} else if (p instanceof UDPPacket) {
			UDPPacket pp = (UDPPacket) p;
			if (pp.src_port == Port.HTTP || pp.dst_port == Port.HTTP) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public boolean isUpLink(Packet p) {
		byte[] header = p.header;
		int linkType = new Integer(header[10]);
		if (linkType == 1)
			return true;
		else
			return false;
	}

	public boolean isDownLink(Packet p) {
		return !isUpLink(p);
	}

	public Integer getAppNameIndex(Packet p) {
		byte[] header = p.header;
		int appNameIndex = new Integer(header[6]);
		return appNameIndex;
	}

	public String getAppNameFromIndex(Integer index) {
		return appNameMap.get(index);
	}

	public void printPacket(Packet p) {
		if (isTCPPacket(p)) {
			TCPPacket pp = (TCPPacket) p;
			System.out.print("[TCP]");
			if (isDNSPacket(pp)) {
				System.out.print("[DNS]\t");
			} else if (isHTTPPacket(pp))
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
		} else if (isUDPPacket(p)) {
			UDPPacket pp = (UDPPacket) p;
			System.out.print("[UDP]");
			if (isDNSPacket(pp)) {
				System.out.print("[DNS]\t");
			} else if (isHTTPPacket(pp))
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
		} else
			System.out.print("[OTHER]\t");
		System.out.print(" size: " + p.len);
	}

	public void printAllPackets() {
		int i = 1;
		System.out.println("#\ttype\tsrc\t\t\tdst\t\t\tappName\t\tup/down");
		for (Packet p : packets) {
			System.out.print(i + "\t");
			printPacket(p);
			System.out.print("\n");
			i++;
		}
	}

	public void inspectPacketTime(Packet p) {
		//String name = getAppNameFromIndex(getAppNameIndex(p));
		sizePw.println(p.len);
		timePw.println(p.sec);
	}

	public void inspectPacketTime(Packet p, String appName) {
		String name = getAppNameFromIndex(getAppNameIndex(p));
		if (name == null)
			return;
		if (!name.equals(appName))
			return;
		if (isUpLink(p))
			return;
		sizePw.println(p.len);
		timePw.println(p.sec);

		
	}

	
	public void inspectPacket(Packet p) {
		printPacket(p);
		all_size += p.len;
		if (isDNSPacket(p)) {
			String DNSRequest = getDNSRequest(p.data);
			if (isUpLink(p)) {
				System.out.print("request: " + DNSRequest);
				if (adlist.vagueMatch(DNSRequest, 1)) {
					adDNSRequests.add(DNSRequest);
					adDNSPackets.add(p);
					System.out.print(" AD DNS REQUEST " + DNSRequest);
				}
			} else if (isDownLink(p)) {
				System.out.print("respons to request " + DNSRequest + ": ");
				ArrayList<InetAddress> addrs = (ArrayList<InetAddress>) getDNSAddrs(p.data);
				for (InetAddress a : addrs)
					System.out.print(a + " ");
				if (isAdDNSRequest(DNSRequest)) {
					for (InetAddress a : addrs)
						adAddrs.add(a);
					adDNSPackets.add(p);
					System.out.print(" AD DNS ANSWER ");
				}
			}
		} else if (isTCPPacket(p)) {
			TCPPacket pp = (TCPPacket) p;
			if (isUpLink(pp)) {
				if (isAdAddr(pp.dst_ip)) {
					adPackets.add(pp);
					System.out.print(" AD TCP UPLINK");
				}
			} else if (isDownLink(pp)) {
				if (isAdAddr(pp.src_ip)) {
					adPackets.add(pp);
					System.out.print(" AD TCP DOWNLINK");
				}
			}
		} else if (isUDPPacket(p)) {
			UDPPacket pp = (UDPPacket) p;
			if (isUpLink(pp)) {
				if (isAdAddr(pp.dst_ip)) {
					adPackets.add(pp);
					System.out.print(" AD UDP UPLINK");
				}
			} else if (isDownLink(pp)) {
				if (isAdAddr(pp.src_ip)) {
					adPackets.add(pp);
					System.out.print(" AD UDP DOWNLINK");
				}
			}
		}
		System.out.println();
	}

	public boolean isAdDNSRequest(String request) {
		if (adDNSRequests.indexOf(request) != -1)
			return true;
		else
			return false;
	}

	public boolean isAdAddr(InetAddress addr) {
		if (adAddrs.indexOf(addr) != -1)
			return true;
		else
			return false;
	}

	public void inspectAllPacketsTime() {
		System.out.println("STARTING INSPECTION WITH TIME");
		for (Packet p : packets) {
			inspectPacketTime(p);
		}
		sizePw.close();
		timePw.close();
	}
	
	public void inspectAllPacketsTime(String appName) {
		System.out.println("STARTING INSPECTION WITH TIME");
		for (Packet p : packets) {
			inspectPacketTime(p, appName);
		}
		sizePw.close();
		timePw.close();
	}


	public void inspectAllPackets() {
		System.out.println("STARTING INSPECTION");
		for (Packet p : packets) {
			inspectPacket(p);
		}

		System.out.println("STATISTICS FOR ALL");
		System.out.println("size of adPackets " + adPackets.size());
		System.out.println("size of adDNSPackets " + adDNSPackets.size());
		System.out.println("size of adDNSRequests " + adDNSRequests.size());
		System.out.println("size of adAddrs " + adAddrs.size());

		calculateAdSize();
		calculateAllSize();

		System.out.println("ad_size " + ad_size);
		System.out.println("all_size " + all_size);
	}

	public void showResultForSpecificProgram(String appName) {
		Integer appNameIndex = null;
		Iterator iter = appNameMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) iter
					.next();
			Integer index = entry.getKey();
			String name = entry.getValue();
			if (name.equals(appName)) {
				appNameIndex = index;
				break;
			}
		}
		if (appNameIndex == null) {
			System.err.println("Cannot find specified program!");
		}
		int specific_all_size = 0;
		int specific_ad_size = 0;
		for (Packet p : packets) {
			if (getAppNameIndex(p).compareTo(appNameIndex) == 0) {
				specific_all_size += p.len;
			}
		}
		for (Packet p : adPackets) {
			if (getAppNameIndex(p).compareTo(appNameIndex) == 0) {
				specific_ad_size += p.len;
			}
		}
		System.out.println("SPECIFIC STATISTICS FOR " + appName);
		System.out.println("all_size " + specific_all_size);
		System.out.println("ad_size " + specific_ad_size);
	}

	private static int byteToInt2(byte[] b, int from, int size) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < size; i++) {
			n <<= 8;
			temp = b[from + i] & mask;
			n |= temp;
		}
		return n;
	}

	private List<InetAddress> getDNSAddrs(byte[] bytes) {

		List<InetAddress> result = new ArrayList<InetAddress>();
		try {
			int numAns = byteToInt2(bytes, 6, 2);
			int i = 12;
			while ((int) bytes[i] != 0)
				++i;
			i += 5;
			for (int j = 0; j < numAns; ++j) {
				i += 2;

				int type = byteToInt2(bytes, i, 2);
				boolean Atype = (type == 1);
				// goto length
				i += 8;
				int l = byteToInt2(bytes, i, 2);
				i += 2;
				byte addr[] = new byte[l];

				for (int k = 0; k < l; ++k)
					addr[k] = bytes[i + k];

				i += l;
				if (Atype)
					try {
						result.add(InetAddress.getByAddress(addr));
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return result;
		} catch (ArrayIndexOutOfBoundsException aiobe) {
			return result;
		}
	}

	private String getDNSRequest(byte[] bytes) {
		String s = new String();
		int i = 12;
		int l = (int) bytes[i];
		while (l != 0) {
			for (int j = 1; j <= l; ++j) {
				s += (char) bytes[i + j];
			}
			i += l + 1;
			l = (int) bytes[i];
			if (l != 0)
				s += ".";
		}
		return s;
	}

	public int calculateAllSize() {
		all_size = 0;
		for (Packet p : packets) {
			all_size += p.len;
		}
		return all_size;
	}

	public int calculateAdSize() {
		ad_size = 0;
		for (Packet p : adDNSPackets)
			ad_size += p.len;
		for (Packet p : adPackets)
			ad_size += p.len;
		return ad_size;
	}
}
