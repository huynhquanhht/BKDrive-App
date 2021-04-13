package Controller;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Model.Bean.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import transfer.constance;

public class LoginController {
	@FXML
	private TextField txtComputer = new TextField();
	@FXML
	private TextField txtUsername = new TextField();
	@FXML
	private TextField txtPassword = new TextField();
	@FXML
	private Button btnConnect = new Button();
	@FXML
	private Stage primaryStage = new Stage();
	private Socket socketPI = null;

	public LoginController() {
		txtComputer = new TextField();
		txtUsername = new TextField();
		txtPassword = new TextField();
		btnConnect = new Button();
		primaryStage = new Stage();
		socketPI = null;
	}

	public void btnConnectIsClicked() {
		if (txtComputer.getText().isEmpty() || txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Error Login!");
			alert.setContentText("Cannot login. Please enter entire info if you want to connect.");
			alert.showAndWait();
		} else {
			connect();
		}
	}

	public void connect() {
		try {
			socketPI = new Socket(txtComputer.getText(), constance.connectionPort);
			ObjectOutputStream oosPI = new ObjectOutputStream(socketPI.getOutputStream());
			ObjectInputStream oisPI = new ObjectInputStream(socketPI.getInputStream());
			oosPI.writeUTF("Login");
			oosPI.flush();
			String loginInfo = txtUsername.getText() + "|" + txtPassword.getText();
			oosPI.writeUTF(loginInfo);
			oosPI.flush();
			String result = oisPI.readUTF();
			if(result.equals("Success")) {
				Account acc = (Account)oisPI.readObject();
				loadMainInterface(socketPI, oisPI, oosPI, txtComputer.getText(), acc);
			} else if(result.equals("Fail")) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Warning");
				alert.setHeaderText("Error Login!");
				alert.setContentText("Login fail. Username or password is incorrected");
				alert.showAndWait();
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Error Connection!");
			alert.setContentText("Cannot connect to server! Please try again!");
			alert.showAndWait();
		}
	}
	public void loadMainInterface(Socket socketPI, ObjectInputStream oisPI, ObjectOutputStream oosPI,  String txtComputer, Account acc) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/Client.fxml"));
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 855, 623);
			scene.getStylesheets().add(getClass().getResource("/View/Client.css").toExternalForm());
			ClientController cc = new ClientController();
			cc = fxmlLoader.<ClientController>getController();
			cc.setSocket(socketPI , oisPI, oosPI,  txtComputer, acc);
			primaryStage.setScene(scene);
			primaryStage.show();
			Stage stage = (Stage)btnConnect.getScene().getWindow();
			stage.close();
		} catch(Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Error load interface!");
			alert.setContentText("Cannot load client interface! Please try again!");
			alert.showAndWait();
			e.printStackTrace();
		}
	}
}
