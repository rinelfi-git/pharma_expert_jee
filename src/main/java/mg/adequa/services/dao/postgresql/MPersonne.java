package mg.adequa.services.dao.postgresql;

import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BPersonne;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DPersonne;
import lib.querybuilder.implementations.PostgreSQLQueryBuilder;
import lib.querybuilder.QueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MPersonne implements DPersonne {
	private DaoFactory dao;
	private DbTables tables;
	
	public MPersonne(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public boolean insert(BPersonne personne) throws Exception {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .set("nom", personne.getNom())
			       .set("prenom", personne.getPrenom()).insert(this.tables.getPersonne());
	}
	
	@Override
	public int lastId() throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		int lastId = 0;
		ResultSet resultSet = query.select("id").from(this.tables.getPersonne()).orderBy("id", OrderBy.DESC).limit(1).get().result();
		if(resultSet.next()) lastId = resultSet.getInt("id");
		return lastId;
	}
	
	@Override
	public boolean update(BPersonne personne, int reference) throws Exception {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query.set("nom", personne.getNom()).set("prenom", personne.getPrenom()).where("id", reference).update(this.tables.getPersonne());
	}
}
