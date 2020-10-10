package mg.adequa.services;

import mg.adequa.services.dao.DaoFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Transaction {
	private DaoFactory daoFactory;
	
	public Transaction(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	public void begin() throws SQLException {
		Connection connection = this.daoFactory.getConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate("BEGIN;");
		if (statement != null) statement.close();
		if (connection != null) connection.close();
	}
	
	public void rollback() throws SQLException {
		Connection connection = this.daoFactory.getConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate("ROLLBACK;");
		if (statement != null) statement.close();
		if (connection != null) connection.close();
	}
	
	public void commit() throws SQLException {
		Connection connection = this.daoFactory.getConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate("COMMIT;");
		if (statement != null) statement.close();
		if (connection != null) connection.close();
	}
	
}
