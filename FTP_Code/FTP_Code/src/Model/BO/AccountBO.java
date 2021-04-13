package Model.BO;

import java.sql.SQLException;

import Model.Bean.Account;
import Model.DAO.AccountDAO;

public class AccountBO {
	private AccountDAO accountDAO = new AccountDAO();
	public Account findAccount(String query) throws NumberFormatException, ClassNotFoundException, SQLException {
		return accountDAO.findAccount(query);
	}
}
