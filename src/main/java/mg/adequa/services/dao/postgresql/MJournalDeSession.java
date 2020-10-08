package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQLQueryBuilder;
import mg.adequa.beans.BJournalDeSession;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DJournalDeSession;
import mg.adequa.tableviews.TJournalDeSession;
import mg.adequa.tableviews.TvDocteur;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MJournalDeSession implements DJournalDeSession {
	private DaoFactory dao;
	private DbTables tables;
	
	public MJournalDeSession(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public QueryBuilder makeQuery(DatatableParameter constraints) throws SQLException {
		PostgreSQLQueryBuilder qeryBuilder = new PostgreSQLQueryBuilder(this.dao.getConnection());
		String[] colonne = new String[]{
			"to_char(date, 'DD Month YYYY HH24:MI:SS')",
			"nom, prenom, " + this.tables.getPoste() + ".nom",
			"action"
		};
		Map<String, String> transposition = new HashMap<>();
		transposition.put(this.tables.getPoste() + ".nom", "poste");
		transposition.put("to_char(date, 'DD Month YYYY HH24:MI:SS')", "date");
		qeryBuilder
			.select(new String[]{
				this.tables.getJournalDeSession() + ".id",
				"nom",
				"prenom",
				"action"
			})
			.select(transposition)
			.from(this.tables.getJournalDeSession())
			.join(this.tables.getUtilisateur() + ".id", this.tables.getJournalDeSession() + ".compte_personnel")
			.join(this.tables.getPersonnel() + ".numero", this.tables.getUtilisateur() + ".personnel")
			.join(this.tables.getPoste() + ".id", this.tables.getPersonnel() + ".poste");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			qeryBuilder.iLike("nom", "%" + constraints.getSearch().getValue() + "%")
			           .orLike("prenom", "%" + constraints.getSearch().getValue() + "%")
			           .orLike(this.tables.getPoste() + ".nom", "%" + constraints.getSearch().getValue() + "%")
			           .orLike("to_char(date, 'DD Month YYYY HH24:MI:SS')", "%" + constraints.getSearch().getValue() + "%")
			           .orLike("action", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) return qeryBuilder.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else return qeryBuilder.orderBy("date", OrderBy.DESC);
	}
	
	@Override
	public ArrayList<TJournalDeSession> makeDatatable(QueryBuilder query, DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<TJournalDeSession> makeDatatable = new ArrayList<>();
		if (constraints.getLimitLength() != -1) query.limit(constraints.getLimitLength(), constraints.getLimitStart());
		ResultSet resultSet;
		resultSet = query.get().result();
		while (resultSet.next()) {
			TJournalDeSession journalDeSession = new TJournalDeSession();
			journalDeSession.setDateHeure(resultSet.getString("date"));
			journalDeSession.setUtilisateur(resultSet.getString("nom") + " " + resultSet.getString("prenom") + " " + resultSet.getString("poste"));
			journalDeSession.setTache(resultSet.getString("action"));
			makeDatatable.add(journalDeSession);
		}
		if (resultSet != null) resultSet.close();
		query.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException{
		QueryBuilder queryBuilder = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return queryBuilder.count(this.tables.getJournalDeSession());
	}
	
	@Override
	public boolean insert(BJournalDeSession journal) throws Exception {
		QueryBuilder queryBuilder = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return queryBuilder.set("compte_personnel", journal.getComptePersonnel())
			.set("action", journal.getAction()).insert(this.tables.getJournalDeSession());
	}
	
	@Override
	public boolean delete() throws Exception {
		QueryBuilder queryBuilder = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return queryBuilder.delete(this.tables.getJournalDeSession());
	}
}
