package Model.Bean;

import java.io.Serializable;

public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7065249758248316953L;
	int id;
	String username;
	String password;
	String name;
	String ipAddress;
	int idFolder;
	public Account(int id, String username, String password, String name, String ipAddress, int idFolder) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
		this.ipAddress = ipAddress;
		this.idFolder = idFolder;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIdFolder() {
		return idFolder;
	}
	public void setIdFolder(int idFolder) {
		this.idFolder = idFolder;
	}

	
}
