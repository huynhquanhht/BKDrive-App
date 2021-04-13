package transfer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class network {
	static int n = 5;

	public static void runSystemCommand(String command) {

		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));

			// reading output stream of the command
			for (int i = 0; i < n; ++i) {
				String s = inputStream.readLine();
				if (s != null) {
					System.out.println(s);
				}

			}
			inputStream.close();
			p.destroy();

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public static void ping(InetAddress ip) {
//		System.out.println("ping -n " + ip.toString().substring(1));

		runSystemCommand("ping -n " + n + " " + ip.toString().substring(1));
	}
}
