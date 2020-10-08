package mg.adequa.services;

import mg.adequa.services.dao.DaoFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Transaction {
	private DaoFactory daoFactory;
	
	public Transaction(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	public void begin() {
		try {
			this.daoFactory.getConnection().createStatement().executeUpdate("BEGIN;");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void rollback() {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.daoFactory.getConnection();
			connection.createStatement().executeUpdate("ROLLBACK;");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}
	
	public void commit() {
		Connection connection = null;
		try {
			connection = this.daoFactory.getConnection();
			connection.createStatement().executeUpdate("COMMIT;");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}
	
}
