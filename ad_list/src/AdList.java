import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AdList {
	private List<String> adList = new ArrayList<String>();

	public void init(String[] files) throws IOException {
		for (String file : files) {
			FileInputStream fstream = null;
			fstream = new FileInputStream(file);

			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				line = parse(line);
				if (line == null)
					continue;
				adList.add(line);
			}
		}
	}
	public boolean match(String s)
	{
		return adList.contains(s);
	}
	public boolean vagueMatch(String s, int level)
	{
		for (String line : adList)
		{
			String[] lineSplit = line.split("\\.");
			String[] sSplit    = s.split("\\.");
			int a = 1;
			if (line.length() == 1)
				continue;
			int b = 1;
			for (int i = 0; i < lineSplit.length; i++)
				if (i == lineSplit.length - 2 && (lineSplit[i].equals("com") || lineSplit[i].equals("co")))
					a = 2;
			for (int i = 0; i < sSplit.length; i++)
				if (i == sSplit.length - 2 && (sSplit[i].equals("com") || sSplit[i].equals("co")))
					b = 2;
			boolean match = true;
			for (int i = 1; i <= level + a; ++i)
			{
				if (lineSplit.length - i < 0 || sSplit.length - i < 0)
					break;
				if(!lineSplit[lineSplit.length  - i].equals(sSplit[sSplit.length- i]))
				{
					match = false;
					break;
				}
			}
			if (match)
				return true;
		}
		return false;
	}
	public List<String> getList()
	{
		return adList;
	}
	private String parse(String line)
	{
		if(line.startsWith("#"))
			return null;
		return line.split(" ")[1];
	}
}
