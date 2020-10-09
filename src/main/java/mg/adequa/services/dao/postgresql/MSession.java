package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQLQueryBuilder;
import mg.adequa.beans.BSession;
import mg.adequa.beans.BUtilisateur;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PSession;
import mg.adequa.payloads.PUtilisateur;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MSession implements DSession {
	private DbTables tables;
	private DaoFactory dao;
	
	public MSession(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public boolean insert(BSession<BUtilisateur> session) throws SQLException, InvalidExpressionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .set("id", session.getId())
			       .set("type", session.getType())
			       .set("date_creation", new Timestamp(session.getDateCreation().getTime()))
			       .set("date_expiration", new Timestamp(session.getDateExpiration().getTime()))
			       .set("utilisateur", session.getContenu().getIdUtilisateur())
			       .insert(this.tables.getSession());
	}
	
	@Override
	public boolean delete(String id) throws SQLException, InvalidExpressionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .where("id", id)
			       .delete(this.tables.getSession());
	}
	
	@Override
	public boolean exists(String id) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .where("id", id)
			       .rowCount(this.tables.getSession()) > 0;
	}
	
	@Override
	public boolean addTimer(String id) throws SQLException, InvalidExpressionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .where("id", id)
			       .set("date_expiration", new Timestamp(System.currentTimeMillis() + BSession.DEFAULT_TIMER * 1000))
			       .update(this.tables.getSession());
	}
	
	@Override
	public PSession<PUtilisateur> get(String id) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		PSession<PUtilisateur> get = null;
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		Map<String, String> transformation = new HashMap<>();
		transformation.put(this.tables.getUtilisateur() + ".id", "id_utilisateur");
		transformation.put(this.tables.getSession() + ".id", "id_session");
		transformation.put(this.tables.getPoste() + ".nom", "nom_poste");
		ResultSet resultSet = query.select(new String[]{
			"type",
			"date_creation",
			"date_expiration",
			this.tables.getPersonne() + ".nom",
			this.tables.getPersonne() + ".prenom",
			"login",
			"administrateur",
			"role",
			"poste",
			"email"
		})
			                      .select(transformation)
			                      .from(this.tables.getSession())
			                      .join(this.tables.getUtilisateur() + ".id", this.tables.getSession() + ".utilisateur")
			                      .join(this.tables.getPersonnel() + ".numero", this.tables.getUtilisateur() + ".personnel")
			                      .join(this.tables.getPoste() + ".id", this.tables.getPersonnel() + ".poste")
			                      .join(this.tables.getPersonne() + ".id", this.tables.getPersonnel() + ".personne")
			                      .get()
			                      .result();
		if (resultSet.next()) {
			get = new PSession<>();
			PUtilisateur utilisateur = new PUtilisateur();
			
			utilisateur.setIdUtilisateur(resultSet.getInt("id_utilisateur"));
			utilisateur.setLogin(resultSet.getString("login"));
			utilisateur.setAdministrateur(resultSet.getBoolean("administrateur"));
			utilisateur.setRole(resultSet.getString("role"));
			utilisateur.setNom(resultSet.getString("nom"));
			utilisateur.setPrenom(resultSet.getString("prenom"));
			utilisateur.setEmail(resultSet.getString("email"));
			utilisateur.setPoste(resultSet.getInt("poste"));
			utilisateur.setNomPoste(resultSet.getString("nom_poste"));
			
			get.setId(resultSet.getString("id_session"));
			get.setContenu(utilisateur);
			get.setDateCreation(resultSet.getTimestamp("date_creation"));
			get.setDateExpiration(resultSet.getTimestamp("date_expiration"));
			get.setType(resultSet.getString("type"));
		}
		return get;
	}
}
