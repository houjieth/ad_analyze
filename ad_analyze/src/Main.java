import java.io.IOException;


public class Main {

	public static void main(String[] args) {
		AdAnalyzer aa = new AdAnalyzer();
		aa.readNameMap(args[1]);
		aa.readCapFile(args[0]);
		aa.printAllPackets();
	}

}
