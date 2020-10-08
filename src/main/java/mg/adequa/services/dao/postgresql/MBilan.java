package mg.adequa.services.dao.postgresql;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DBilan;

import java.sql.*;

public class MBilan implements DBilan {
	private DaoFactory daoFactory;
	
	public MBilan(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	@Override
	public int getProduit() {
		String query = "SELECT";
		query += " SUM(produit_en_stock)";
		query += " FROM";
		query += " bilan";
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if(resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public int getCaisse() {
		String query = "SELECT";
		query += " SUM(caisse)";
		query += " FROM";
		query += " bilan";
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if(resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public int getBanque() {
		String query = "SELECT";
		query += " SUM(banque)";
		query += " FROM";
		query += " bilan";
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if(resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean ajouterProduit(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET produit = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant + montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean ajouterCaisse(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET caisse = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant + montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean ajouterBanque(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET banque = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant + montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean enleverProduit(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET produit = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant - montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean enleverCaisse(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET caisse = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant - montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public boolean enleverBanque(int montant) {
		int ancienMontant = this.getProduit();
		boolean output = false;
		String query = "UPDATE";
		query += " bilan";
		query += " SET banque = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, ancienMontant - montant);
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
}
