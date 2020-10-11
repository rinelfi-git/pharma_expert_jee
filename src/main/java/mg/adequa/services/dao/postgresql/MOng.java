package mg.adequa.services.dao.postgresql;

import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BOng;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PFournisseur;
import mg.adequa.payloads.POng;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DOng;
import mg.adequa.tableviews.TJournalDeSession;
import mg.adequa.tableviews.TOng;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MOng implements DOng {
	private final DaoFactory daoFactory;
	private final DbTables tables;
	
	public MOng(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
		this.tables = new DbTables();
	}
	
	@Override
	public ArrayList<TOng> makeDatatable(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<TOng> makeDatatable = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		String[] colonne = new String[]{
			"libelle",
			"description"
		};
		query.select().from(this.tables.getOng());
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query.iLike("libelle", "%" + constraints.getSearch().getValue() + "%")
				.orILike("description", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) query.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else query.orderBy("libelle", OrderBy.ASC);
		if (constraints.getLimitLength() != -1) query.limit(constraints.getLimitLength(), constraints.getLimitStart());
		ResultSet resultSet = query.get().result();
		while (resultSet.next()) {
			TOng temporary = new TOng();
			temporary.setId(resultSet.getInt("id"));
			temporary.setLibelle(resultSet.getString("libelle"));
			temporary.setDescription(resultSet.getString("description"));
			makeDatatable.add(temporary);
		}
		if(resultSet != null) resultSet.close();
		query.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		return query.count(this.tables.getOng());
	}
	
	@Override
	public boolean insert(BOng ong) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("libelle", ong.getLibelle());
		keyValue.put("description", ong.getDescription());
		return query.set(keyValue).insert(this.tables.getOng());
	}
	
	@Override
	public boolean update(int id, BOng ong) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		Map<String, Object> keyValue = new HashMap<>();
		keyValue.put("libelle", ong.getLibelle());
		keyValue.put("description", ong.getDescription());
		return query.set(keyValue).where("id", id).update(this.tables.getOng());
	}
	
	@Override
	public ArrayList<POng> select() throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<POng> select = new ArrayList<>();
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		ResultSet resultSet = query.select().from(this.tables.getOng()).get().result();
		while(resultSet.next()) {
			POng temporary = new POng();
			temporary.setId(resultSet.getInt("id"));
			temporary.setLibelle(resultSet.getString("libelle"));
			temporary.setDescription(resultSet.getString("description"));
			select.add(temporary);
		}
		return select;
	}
	
	@Override
	public POng select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		POng select = null;
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		ResultSet resultSet = query.select().from(this.tables.getOng()).where("id", id).get().result();
		if(resultSet.next()) {
			select = new POng();
			select.setId(resultSet.getInt("id"));
			select.setLibelle(resultSet.getString("libelle"));
			select.setDescription(resultSet.getString("description"));
		}
		return select;
	}
}
