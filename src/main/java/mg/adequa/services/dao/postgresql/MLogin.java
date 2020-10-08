package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQLQueryBuilder;
import mg.adequa.beans.BUtilisateur;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DLogin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MLogin implements DLogin {
	private DaoFactory dao;
	private DbTables tables;
	
	public MLogin(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public boolean urtilisateurExiste(String login) throws SQLException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query
			       .where("login", login)
			       .from(this.tables.getUtilisateur())
			       .rowCount() > 0;
	}
	
	@Override
	public String getPassword(String login) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		String getPassword = null;
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		ResultSet resultSet = query
			                      .select("password")
			                      .from(this.tables.getUtilisateur())
			                      .where("login", login)
			                      .get()
			                      .result();
		if (resultSet.next()) getPassword = resultSet.getString("password");
		return getPassword;
	}
	
	@Override
	public BUtilisateur getData(String login) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		BUtilisateur getData = null;
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		Map<String, String> transposition = new HashMap<>();
		ResultSet resultSet =
			query
				.select(new String[]{
					this.tables.getUtilisateur() + ".id",
					"nom",
					"prenom",
					"role",
					"administrateur",
					"login",
					"poste"
				})
				.select(transposition)
				.from(this.tables.getUtilisateur())
				.join(this.tables.getPersonnel() + ".id", this.tables.getUtilisateur() + ".personnel")
				.where("login", login)
				.get()
				.result();
		if (resultSet.next()) {
			getData = new BUtilisateur();
			getData.setId(resultSet.getInt("id"));
			getData.setLogin(resultSet.getString("login"));
			getData.setAdministrateur(resultSet.getBoolean("administrateur"));
			getData.setRole(resultSet.getString("role"));
			getData.setNom(resultSet.getString("nom"));
			getData.setPrenom(resultSet.getString("prenom"));
			getData.setPoste(resultSet.getInt("poste"));
		}
		return getData;
	}
}
