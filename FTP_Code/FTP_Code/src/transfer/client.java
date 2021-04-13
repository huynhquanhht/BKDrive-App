package transfer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class client {

	public client(ObjectInputStream oisPI, ObjectOutputStream oosPI, ArrayList<String> path, ArrayList<String> dir) {
	
		
		
		new Thread(new Runnable() {

			@Override
			public  void run() {
			
				int dataPort = -1;

				dataPort = transfer.receivePort(oisPI, oosPI );
				while (dataPort == -1) {
					dataPort = transfer.receivePort(oisPI, oosPI);
				}
				System.out.println("Nhan dataport: " + dataPort);
			
				try {

					for (int i = 0; i < path.size(); ++i) {
						transfer.sendCmd(oosPI, constance.RQ_SEND_FILE);
						System.out.println("C_RQ: "+constance.RQ_SEND_FILE);
						String rp = transfer.getCmd(oisPI);
						System.out.println("C_RP: "+rp);
						if (rp.equals(constance.RP_ACCEPT_SEND_FILE)) {
//							System.out.println("RPONSE: "+rp);
							Socket dataSocket = new Socket(constance.HOST, dataPort);
							String file = path.get(i);
							
							transfer.sendFile(dataSocket, file, dir.get(i));
							dataSocket.close();
						}
					}
					transfer.sendCmd(oosPI, constance.RQ_FINISH_SEND_FILE);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Khong the ket noi");
				}

			}
		}).start();

	}
}
