package Model.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {
	Connection con;
	public ConnectDB() throws ClassNotFoundException {
		connect();
	}
	public void connect() throws ClassNotFoundException {
		 try {
			 // localhost or 127.0.0.1
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pbl4?serverTimezone=UTC","root","");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String execUpdate(String query){
		try {
			Statement st = con.createStatement();
			st.executeUpdate(query); 
			return "Success";
		} catch(SQLException ex) {
			return "fail";
		}
	}
	public ResultSet execSelect(String query) {
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);

			return rs;
		} catch(SQLException ex) {
			return null;
		}
	}
	public String close() {
		try {
			con.close();
			return "Database is closed.\n";
		} catch (SQLException ex) {
			return "Cannot close database. Please check again.\n";
		}
	}
}
