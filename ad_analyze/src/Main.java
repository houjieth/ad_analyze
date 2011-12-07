import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		AdAnalyzer aa = new AdAnalyzer();
		aa.init(args);
		//System.setOut(new PrintStream(new File("/Users/jiehou/Desktop/output")));
		//aa.printAllPackets();
		//aa.inspectAllPackets();
		aa.inspectAllPacketsTime("com.rovio.angrybirdsseasons");
		//aa.showResultForSpecificProgram("com.rovio.angrybirdsseasons");
	}
}