package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BJournalDeSession;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DJournalDeSession;
import mg.adequa.tableviews.TJournalDeSession;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MJournalDeSession implements DJournalDeSession {
	private DaoFactory daoFactory;
	private DbTables tables;
	
	public MJournalDeSession(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
		this.tables = new DbTables();
	}
	
	@Override
	public ArrayList<TJournalDeSession> makeDatatable(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<TJournalDeSession> makeDatatable = new ArrayList<>();
		
		//query field
		PostgreSQL queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		String[] colonne = new String[]{
			"to_char(date, 'DD Month YYYY HH24:MI:SS')",
			"nom, prenom, " + this.tables.getPoste() + ".nom",
			"action"
		};
		Map<String, String> transposition = new HashMap<>();
		transposition.put(this.tables.getPoste() + ".nom", "poste");
		transposition.put("to_char(date, 'DD Month YYYY HH24:MI:SS')", "date");
		queryBuilder
			.select(new String[]{
				this.tables.getJournalDeSession() + ".id",
				this.tables.getPersonne() + ".nom",
				"prenom",
				"action"
			})
			.select(transposition)
			.from(this.tables.getJournalDeSession())
			.join(this.tables.getUtilisateur() + ".id", this.tables.getJournalDeSession() + ".compte_personnel")
			.join(this.tables.getPersonnel() + ".numero", this.tables.getUtilisateur() + ".personnel")
			.join(this.tables.getPersonne() + ".id", this.tables.getPersonnel() + ".personne")
			.join(this.tables.getPoste() + ".id", this.tables.getPersonnel() + ".poste");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			queryBuilder.iLike(this.tables.getPersonne() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("prenom", "%" + constraints.getSearch().getValue() + "%")
				.orILike(this.tables.getPoste() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("to_char(date, 'DD Month YYYY HH24:MI:SS')", "%" + constraints.getSearch().getValue() + "%")
				.orILike("action", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) queryBuilder.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else queryBuilder.orderBy("date", OrderBy.DESC);
		// limite
		if (constraints.getLimitLength() != -1) queryBuilder.limit(constraints.getLimitLength(), constraints.getLimitStart());
		// limite
		//query field
		
		ResultSet resultSet = queryBuilder.get().result();
		while (resultSet.next()) {
			TJournalDeSession journalDeSession = new TJournalDeSession();
			journalDeSession.setDateHeure(resultSet.getString("date"));
			journalDeSession.setUtilisateur(resultSet.getString("nom") + " " + resultSet.getString("prenom") + " " + resultSet.getString("poste"));
			journalDeSession.setTache(resultSet.getString("action"));
			makeDatatable.add(journalDeSession);
		}
		if (resultSet != null) resultSet.close();
		queryBuilder.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		QueryBuilder queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		int dataRecordsTotal = queryBuilder.count(this.tables.getJournalDeSession());
		queryBuilder.close();
		return dataRecordsTotal;
	}
	
	@Override
	public int recordFiltered(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		//query field
		PostgreSQL queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		String[] colonne = new String[]{
			"to_char(date, 'DD Month YYYY HH24:MI:SS')",
			"nom, prenom, " + this.tables.getPoste() + ".nom",
			"action"
		};
		Map<String, String> transposition = new HashMap<>();
		transposition.put(this.tables.getPoste() + ".nom", "poste");
		transposition.put("to_char(date, 'DD Month YYYY HH24:MI:SS')", "date");
		queryBuilder
			.select(new String[]{
				this.tables.getJournalDeSession() + ".id",
				this.tables.getPersonne() + ".nom",
				"prenom",
				"action"
			})
			.select(transposition)
			.from(this.tables.getJournalDeSession())
			.join(this.tables.getUtilisateur() + ".id", this.tables.getJournalDeSession() + ".compte_personnel")
			.join(this.tables.getPersonnel() + ".numero", this.tables.getUtilisateur() + ".personnel")
			.join(this.tables.getPersonne() + ".id", this.tables.getPersonnel() + ".personne")
			.join(this.tables.getPoste() + ".id", this.tables.getPersonnel() + ".poste");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			queryBuilder.iLike(this.tables.getPersonne() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("prenom", "%" + constraints.getSearch().getValue() + "%")
				.orILike(this.tables.getPoste() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("to_char(date, 'DD Month YYYY HH24:MI:SS')", "%" + constraints.getSearch().getValue() + "%")
				.orILike("action", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) queryBuilder.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else queryBuilder.orderBy("date", OrderBy.DESC);
		//query field
		int recordFiltered = queryBuilder.get().rowCount();
		queryBuilder.close();
		return recordFiltered;
	}
	
	@Override
	public boolean insert(BJournalDeSession journal) throws Exception {
		QueryBuilder queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		return queryBuilder.set("compte_personnel", journal.getComptePersonnel()).set("action", journal.getAction()).insert(this.tables.getJournalDeSession());
	}
	
	@Override
	public boolean delete() throws Exception {
		QueryBuilder queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		return queryBuilder.delete(this.tables.getJournalDeSession());
	}
}
