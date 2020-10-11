package mg.adequa.services.dao.postgresql;

import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.dbentity.BPersonneOng;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PPersonneOng;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DPersonneOng;
import mg.adequa.tableviews.TPersonneOng;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MPersonneOng implements DPersonneOng {
	private final DaoFactory daoFactory;
	private final DbTables tables;
	
	public MPersonneOng(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
		this.tables = new DbTables();
	}
	
	@Override
	public ArrayList<TPersonneOng> makeDatatable(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<TPersonneOng> makeDatatable = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		Map<String, String> transpositions = new HashMap<>();
		
		transpositions.put(this.tables.getOng() + ".libelle", "ong");
		String[] selection = new String[]{
			this.tables.getPersonne() + ".nom",
			"prenom"
		};
		String[] colonne = new String[]{
			this.tables.getPersonne() + ".nom, prenom",
			this.tables.getOng() + ".libelle"
		};
		query
			.select(selection)
			.select(transpositions)
			.from(this.tables.getPersonneOng())
			.join(this.tables.getOng() + ".id", this.tables.getPersonneOng() + ".ong")
			.join(this.tables.getPersonne() + ".id", this.tables.getPersonneOng() + ".id");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query.iLike(this.tables.getPersonne() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orLike("prenom", "%" + constraints.getSearch().getValue() + "%")
				.orLike(this.tables.getOng() + ".libelle", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) query.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else query.orderBy(this.tables.getPersonne() + ".nom, prenom", OrderBy.ASC);
		if (constraints.getLimitLength() != -1) query.limit(constraints.getLimitLength(), constraints.getLimitStart());
		ResultSet resultSet = query.get().result();
		while (resultSet.next()) {
			TPersonneOng temporary = new TPersonneOng();
			temporary.setId(resultSet.getInt("id"));
			temporary.setNomPrenom(resultSet.getString("nom") + " " + resultSet.getString("prenom"));
			temporary.setOng(resultSet.getString("ong"));
			makeDatatable.add(temporary);
		}
		if (resultSet != null) resultSet.close();
		query.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		return query.count(this.tables.getPersonneOng());
	}
	
	@Override
	public boolean insert(BPersonneOng personneOng) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("id", personneOng.getId());
		keyValue.put("ong", personneOng.getOng());
		return query.set(keyValue).insert(this.tables.getPersonneOng());
	}
	
	@Override
	public boolean update(int id, BPersonneOng personneOng) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("id", personneOng.getId());
		keyValue.put("ong", personneOng.getOng());
		return query.set(keyValue).where("id", id).update(this.tables.getFournisseur());
	}
	
	@Override
	public ArrayList<PPersonneOng> select() throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<PPersonneOng> select = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		ResultSet resultSet = query.select(new String[]{this.tables.getPersonne() + ".id", "nom", "prenom", "ong"}).from(this.tables.getPersonneOng()).join(this.tables.getPersonne() + ".id", this.tables.getPersonneOng() + ".id").get().result();
		while(resultSet.next()) {
			PPersonneOng temporary = new PPersonneOng();
			temporary.setId(resultSet.getInt("id"));
			temporary.setNom(resultSet.getString("nom"));
			temporary.setPrenom(resultSet.getString("prenom"));
			temporary.setOng(resultSet.getInt("ong"));
			select.add(temporary);
		}
		return select;
	}
	
	@Override
	public PPersonneOng select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		PPersonneOng select = null;
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		ResultSet resultSet = query.select(new String[]{this.tables.getPersonne() + ".id", "nom", "prenom", "ong"}).from(this.tables.getPersonneOng()).join(this.tables.getPersonne() + ".id", this.tables.getPersonneOng() + ".id").get().result();
		if(resultSet.next()) {
			select = new PPersonneOng();
			select.setId(resultSet.getInt("id"));
			select.setNom(resultSet.getString("nom"));
			select.setPrenom(resultSet.getString("prenom"));
			select.setOng(resultSet.getInt("ong"));
		}
		return select;
	}
}
