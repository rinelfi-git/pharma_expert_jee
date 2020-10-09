package mg.adequa.services.dao.postgresql;

import mg.adequa.payloads.PAutreEntree;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DAutreEntree;
import mg.adequa.tableviews.AutreEntreeTV;
import mg.adequa.utils.DatatableParameter;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MAutreEntree implements DAutreEntree {
	private DaoFactory daoFactory;
	
	public MAutreEntree(DaoFactory daoFactory) { this.daoFactory = daoFactory; }
	
	@Override
	public String makeQuery(DatatableParameter constraints) {
		String[] colonne = new String[]{"date_operation", "libelle_operation_reference, libelle_operation_saisie", "numero_de_piece_justificative", "somme", "observation"};
		String query = "SELECT" +
			               "libelle_operation," +
			               "numero_piece_justificative," +
			               "somme, observation," +
			               "id," +
			               "to_char(date_operation, 'DD Month YYYY HH24:MI:SS') AS date_operation";
		query += " FROM autre_entree_datatable";
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query += " WHERE date_operation::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR libelle_operation_reference ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR libelle_operation_saisie ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR numero_de_piece_justificative ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR observation ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR somme::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
		}
		if (constraints.getOrderColumn() != -1) query += " ORDER BY " + colonne[constraints.getOrderColumn()] + " " + constraints.getOrderDirection();
		else query += " ORDER BY date_operation ASC";
		return query.trim();
	}
	
	@Override
	public ArrayList<AutreEntreeTV> makeDatatable(String query, DatatableParameter constraints) {
		ArrayList<AutreEntreeTV> output = new ArrayList<>();
		if (constraints.getLimitLength() != -1) query += " LIMIT " + constraints.getLimitLength() + " OFFSET " + constraints.getLimitStart();
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				AutreEntreeTV temp = new AutreEntreeTV();
				temp.setId(resultSet.getInt("id"));
				temp.setDateOperation(resultSet.getString("date_operation"));
				temp.setLibelleOperation(resultSet.getString("libelle_operation"));
				temp.setNumeroDePieceJustificative(resultSet.getString("numero_piece_justificative"));
				temp.setObservation(resultSet.getString("observation"));
				temp.setSomme(resultSet.getInt("somme"));
				output.add(temp);
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
		return output;
	}
	
	@Override
	public int dataRecordsTotal() {
		String query = "SELECT COUNT(*) AS total_records" +
			               " FROM";
		query += " autre_entree_datatable";
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		int output = 0;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			if (resultSet.next()) output = resultSet.getInt("total_records");
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
	public PAutreEntree select(int reference) {
		return null;
	}
	
	@Override
	public boolean insert(PAutreEntree data) {
		boolean output = false;
		String autreEntreeInsert = "INSERT INTO";
		autreEntreeInsert += " autre_entree(" +
			         "date_operation," +
			         "libelle_enregistre,";
		if (data.isLibelleEnregistre()) autreEntreeInsert += "libelle_operation_reference,";
		else autreEntreeInsert += "libelle_operation_saisie,";
		autreEntreeInsert += "numero_de_piece_justificative," +
			         "mode_de_payement," +
			         "somme," +
			         "observation)" +
			         " VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
		String mainQuery = "INSERT INTO";
		mainQuery += " journal_entree(" +
			        "date_operation," +
			        "libelle_operation," +
			        "autre_entree," +
			        "somme," +
			        "observation," +
			        "entree," +
			        "sortie)" +
			        " VALUES(?, ?, (" + autreEntreeInsert + "), ?, ?, true, false)";
		Connection connection;
		PreparedStatement preparedStatement;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(mainQuery);
			
			preparedStatement.setDate(1, new Date(new SimpleDateFormat().parse(data.getDateOperation()).getTime()));
			preparedStatement.setString(2, data.getLibelleOperation());
			preparedStatement.setDate(3, new Date(new SimpleDateFormat().parse(data.getDateOperation()).getTime()));
			preparedStatement.setBoolean(4, data.isLibelleEnregistre());
			preparedStatement.setString(5, data.getLibelleOperation());
			preparedStatement.setString(6, data.getNumeroDePieceJustificative());
			preparedStatement.setString(7, data.getModeDePayment());
			preparedStatement.setInt(8, data.getSomme());
			preparedStatement.setString(9, data.getObservation());
			preparedStatement.setInt(10, data.getSomme());
			preparedStatement.setString(11, data.getObservation());
			
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	@Override
	public boolean insertAndArchive(PAutreEntree data) {
		return false;
	}
	
	private String escapeQuotes(String entry) {
		return entry.replaceAll("'", "''");
	}
}
