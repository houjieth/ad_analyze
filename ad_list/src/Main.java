import java.io.IOException;
import java.util.List;


public class Main {
	public static void main(String[] args) throws IOException {
		AdList adList = new AdList();
		String []files = {"/root/programming/AdLibrary/ad_analyze/adaway.txt", "/root/programming/AdLibrary/ad_analyze/adfree.txt"};
		adList.init(files);
		if (adList.vagueMatch("gontijoamaral.hpg.com.br", 5))
		{
			System.out.println("found");
		}
		else
		{
			System.out.println("not found");
		}
		if (adList.match("amazon.com"))
		{
			System.out.println("found");
		}
		else
		{
			System.out.println("not found");
		}
	}
}
