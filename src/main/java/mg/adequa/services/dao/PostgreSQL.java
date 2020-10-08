package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.*;
import mg.adequa.services.dao.postgresql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQL implements DaoFactory {
	private String username, password, host, database;
	private Connection connection;
	
	private PostgreSQL(String username, String password, String host, String database) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.database = database;
	}
	
	public static PostgreSQL getInstance() {
		try {
			Class.forName("org.postgresql.Driver");
			return new PostgreSQL("adequa", "adequa", "localhost", "adequa");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		if (this.connection == null || this.connection.isClosed()) this.connection = DriverManager.getConnection("jdbc:postgresql://" + this.host + "/" + this.database, this.username, this.password);
		return this.connection;
	}
	
	@Override
	public DAchat getAchat() {
		return new MAchat(this);
	}
	
	@Override
	public DDocteur getDocteur() {return new MDocteur(this);}
	
	@Override
	public DBilan getBilan() {return new MBilan(this);}
	
	@Override
	public JournalEntreeSortieDao getJournalEntreeSortie() {return new MJournalES(this);}
	
	@Override
	public DPersonne getPersonne() {return new MPersonne(this);}
	
	@Override
	public DSession getSession() {return new MSession(this);}
	
	@Override
	public DLogin getLogin() {return new MLogin(this);}
}
