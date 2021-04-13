package Model.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;

import Model.Bean.Account;

public class AccountDAO {
	public Account findAccount(String query) throws ClassNotFoundException, NumberFormatException, SQLException {
		ConnectDB con = new ConnectDB();
		ResultSet rs = con.execSelect(query);
		while(rs.next()) {
			int id = Integer.parseInt(rs.getString("id"));
			int idFolder = Integer.parseInt(rs.getString("idfolder"));
			String username = rs.getString("username");
			String password = rs.getString("password");
			String name = rs.getString("name");
			Account acc = new Account(id, username, password, name, null, idFolder);
			return acc;
		}
		return null;
	}
}
