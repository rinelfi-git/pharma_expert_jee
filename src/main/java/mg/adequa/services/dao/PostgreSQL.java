package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.AchatDao;
import mg.adequa.services.dao.interfaces.BilanDao;
import mg.adequa.services.dao.interfaces.DocteurDao;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;
import mg.adequa.services.dao.postgresql.AchatModel;
import mg.adequa.services.dao.postgresql.BilanModel;
import mg.adequa.services.dao.postgresql.DocteurModel;
import mg.adequa.services.dao.postgresql.JournalEntreeSortieModel;

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
	public AchatDao getAchatDao() {
		return new AchatModel(this);
	}
	
	@Override
	public DocteurDao getDocteurDao() {return new DocteurModel(this);}
	
	@Override
	public BilanDao getBilanDao() {return new BilanModel(this);}
	
	@Override
	public JournalEntreeSortieDao getJournalEntreeSortieDao() {return new JournalEntreeSortieModel(this);}
}