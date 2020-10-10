package mg.adequa.services.dao.postgresql;

import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BFournisseur;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PFournisseur;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DFournisseur;
import mg.adequa.tableviews.TFournisseur;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MFournisseur implements DFournisseur {
	private DaoFactory dao;
	private DbTables tables;
	
	public MFournisseur(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public ArrayList<TFournisseur> makeDatatable(DatatableParameter constraints) throws NoSpecifiedTableException, SQLException, NoConnectionException {
		String[] columns = new String[]{"nom", "localisation", "information_complementaire"};
		ArrayList<TFournisseur> makeDatatable = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		ResultSet resultSet;
		
		query.select()
			.from(this.tables.getFournisseur());
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query.iLike("nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("localisation", "%" + constraints.getSearch().getValue() + "%")
				.orILike("information_complementaire", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) query.orderBy(columns[constraints.getOrderColumn()], constraints.getOrderDirection());
		else query.orderBy(this.tables.getPoste() + ".nom, prenom", OrderBy.ASC);
		if (constraints.getLimitLength() != -1) query.limit(constraints.getLimitLength(), constraints.getLimitStart());
		resultSet = query.get().result();
		while (resultSet.next()) {
			TFournisseur temporary = new TFournisseur();
			temporary.setId(resultSet.getInt("id"));
			temporary.setNom(resultSet.getString("nom"));
			temporary.setDescription(resultSet.getString("information_complementaire"));
			makeDatatable.add(temporary);
		}
		if(resultSet != null) resultSet.close();
		query.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		return query.count(this.tables.getFournisseur());
	}
	
	@Override
	public ArrayList<PFournisseur> select() throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<PFournisseur> select = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		ResultSet resultSet = query.select().from(this.tables.getFournisseur()).get().result();
		while(resultSet.next()) {
			PFournisseur temporary = new PFournisseur();
			temporary.setId(resultSet.getInt("id"));
			temporary.setNom(resultSet.getString("nom"));
			temporary.setLocalisation(resultSet.getString("localisation"));
			temporary.setInformationComplementaire(resultSet.getString("information_complementaire"));
			select.add(temporary);
		}
		if(resultSet != null) resultSet.close();
		query.close();
		return select;
	}
	
	@Override
	public PFournisseur select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		PFournisseur select = null;
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		ResultSet resultSet = query.select().from(this.tables.getFournisseur()).where("id", id).get().result();
		if(resultSet.next()) {
			select = new PFournisseur();
			select.setId(resultSet.getInt("id"));
			select.setNom(resultSet.getString("nom"));
			select.setLocalisation(resultSet.getString("localisation"));
			select.setInformationComplementaire(resultSet.getString("information_complementaire"));
		}
		if(resultSet != null) resultSet.close();
		query.close();
		return select;
	}
	
	@Override
	public boolean insert(BFournisseur fournisseur) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("nom", fournisseur.getNom());
		keyValue.put("localisation", fournisseur.getLocalisation());
		keyValue.put("information_complementaire", fournisseur.getInformationComplementaire());
		return query.set(keyValue).insert(this.tables.getFournisseur());
	}
	
	@Override
	public boolean update(int id, BFournisseur fournisseur) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("nom", fournisseur.getNom());
		keyValue.put("localisation", fournisseur.getLocalisation());
		keyValue.put("information_complementaire", fournisseur.getInformationComplementaire());
		return query.set(keyValue).where("id", id).update(this.tables.getFournisseur());
	}
}
