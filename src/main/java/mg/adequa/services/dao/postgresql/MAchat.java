package mg.adequa.services.dao.postgresql;

import mg.adequa.payloads.PAchat;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DAchat;
import mg.adequa.utils.DatatableParameter;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MAchat implements DAchat {
	private DaoFactory daoFactory;
	
	public MAchat(DaoFactory daoFactory) { this.daoFactory = daoFactory;}
	
	
	@Override
	public String makeQuery(DatatableParameter constraints) {
		String[] colonne = new String[]{"date_operation", "facture.numero", "fournisseur", "somme", "mode_de_payement", "reference", "payement", "date_echeance"};
		String query = "SELECT id, facture, fournisseur, somme, mode_de_payement, reference, payement" +
			               ", to_char(date_operation, 'DD Month YYYY') AS date_operation" +
			               ", to_char(date_echeance, 'DD Month YYYY') AS date_echeance" +
			               " from datatable_achat";
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query += " WHERE date_operation::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR fournisseur ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR mode_de_payement ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR date_echeance::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
			query += " OR somme::varchar ILIKE '%" + this.escapeQuotes(constraints.getSearch().getValue()) + "%'";
		}
		if (constraints.getOrderColumn() != -1) query += " ORDER BY " + colonne[constraints.getOrderColumn()] + " " + constraints.getOrderDirection();
		else query += " ORDER BY date_operation ASC";
		return query.trim();
	}
	
	@Override
	public ArrayList<Map> makeDatatable(String query, DatatableParameter constraints) {
		ArrayList<Map> output = new ArrayList<>();
		if (constraints.getLimitLength() != -1) query += " LIMIT " + constraints.getLimitLength() + " OFFSET " + constraints.getLimitStart();
		ResultSet resultSet = null;
		Connection connection = null;
		Statement statement = null;
		try {
			connection = this.daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				Map<String, Object> tmp = new HashMap<>();
				tmp.put("id", resultSet.getInt("id"));
				tmp.put("dateOperation", resultSet.getString("date_operation"));
				tmp.put("facture", resultSet.getString("facture"));
				tmp.put("fournisseur", resultSet.getString("fournisseur"));
				tmp.put("somme", resultSet.getInt("somme"));
				tmp.put("modeDePayement", resultSet.getString("mode_de_payement"));
				tmp.put("reference", resultSet.getString("reference"));
				tmp.put("payement", resultSet.getString("payement"));
				tmp.put("dateEcheance", resultSet.getString("date_echeance"));
				output.add(tmp);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return output;
	}
	
	@Override
	public int dataRecordsTotal() {
		String query = "SELECT COUNT(*) AS total_records";
		query += " FROM";
		query += " datatable_achat";
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
		}
		return output;
	}
	
	@Override
	public PAchat select(int reference) {
		return null;
	}
	
	@Override
	public ArrayList<PAchat> select() {
		return null;
	}
	
	@Override
	public boolean insertOnly(PAchat pAchat) {
		boolean output = false;
		String query = "INSERT INTO";
		query += " achat(" +
			         "facture," +
			         "date_operation," +
			         "libelle," +
			         "somme," +
			         "en_attente," +
			         "date_echeance" +
			         ") " +
			         "VALUES (?, ?, ?, ?, ?, ?)";
		Connection connection;
		PreparedStatement preparedStatement;
		try {
			connection = this.daoFactory.getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, pAchat.getFacture());
			preparedStatement.setDate(2, new Date(new SimpleDateFormat("yyyy-MM-dd").parse(pAchat.getDateOperation()).getTime()));
			preparedStatement.setString(3, pAchat.getLibelle());
			preparedStatement.setInt(4, pAchat.getSomme());
			preparedStatement.setBoolean(5, pAchat.isEnAttente());
			preparedStatement.setDate(6, new Date(new SimpleDateFormat("yyyy-MM-dd").parse(pAchat.getDateEcheance()).getTime()));
			output = preparedStatement.executeUpdate() > 0;
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return output;
	}
	
	@Override
	public boolean insertAndLog(PAchat pAchat) {
		return false;
	}
	
	@Override
	public boolean update(int reference, PAchat pAchat) {
		return false;
	}
	
	private String escapeQuotes(String entry) {
		return entry.replaceAll("'", "''");
	}
}
