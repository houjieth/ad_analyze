import java.io.IOException;


public class Main {

	public static void main(String[] args) {
		AdAnalyzer aa = new AdAnalyzer();
		aa.init(args);
		aa.printAllPackets();
		aa.inspectAllPackets();
		System.out.println("size of adPackets " + aa.adPackets.size());
		System.out.println("size of adDNSPackets " + aa.adDNSPackets.size());
		System.out.println("size of adDNSRequests " + aa.adDNSRequests.size());
		System.out.println("size of adAddrs " + aa.adAddrs.size());
		System.out.println("all_size" + aa.all_size);
	}
}
