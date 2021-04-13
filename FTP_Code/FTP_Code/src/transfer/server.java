package transfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class server {

	private Socket connectionSocket;

	public server(ServerSocket ss) {
		try {
			while (true) {
				connectionSocket = ss.accept();

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							ObjectInputStream dis = new ObjectInputStream(connectionSocket.getInputStream());
							ObjectOutputStream dos = new ObjectOutputStream(connectionSocket.getOutputStream());
							String cmd = dis.readUTF();
							int freePort = -1;
							if (cmd.equals(constance.PASV)) {
								freePort = transfer.getFreePort();
								dos.writeUTF(freePort + "");
								dos.flush();
								loggerserver.log(Level.INFO, "SEND_FREE_PORT: " + freePort);
							}
							ServerSocket dataSocket = new ServerSocket(freePort);
							while (true) {
								String rq = transfer.getCmd(dis);
//								System.out.println("S_RQ: "+rq);
								if (rq.equals(constance.RQ_SEND_FILE)) {
									transfer.sendCmd(dos, constance.RP_ACCEPT_SEND_FILE);
									Socket clientSock = dataSocket.accept();
									transfer.saveFile(clientSock, "");
								} else if (rq.equals(constance.RQ_FINISH_SEND_FILE)) {
									loggerserver.log(Level.INFO, constance.L_FINISH_SEND_FILE);
//									System.out.println("Send xong");
									break;
								}
							}
							dataSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
							loggerserver.log(Level.WARNING, constance.L_THREAD);
						}
					}
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			loggerserver.log(Level.WARNING, constance.L_SERVER_ACCEPT_CONNECTION);
		}
	}

}
