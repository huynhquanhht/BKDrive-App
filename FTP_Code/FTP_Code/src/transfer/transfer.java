package transfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class transfer {

	public static String getCmd(ObjectInputStream connectionDataInputStream) {
		try {
			String rs = null;
			while (rs == null) {
				rs = connectionDataInputStream.readUTF();
			}
			return rs;
		} catch (IOException e) {
			System.err.println("Loi get cmd");
		}
		return null;
	}

	public static void sendCmd(ObjectOutputStream connectionDataOutputStream, String cmd) {
		try {
			connectionDataOutputStream.writeUTF(cmd);
			connectionDataOutputStream.flush();
		} catch (IOException e) {
			System.err.println("Loi send cmd: " + cmd);
		}
	}

	public static int getFreePort() {
		int freePort = -1;
		Random rand = new Random();
		do {
			freePort = rand.nextInt(65356);
		} while (isFreePort(freePort) == false || freePort == constance.connectionPort);
		return freePort;
	}

	private static boolean isFreePort(int port) {
		try {
			new ServerSocket(port).close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static int receivePort(ObjectInputStream dis, ObjectOutputStream dos) {

		String cmd = constance.PASV;
		try {

			dos.writeUTF(cmd);
			dos.flush();

			String s = dis.readUTF();

			int port = Integer.parseInt(s);

			return port;

		} catch (IOException e) {
			System.err.println("Loi khi truyen lenh lay port");
//			e.printStackTrace();
		}

		return -1;
	}

	public static void saveFile(Socket clientSock, String root) {

		try {
			InputStream inputStream = clientSock.getInputStream();

			ObjectInputStream inStream = new ObjectInputStream(inputStream);
			fileInfo finf = (fileInfo) inStream.readObject();
			long filesize = finf.getFilesize();
			String dir = finf.getDir();

			int read = 0;
			// long totalRead = 0;
			long remaining = filesize;

			DataInputStream dis = new DataInputStream(inputStream);

			Files.createDirectories(Paths.get(dir));
			FileOutputStream fos = new FileOutputStream(root + dir + File.separator + finf.getFilename());
			byte[] buffer = new byte[4096];
			network.ping(clientSock.getInetAddress());
			while ((read = dis.read(buffer, 0, Math.max(4096, Math.min(buffer.length, (int) remaining)))) > 0) {
				// totalRead += read;
				
				remaining -= read;
//				System.out.println("read: " + read + " total read: " + totalRead + " bytes. "
//						+ Math.max(4096, Math.min(buffer.length, (int) remaining)));
				fos.write(buffer, 0, read);

			}
			System.out.println("XONG");
			fos.close();
			dis.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}

//		System.out.println("DONE");

	}

	public static void sendFile(Socket dataSocket, String file, String dir) {

		try {
			OutputStream outputStream = dataSocket.getOutputStream();
			DataOutputStream dos = new DataOutputStream(outputStream);
			FileInputStream fis = new FileInputStream(file);

			// get fileinfo

			File f = new File(file);
			fileInfo finf = new fileInfo(f.getName(), f.length(), dir);
			// System.out.println(finf.getFilename() + ": " + finf.getFilesize() + "
			// bytes");

			// send fileinfo
			ObjectOutputStream outStream = new ObjectOutputStream(dataSocket.getOutputStream());
			outStream.writeObject(finf);

			//

			byte[] buffer = new byte[4096];
			network.ping(dataSocket.getInetAddress());
			while (fis.read(buffer) >= 0) {
			

				dos.write(buffer);
				dos.flush();
			}
			System.out.println("XONG2");
			fis.close();
//
			dos.flush();
//			dos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<String> getFullSourceFilePath(String fullpath) {
		try (Stream<Path> walk = Files.walk(Paths.get(fullpath))) {
			ArrayList<String> sourceFilePath_full = new ArrayList<String>();
			sourceFilePath_full = (ArrayList<String>) walk.filter(Files::isRegularFile).map(x -> x.toString())
					.collect(Collectors.toList());

			return sourceFilePath_full;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static ArrayList<String> getHalfSourceFilePath(ArrayList<String> sourceFilePath_full, String fullpath,
			String halfpath, int id) {
		ArrayList<String> sourceFilePath_half = new ArrayList<String>();
		for (String s : sourceFilePath_full) {

			sourceFilePath_half.add(id + File.separator
					+ s.substring(fullpath.length() - halfpath.length(), s.lastIndexOf("\\", s.length())));
		}
		return sourceFilePath_half;

	}
}
