package Controller;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import Model.Bean.Account;
import Model.Bean.FileClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import transfer.client;
import transfer.constance;
import transfer.loggerclient;
import transfer.transfer;

public class UploadController {
	@FXML
	TextField txtFileName;
	@FXML
	TextField txtUrl;
	@FXML
	TextField txtSize;
	@FXML
	Button btnChooseFile;
	@FXML
	Button btnChooseFolder;
	@FXML
	Button btnUpload;
	@FXML
	Button btnCancel;
	@FXML
	TableView<FileClass> tvUpload;
	@FXML
	TableColumn<FileClass, String> tcName;
	@FXML
	TableColumn<FileClass, String> tcDate;
	@FXML
	TableColumn<FileClass, String> tcSize;
	@FXML
	TableColumn<FileClass, String> tcPath;
	Socket socketPI;
	List<File> files;
	private Account acc;
	private ObjectOutputStream oosPI = null;
	private ObjectInputStream oisPI = null;

	public UploadController() {
		txtFileName = new TextField();
		txtUrl = new TextField();
		txtSize = new TextField();
		btnChooseFile = new Button();
		btnChooseFolder = new Button();
		btnUpload = new Button();
		btnCancel = new Button();
		tvUpload = new TableView<FileClass>();
		tcName = new TableColumn<FileClass, String>();
		tcDate = new TableColumn<FileClass, String>();
		tcSize = new TableColumn<FileClass, String>();
		tcPath = new TableColumn<FileClass, String>();
		files = new ArrayList<File>();

	}

	public void setInfo(Socket socket, Account acc, ObjectInputStream oisPI, ObjectOutputStream oosPI) {
		this.socketPI = socket;
		this.acc = acc;
		this.oosPI = oosPI;
		this.oisPI = oisPI;
	}

	public void btnChooseFileIsClicked() {
		System.out.println("Choose File");
		FileChooser fileChooser = new FileChooser();
		Stage primaryStage = new Stage();
		files = fileChooser.showOpenMultipleDialog(primaryStage);

		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcDate.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
		tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		tcPath.setCellValueFactory(new PropertyValueFactory<>("path"));
		ObservableList<FileClass> fileList = FXCollections.observableArrayList();
		for (File f : files) {
			long lastModified = f.lastModified();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date date = new Date(lastModified);
			String dateModified = sdf.format(date).toString();
			FileClass fc = new FileClass(null, f.getName(), dateModified, getFileSizeMegaBytes(f), null, f.getPath(),
					null);
			if (f.length() / (1024 * 1024) == 0) {
				if (f.length() / 1024 == 0) {
					fc.setSize(getFileSizeBytes(f));
				} else {
					fc.setSize(getFileSizeKiloBytes(f));
				}
			}
			fileList.add(fc);
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

	public void btnUploadIsClicked() throws IOException {
		if (tvUpload.getItems().size() == 0) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Error Upload!");
			alert.setContentText("Please choose files which you want to upload!");
			alert.showAndWait();
		} else {
			ObservableList<FileClass> fileList = FXCollections.observableArrayList();
			fileList = tvUpload.getItems();
			ArrayList<String> sourceFilePath_full = new ArrayList<String>();
			ArrayList<String> sourceFilePath_half = new ArrayList<String>();

			File f = new File(fileList.get(0).getPath());
			if (f.isDirectory()) {
				String half = f.getName();
				String full = f.getAbsolutePath();
				sourceFilePath_full = transfer.getFullSourceFilePath(full);
				sourceFilePath_half = transfer.getHalfSourceFilePath(sourceFilePath_full, full, half, this.acc.getId());
				for (int i = 0; i < sourceFilePath_half.size(); ++i) {
					sourceFilePath_half.set(i, constance.hostFolder + sourceFilePath_half.get(i));
				}

			} else if (f.isFile()) {

				for (FileClass fc : fileList) {
					File f1 = new File(fc.getPath());
					if (f1.isFile()) {
						sourceFilePath_full.add(f1.getAbsolutePath());
						sourceFilePath_half
								.add(constance.hostFolder + File.separator + this.acc.getId() + File.separator);
					}
				}
			}
			LocalDateTime startTime = LocalDateTime.now();
			loggerclient.log(Level.INFO, constance.L_START_TIME + startTime + "");
			new client(oisPI, oosPI, sourceFilePath_full, sourceFilePath_half);
			LocalDateTime endTime = LocalDateTime.now();
			System.out.println(endTime);
			loggerclient.log(Level.INFO, constance.L_END_TIME + startTime + "");
			System.out.println("Ket thuc gui: " + endTime);
		}
	}

	public void btnChooseFolderIsClicked() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		Stage primaryStage = new Stage();
		File dir = directoryChooser.showDialog(primaryStage);
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcDate.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
		tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
		tcPath.setCellValueFactory(new PropertyValueFactory<>("path"));
		ObservableList<FileClass> fileList = FXCollections.observableArrayList();

		long lastModified = dir.lastModified();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date(lastModified);
		String dateModified = sdf.format(date).toString();
		FileClass fc = new FileClass(null, dir.getName(), dateModified, getFileSizeMegaBytes(dir), null, dir.getPath(),
				null);
		if (dir.length() / (1024 * 1024) == 0) {
			if (dir.length() / 1024 == 0) {
				fc.setSize(getFileSizeBytes(dir));
			} else {
				fc.setSize(getFileSizeKiloBytes(dir));
			}
		}
		fileList.add(fc);
		tvUpload.setItems(fileList);
	}

}