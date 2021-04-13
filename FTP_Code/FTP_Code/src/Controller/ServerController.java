package Controller;

import java.io.File;
import Model.Bean.FileClass;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

import Model.BO.AccountBO;
import Model.Bean.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import transfer.constance;
import transfer.loggerserver;
import transfer.transfer;

public class ServerController {
	@FXML
	private TextArea txtServer;
	@FXML
	private Button btnOpen;
	@FXML
	private TableView<Account> tvServer;
	@FXML
	private TableColumn<Account, Integer> tcId;
	@FXML
	private TableColumn<Account, String> tcName;
	@FXML
	private TableColumn<Account, String> tcIp;
	@FXML
	private Label lblIP;

	private ServerSocket serverPI = null;

	ObservableList<Account> accList;

	public ServerController() {
		txtServer = new TextArea();
		lblIP = new Label();
		btnOpen = new Button();
		tvServer = new TableView<Account>();
		tcId = new TableColumn<Account, Integer>();
		tcIp = new TableColumn<Account, String>();
		tcName = new TableColumn<Account, String>();

		accList = FXCollections.observableArrayList();

	}

	public void btnOpenIsClicked() throws IOException {
		serverPI = new ServerSocket(constance.connectionPort);
		txtServer.appendText(">> Server is open with port " + constance.connectionPort + "!\n");

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						System.out.println("Connect1");
						Socket socketPI = serverPI.accept();

						System.out.println("Connect2");
						txtServer.appendText(">> Initial ois, oos successfully!\n");
						// client
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									ObjectInputStream oisPI = new ObjectInputStream(socketPI.getInputStream());
									ObjectOutputStream oosPI = new ObjectOutputStream(socketPI.getOutputStream());
									ServerSocket dataSocket = null;

									boolean running = true;
									int freePort = -1;
									while (running) {

										String cmd = oisPI.readUTF();
										System.out.println("CMD: " + cmd);
										Account acc;
										String path = "";
										switch (cmd) {
										case constance.LOGIN:
											acc = login(socketPI, oisPI, oosPI);
											path = constance.hostFolder + acc.getIdFolder();
											setTableViewForClient(oosPI, path);
											break;
										case constance.REFRESH:
											path = oisPI.readUTF();
											setTableViewForClient(oosPI, path);
											break;

										case constance.DELETE:
											path = oisPI.readUTF();
											File ff = new File(path);
											System.out.println("delete: " + path);
										
											deleteFile(ff);

											setTableViewForClient(oosPI, path.substring(0, path.lastIndexOf("\\")));
											break;
										case constance.VIEW_IN_FOLDER:
											path = oisPI.readUTF();
											File file = new File(path);
											if (file.isDirectory()) {
												setTableViewForClient(oosPI, path);
											}
											break;
										case constance.BACK:
											path = oisPI.readUTF();
											setTableViewForClient(oosPI, path);
											break;
										case constance.PASV:
											freePort = transfer.getFreePort();
											oosPI.writeUTF(freePort + "");
											oosPI.flush();
											System.out.println("Gui free port: " + freePort);
											dataSocket = new ServerSocket(freePort);
											break;
										case constance.RQ_SEND_FILE:
											transfer.sendCmd(oosPI, constance.RP_ACCEPT_SEND_FILE);
											Socket clientSock = dataSocket.accept();
											transfer.saveFile(clientSock, "");
											break;
										case constance.RQ_FINISH_SEND_FILE:
											break;

										case constance.RQ_DOWNLOAD:

											oosPI.writeUTF(constance.RP_ACCEPT_DOWNLOAD);
											oosPI.flush();
											String full = (String) oisPI.readObject();
											String root = (String) oisPI.readObject();
											System.out.println("file can down: " + full);
											System.out.println("ROOT: " + root);
											int freePortDownload = transfer.getFreePort();
											System.out.println("S_Freeport_Download: " + freePortDownload);
											oosPI.writeUTF(freePortDownload + "");
											oosPI.flush(); 

											ArrayList<String> sourceFilePath_full = new ArrayList<String>();
											ArrayList<String> sourceFilePath_half = new ArrayList<String>();
											File f = new File(full);
											String half = root + "0\\" + full.substring(full.lastIndexOf("\\") + 1);
											if (f.isDirectory()) {
												System.out.println("full: " + full);
												System.out.println("half: " + half);
												sourceFilePath_full = transfer.getFullSourceFilePath(full);
												sourceFilePath_half = transfer
														.getHalfSourceFilePath(sourceFilePath_full, full, half, 0);
											} else if (f.isFile()) {
												File sourceFile[] = new File[] { f };
												for (File file1 : sourceFile) {
													String absolutePath = file1.getAbsolutePath();
													sourceFilePath_full.add(absolutePath);
													sourceFilePath_half.add(root + 0 + File.separator);
												}
											}

											new Thread(new Runnable() {

												@Override
												public void run() {
													try {
														ArrayList<String> sourceFilePath_full = new ArrayList<String>();
														ArrayList<String> sourceFilePath_half = new ArrayList<String>();
														File f = new File(full);
														String half = full.substring(full.lastIndexOf("\\") + 1);
														if (f.isDirectory()) {
															System.out.println("full: " + full);
															System.out.println("half: " + half);
															sourceFilePath_full = transfer.getFullSourceFilePath(full);

															sourceFilePath_half = transfer.getHalfSourceFilePath(
																	sourceFilePath_full, full, half, 0);
															for (int i = 0; i < sourceFilePath_half.size(); ++i) {
																sourceFilePath_half.set(i, root + File.separator
																		+ sourceFilePath_half.get(i));
															}
														} else if (f.isFile()) {
															File sourceFile[] = new File[] { f };
															for (File file1 : sourceFile) {
																String absolutePath = file1.getAbsolutePath();
																sourceFilePath_full.add(absolutePath);
																sourceFilePath_half.add(root + 0 + File.separator);
															}
														}

														ServerSocket ss = new ServerSocket(freePortDownload);
														System.out.println("ss: " + ss.isClosed());
														Socket cs = ss.accept();
														System.out.println("Da co ket noi den " + cs.getLocalPort()
																+ " " + cs.getInetAddress());
														ObjectOutputStream oos = new ObjectOutputStream(
																cs.getOutputStream());
														ObjectInputStream ois = new ObjectInputStream(
																cs.getInputStream());

														for (int i = 0; i < sourceFilePath_full.size(); ++i) {
															System.out.println(sourceFilePath_full.get(i) + " --> "
																	+ sourceFilePath_half.get(i));
															oos.writeUTF(constance.RQ_SEND_FILE);
															oos.flush();
															System.out.println("SS_RQ: " + constance.RQ_SEND_FILE);
															String rp = ois.readUTF();
															System.out.println("SSS_RP: " + rp);
															if (rp.equals(constance.RP_ACCEPT_SEND_FILE)) {
																System.out.println("RPONSE_DOWNLOAD: " + rp);
																String fullpath = sourceFilePath_full.get(i);
																String halfpath = sourceFilePath_half.get(i);
																loggerserver.log(Level.INFO,
																		constance.L_TYPE_DOWNLOAD
																				+ sourceFilePath_full.get(i)
																				+ constance.L_TYPE_STORE
																				+ sourceFilePath_half.get(i));

																loggerserver.log(Level.INFO,
																		constance.L_START_TIME + LocalDateTime.now());											
																transfer.sendFile(cs, fullpath, halfpath);
															}
														}
														oos.writeUTF(constance.RP_FINISH_DOWNLOAD);
														oos.flush();
														loggerserver.log(Level.INFO,
																constance.L_END_TIME + LocalDateTime.now());
														ss.close();
													} catch (IOException e) {
														e.printStackTrace();
														loggerserver.log(Level.WARNING, constance.L_ERR_SOCKET);
													}
												}
											}).start();

										}

									}
								} catch (Exception e) {
									e.printStackTrace();
									loggerserver.log(Level.WARNING, constance.L_ERR_DOWNLOAD);
								}
							}
						}).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void deleteFile(File file) {
		if (file.list() != null)
			for (File f : file.listFiles()) {

				if (f.isDirectory()) {
					deleteFile(f);
				} else {

					f.delete();
				}
			}
		file.delete();
	}

	public Account login(Socket socketPI, ObjectInputStream oisPI, ObjectOutputStream oosPI)
			throws NumberFormatException, ClassNotFoundException, SQLException, IOException {
		String loginInfo = oisPI.readUTF();
		int pos = loginInfo.indexOf('|');
		String username = loginInfo.substring(0, pos);
		String password = loginInfo.substring(pos + 1, loginInfo.length());

		String query = "select * from account where username='" + username + "' and password='" + password + "'";
		AccountBO accountBo = new AccountBO();
		Account acc;
		acc = accountBo.findAccount(query);
		if (acc == null) {
			oosPI.writeUTF("Fail");
			oosPI.flush();
			return null;
		}
		oosPI.writeUTF("Success");
		oosPI.flush();
		oosPI.writeObject(acc);
		oosPI.flush();
		return acc;
	}

	public void setTableViewForClient(ObjectOutputStream oosPI, String path) throws IOException {

		try {
			System.out.println("Path: " + path);
			Vector<FileClass> vFile = new Vector<FileClass>();
			File file = new File(path);
			System.out.println(path);
			File[] files = file.listFiles();
			if (files.length > 0) {
				for (File f : files) {
					String fileName = f.getName();
					int index = fileName.lastIndexOf('.');
					String extension = fileName.substring(index + 1);
					long lastModified = f.lastModified();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date = new Date(lastModified);
					String dateModified = sdf.format(date).toString();
					FileClass fi = new FileClass(null, f.getName(), dateModified, getFileSizeMegaBytes(f), extension,
							f.getAbsolutePath(), null);
					if (f.length() / (1024 * 1024) == 0) {
						if (f.length() / 1024 == 0) {
							fi.setSize(getFileSizeBytes(f));
						} else {
							fi.setSize(getFileSizeKiloBytes(f));
						}
					}
					if (!f.getName().contains(".")) {
						if (f.isDirectory() == true) {
							fi.setType("Folder");
						} else {
							fi.setType("File");
						}
					}
					vFile.add(fi);
				}
			}
			oosPI.writeUTF(path);

			oosPI.flush();

			oosPI.writeObject(vFile);
			oosPI.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private String getFileSizeMegaBytes(File file) {
		return (double) Math.round((file.length() / (1024 * 1024)) * 10) + " MB";
	}

	private String getFileSizeKiloBytes(File file) {
		return (double) Math.round((file.length() / 1024) * 10) + " KB";
	}

	private String getFileSizeBytes(File file) {
		return file.length() + " Bytes";
	}

	public void upload(ObjectInputStream ois) throws ClassNotFoundException, IOException {

	}

}
