package Controller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JFileChooser;

import Model.Bean.Account;
import Model.Bean.FileClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import transfer.client;
import transfer.constance;
import transfer.loggerclient;
import transfer.loggerserver;
import transfer.transfer;

public class ClientController {
	@FXML
	private Button btnUpload;
	@FXML
	private TableView<FileClass> tvClient;
	@FXML
	private TableColumn<FileClass, ImageView> tcImage;
	@FXML
	private TableColumn<FileClass, String> tcName;
	@FXML
	private TableColumn<FileClass, String> tcDate;
	@FXML
	private TableColumn<FileClass, String> tcSize;
	@FXML
	private TableColumn<FileClass, String> tcType;
	@FXML
	private Button btnRefresh;
	@FXML
	private Button btnBack;
	@FXML
	private TextArea txtServer;
	@FXML
	private Label lblPath;
	Socket socketPI = null;
	Socket socketDTP = null;
	ObjectOutputStream oosPI;
	ObjectInputStream oisPI;

	ObjectOutputStream oosDTP;
	ObjectInputStream oisDTP;
	Account acc;

	public ClientController() {
		btnRefresh = new Button();
		tvClient = new TableView<FileClass>();
		tvClient.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tcImage = new TableColumn<FileClass, ImageView>();
		tcName = new TableColumn<FileClass, String>();
		tcDate = new TableColumn<FileClass, String>();
		tcSize = new TableColumn<FileClass, String>();
		tcType = new TableColumn<FileClass, String>();
		lblPath = new Label();
		txtServer = new TextArea();
		btnBack = new Button();
	}

	public void setSocket(Socket socketPI, ObjectInputStream oisPI, ObjectOutputStream oosPI, String ipAddress,
			Account acc) throws IOException, ClassNotFoundException {
		this.socketPI = socketPI;
		this.oisPI = oisPI;
		this.oosPI = oosPI;
		this.acc = acc;
		setTableView();
	}

	// When is form client start, btnRefresh is clicked
	public void setTableView() throws IOException, ClassNotFoundException {
		// @SuppressWarnings("unchecked")
		String path = oisPI.readUTF();
		@SuppressWarnings("unchecked")
		Vector<FileClass> vFile = (Vector<FileClass>) oisPI.readObject();
		tcImage.setCellValueFactory(new PropertyValueFactory<>("img"));
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcDate.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
		tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
		ObservableList<FileClass> fileList = FXCollections.observableArrayList();
		for (FileClass f : vFile) {
			FileClass fc = new FileClass();
			fc = setInfo(f);
			fileList.add(fc);
		}
		tvClient.setItems(fileList);
		lblPath.setText(path);
	}

	public String processPath(String path) {
		ArrayList<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '\\') {
				index.add(i);
			}
		}
		return path.substring(0, index.get(index.size() - 1));
	}

	public void clickRowTableView(MouseEvent e) throws IOException, ClassNotFoundException {
		FileClass fc = tvClient.getSelectionModel().getSelectedItem();
		if (e.getClickCount() == 2 && fc != null) {
			try {
				if (fc.getType().equals("Folder")) {
					String path = fc.getPath();
					oosPI.writeUTF("View in folder");
					oosPI.flush();
					oosPI.writeUTF(path);
					oosPI.flush();
					setTableView();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ec) {
				ec.printStackTrace();
			}
		}
	}

	public void refresh() {
		try {
			String path = lblPath.getText();
			oosPI.writeUTF("Refresh");
			oosPI.flush();
			oosPI.writeUTF(path);
			oosPI.flush();
			setTableView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileClass setInfo(FileClass f) {
		FileClass fc;
		ImageView img = new ImageView();
		img.setFitHeight(30);
		img.setFitWidth(30);

		if (f.getType().equals("txt")) {
			f.setType("Text Document");
			img.setImage(new Image("/Img/txt.png"));
		} else if (f.getType().equals("rar")) {
			f.setType("WinRAR archieve");
			img.setImage(new Image("/Img/rar.png"));
		} else if (f.getType().equals("zip")) {
			f.setType("WinZIP archieve");
			img.setImage(new Image("/Img/rar.png"));
		} else if (f.getType().equals("xlsx")) {
			f.setType("Excel Document");
			img.setImage(new Image("/Img/excel.png"));
		} else if (f.getType().equals("ppt")) {
			f.setType("Powerpoint Document");
			img.setImage(new Image("/Img/ppt.png"));
		} else if (f.getType().equals("docx")) {
			f.setType("Word Document");
			img.setImage(new Image("/Img/word.png"));
		} else if (f.getType().equals("pdf")) {
			f.setType("PDF Document");
			img.setImage(new Image("/Img/pdf.png"));
		} else if (f.getType().equals("Folder")) {
			f.setType("Folder");
			img.setImage(new Image("/Img/folder.png"));
		} else {
			f.setType("");
			img.setImage(new Image("/Img/files_white.png"));
		}
		fc = new FileClass(img, f.getName(), f.getLastModified(), f.getSize(), f.getType(), f.getPath(), null);
		return fc;
	}

	public void back() {
		try {
			String path = lblPath.getText();
			ArrayList<Integer> index = new ArrayList<Integer>();
			for (int i = 0; i < path.length(); i++) {
				if (path.charAt(i) == '\\') {
					index.add(i);
				}
			}
			if (index.size() > 1) {
				String pathProcess = processPath(path);
				oosPI.writeUTF("Back");
				oosPI.flush();

				oosPI.writeUTF(pathProcess);
				oosPI.flush();

				lblPath.setText(pathProcess);
				setTableView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() throws IOException {
		FileClass fc = tvClient.getSelectionModel().getSelectedItem();
		if (fc != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete File");
			alert.setHeaderText("Are you sure want to move this file to the Recycle Bin?");
			alert.setContentText(fc.getPath());

			// option != null.
			Optional<ButtonType> option = alert.showAndWait();

			if (option.get() == ButtonType.OK) {
				oosPI.writeUTF("delete");
				oosPI.flush();
				oosPI.writeUTF(fc.getPath());
				oosPI.flush();
				try {
					setTableView();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (option.get() == ButtonType.CANCEL) {
				alert.close();
			}
		}

	}

	public void download() {
		FileClass fc = tvClient.getSelectionModel().getSelectedItem();
		if (fc != null) {
			try {
				String path = fc.getPath();
				System.out.println("Download: " + path);
				oosPI.writeUTF(constance.RQ_DOWNLOAD); 
				oosPI.flush();

				String cmd = oisPI.readUTF();
				if (cmd.equals(constance.RP_ACCEPT_DOWNLOAD)) {
					oosPI.writeObject(path.toString()); 				
					oosPI.flush(); 

					// root to download
					String root = System.getProperty("user.dir");
					root = root.substring(0, root.indexOf("\\")) + File.separator;
					oosPI.writeObject(root);
					oosPI.flush();

					int freePort = Integer.parseInt(oisPI.readUTF());
					loggerclient.log(Level.INFO, constance.L_CLIENT_RECEIVE_FREE_PORT + " " + freePort);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Socket reciever = new Socket(constance.HOST, freePort);
								ObjectOutputStream oos = new ObjectOutputStream(reciever.getOutputStream());
								ObjectInputStream ois = new ObjectInputStream(reciever.getInputStream());

								boolean running = true;
								String root = System.getProperty("user.dir");

								root = root.substring(0, root.indexOf("\\")) + File.separator;
								while (running) {
									String cmd2 = ois.readUTF();
									if (cmd2.equals(constance.RQ_SEND_FILE)) {
										oos.writeUTF(constance.RP_ACCEPT_SEND_FILE);
										oos.flush();
										loggerserver.log(Level.INFO, "DOWNLOAD: " + path + " STORE: " + root + "\0");
										loggerserver.log(Level.INFO, "START: " + LocalDateTime.now());
										root = "";
										transfer.saveFile(reciever, root);
										reciever.close();
									} else if (cmd2.equals(constance.RP_FINISH_DOWNLOAD)) {
										running = false;
										loggerserver.log(Level.INFO, "END: " + LocalDateTime.now());
										System.out.println(constance.RP_FINISH_DOWNLOAD);
									}
								}

							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}).start();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	public void loadUploadInterface() {
		try {
			System.out.println();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/Upload.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 652, 510);
			scene.getStylesheets().add(getClass().getResource("/View/Upload.css").toExternalForm());
			UploadController uc = new UploadController();
			uc = fxmlLoader.<UploadController>getController();

			uc.setInfo(socketPI, acc, this.oisPI, this.oosPI);
			Stage primaryStage = new Stage();
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
