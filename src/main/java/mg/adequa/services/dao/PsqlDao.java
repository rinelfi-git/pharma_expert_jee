package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.AchatDao;
import mg.adequa.services.dao.interfaces.BilanDao;
import mg.adequa.services.dao.interfaces.DocteurDao;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;
import mg.adequa.services.dao.models.AchatModel;
import mg.adequa.services.dao.models.BilanModel;
import mg.adequa.services.dao.models.DocteurModel;
import mg.adequa.services.dao.models.JournalEntreeSortieModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PsqlDao implements DaoFactory {
	private String username, password, host, database;
	private Connection connection;
	
	private PsqlDao(String username, String password, String host, String database) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.database = database;
	}
	
	public static PsqlDao getInstance() {
		try {
			Class.forName("org.postgresql.Driver");
			return new PsqlDao("adequa", "adequa", "localhost", "adequa");
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
