package mg.adequa.services.dao.postgresql;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;
import mg.adequa.tableviews.JournalEntreeSortieTV;
import mg.adequa.utils.DatatableParameter;

import java.sql.*;
import java.util.ArrayList;

public class MJournalES implements JournalEntreeSortieDao {
	private DaoFactory daoFactory;
	
	public MJournalES(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	@Override
	public String makeEntreeQuery(DatatableParameter constraints) {
		String[] colonne = new String[]{"id", "libelle_operation", "piece_justificative", "somme", "observation"};
		String query = "SELECT " +
			               "id," +
			               "libelle_operation," +
			               "piece_justificative," +
			               "somme," +
			               "observation," +
			               "to_char(date_operation, 'DD Month YYYY') AS date_operation," +
			               " from journal_entree_sortie";
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query += " WHERE date_operation::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR libelle_operation ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR piece_justificative ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR observation ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " AND sortie = false AND entree = true";
		} else query += " WHERE sortie = false AND entree = true";
		if (constraints.getOrderColumn() != -1) query += " ORDER BY " + colonne[constraints.getOrderColumn()] + " " + constraints.getOrderDirection();
		else query += " ORDER BY id DESC";
		return query.trim();
	}
	
	@Override
	public String makeSortieQuery(DatatableParameter constraints) {
		String[] colonne = new String[]{"id", "libelle_operation", "piece_justificative", "somme", "observation"};
		String query = "SELECT " +
			               "id," +
			               "libelle_operation," +
			               "piece_justificative," +
			               "somme," +
			               "observation," +
			               "to_char(date_operation, 'DD Month YYYY') AS date_operation," +
			               " from journal_entree_sortie";
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query += " WHERE date_operation::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR libelle_operation ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR piece_justificative ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR observation ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " AND sortie = true AND entree = false";
		} else query += " WHERE sortie = true AND entree = false";
		if (constraints.getOrderColumn() != -1) query += " ORDER BY " + colonne[constraints.getOrderColumn()] + " " + constraints.getOrderDirection();
		else query += " ORDER BY id DESC";
		return query.trim();
	}
	
	@Override
	public ArrayList<JournalEntreeSortieTV> makeDatatable(String query, DatatableParameter constraints) {
		ArrayList<JournalEntreeSortieTV> journals = new ArrayList<>();
		if (constraints.getLimitLength() != -1) query += " LIMIT " + constraints.getLimitLength() + " OFFSET " + constraints.getLimitStart();
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				JournalEntreeSortieTV journal = new JournalEntreeSortieTV();
				journal.setId(resultSet.getInt("id"));
				journal.setDateOperation(resultSet.getString("date_operation"));
				journal.setLibelleOperation(resultSet.getString("libelle_operation"));
				journal.setPieceJustificative(resultSet.getString("piece_justificative"));
				journal.setSomme(resultSet.getInt("somme"));
				journal.setObservation(resultSet.getString("observation"));
				journals.add(journal);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return journals;
	}
	
	@Override
	public int dataRecordsTotalSortie() {
		String query = "SELECT COUNT(*) AS total_records";
		query += " FROM";
		query += " journal_entree_sortie WHERE sortie = true AND entree = false";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public int dataRecordsTotalEntree() {
		String query = "SELECT COUNT(*) AS total_records";
		query += " FROM";
		query += " journal_entree_sortie WHERE sortie = false AND entree = true";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public int sommeEntrees(int mois, int annee) {
		StringBuilder query = new StringBuilder();
		query
			.append(" SELECT ")
			.append(" SUM(somme) ")
			.append(" FROM ")
			.append(" journal_entree_sortie ")
			.append(" WHERE ")
			.append(" sortie = false AND entree = true ")
			.append(" AND ")
			.append(" EXTRACT(MONTH from date_operation)::integer = ? ")
			.append(" EXTRACT(YEAR from date_operation)::integer = ? ");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query.toString().trim());
			preparedStatement.setInt(1, mois);
			preparedStatement.setInt(2, annee);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	@Override
	public int sommeSorties(int mois, int annee) {
		StringBuilder query = new StringBuilder();
		query
			.append(" SELECT ")
			.append(" SUM(somme) ")
			.append(" FROM ")
			.append(" journal_entree_sortie ")
			.append(" WHERE ")
			.append(" sortie = true AND entree = false ")
			.append(" AND ")
			.append(" EXTRACT(MONTH from date_operation)::integer = ? ")
			.append(" EXTRACT(YEAR from date_operation)::integer = ? ");
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query.toString().trim());
			preparedStatement.setInt(1, mois);
			preparedStatement.setInt(2, annee);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) output = resultSet.getInt(0);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
	
	private String escapeQuotes(String entry) {
		return entry.replaceAll("'", "''");
	}
}
