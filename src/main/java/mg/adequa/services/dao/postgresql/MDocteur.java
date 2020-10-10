package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BDocteur;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PDocteur;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DDocteur;
import mg.adequa.tableviews.TDocteur;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MDocteur implements DDocteur {
	private DbTables tables;
	private DaoFactory daoFactory;
	
	public MDocteur(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
		this.tables = new DbTables();
	}
	
	@Override
	public PostgreSQL makeQuery(DatatableParameter constraints) throws SQLException {
		PostgreSQL query = new PostgreSQL(this.daoFactory.getConnection());
		String[] colonne = new String[]{
			"nom",
			"prenom",
			this.tables.getServiceHospitalier() + ".libelle"
		};
		query
			.select(new String[]{this.tables.getDocteur() + ".id", "nom", "prenom"})
			.select(this.tables.getServiceHospitalier() + ".libelle", "service ")
			.from(this.tables.getPersonne())
			.join(this.tables.getDocteur() + ".id", this.tables.getPersonne() + ".id")
			.join(this.tables.getServiceHospitalier() + ".id", this.tables.getDocteur() + ".service_hospitalier");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query.iLike("nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("prenom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("libelle", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) return (PostgreSQL) query.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else return (PostgreSQL) query.orderBy("nom, prenom", OrderBy.ASC);
	}
	
	@Override
	public ArrayList<TDocteur> makeDatatable(QueryBuilder queryBuilder, DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<TDocteur> makeDatatable = new ArrayList<>();
		if (constraints.getLimitLength() != -1) queryBuilder.limit(constraints.getLimitLength(), constraints.getLimitStart());
		ResultSet resultSet = null;
		resultSet = queryBuilder.get().result();
		while (resultSet.next()) {
			TDocteur docteur = new TDocteur();
			docteur.setId(resultSet.getInt("id"));
			docteur.setNomPrenom(resultSet.getString("nom") + " " + resultSet.getString("prenom"));
			docteur.setService(resultSet.getString("service"));
			makeDatatable.add(docteur);
		}
		if (resultSet != null) resultSet.close();
		queryBuilder.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		QueryBuilder queryBuilder = new PostgreSQL(this.daoFactory.getConnection());
		return queryBuilder.count(this.tables.getDocteur());
	}
	
	@Override
	public PDocteur select(int reference) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQL(this.daoFactory.getConnection());
		ResultSet resultSet = query
			                      .select(new String[]{this.tables.getPersonne() + ".id", "nom", "prenom", "service_hospitalier"})
			                      .from(this.tables.getDocteur())
			                      .join(this.tables.getDocteur() + ".id", this.tables.getPersonne() + ".id")
			                      .get()
			                      .result();
		PDocteur select = null;
		if (resultSet.next()) {
			select = new PDocteur();
			select.setId(resultSet.getInt("id"));
			select.setNom(resultSet.getString("nom"));
			select.setPrenom(resultSet.getString("prenom"));
			select.setServiceHospitalier(resultSet.getInt("service_hospitalier"));
		}
		return select;
	}
	
	@Override
	public boolean insert(BDocteur docteur) throws Exception {
		QueryBuilder query = new PostgreSQL(this.daoFactory.getConnection());
		return query
			       .set("nom", docteur.getNom())
			       .set("id", docteur.getIdPersonne())
			       .set("prenom", docteur.getPrenom())
			       .insert(this.tables.getPersonne());
	}
	
	@Override
	public boolean update(BDocteur docteur, int reference) throws Exception {
		QueryBuilder query = new PostgreSQL(this.daoFactory.getConnection());
		return query
			       .set("id", docteur.getIdPersonne())
			       .set("service_hospitalier", docteur.getServiceHospitalier())
			       .update(this.tables.getPersonne());
	}
}
