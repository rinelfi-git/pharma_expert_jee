package mg.adequa.services.dao.postgresql;

import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BAutorisation;
import mg.adequa.beans.BMenu;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DAutorisation;
import mg.adequa.tableviews.TAutorisation;
import mg.adequa.utils.DatatableParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MAutorisation implements DAutorisation {
	private DaoFactory dao;
	private DbTables tables;
	
	public MAutorisation(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public PostgreSQL makeQuery(DatatableParameter constraints) throws SQLException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		Map<String, String> transposition = new HashMap<>();
		transposition.put(this.tables.getUtilisateur() + ".id", "id_utilisateur");
		transposition.put(this.tables.getPoste() + ".nom", "nom_poste");
		String[] colonne = new String[]{
			this.tables.getPersonne() + ".nom," +
				"prenom," +
				this.tables.getPoste() + ".nom"
		};
		query.select(transposition).select(new String[]{
			this.tables.getAutorisation() + ".id",
			"nom",
			"prenom"
		})
			.select(this.tables.getServiceHospitalier() + ".libelle", "service ")
			.from(this.tables.getPersonne())
			.join(this.tables.getPersonnel() + ".personne", this.tables.getPersonne() + ".id")
			.join(this.tables.getUtilisateur() + ".personnel", this.tables.getPersonnel() + ".numero")
			.join(this.tables.getPoste() + ".id", this.tables.getPersonnel() + ".poste");
		if (constraints.getSearch() != null && constraints.getSearch().getValue() != null) {
			query.iLike(this.tables.getPersonne() + ".nom", "%" + constraints.getSearch().getValue() + "%")
				.orILike("prenom", "%" + constraints.getSearch().getValue() + "%")
				.orILike(this.tables.getPoste() + ".nom", "%" + constraints.getSearch().getValue() + "%");
		}
		if (constraints.getOrderColumn() != -1) return query.orderBy(colonne[constraints.getOrderColumn()], constraints.getOrderDirection());
		else return query.orderBy(this.tables.getPoste() + ".nom, prenom", OrderBy.ASC);
	}
	
	@Override
	public ArrayList<TAutorisation> makeDatatable(PostgreSQL queryBuilder, DatatableParameter constraints) throws NoSpecifiedTableException, SQLException, NoConnectionException {
		ArrayList<TAutorisation> makeDatatable = new ArrayList<>();
		if (constraints.getLimitLength() != -1) queryBuilder.limit(constraints.getLimitLength(), constraints.getLimitStart());
		ResultSet resultSet = null;
		resultSet = queryBuilder.get().result();
		while (resultSet.next()) {
			TAutorisation autorisation = new TAutorisation();
			autorisation.setId(resultSet.getInt("id"));
			autorisation.setNom(resultSet.getString("nom") + " " + resultSet.getString("prenom") + " (" + resultSet.getString("nom_poste") + ")");
			autorisation.setList(this.selectAutorisations(resultSet.getInt("id_utilisateur")));
			makeDatatable.add(autorisation);
		}
		if (resultSet != null) resultSet.close();
		queryBuilder.close();
		return makeDatatable;
	}
	
	@Override
	public int dataRecordsTotal() throws SQLException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		return query.count(this.tables.getUtilisateur());
	}
	
	@Override
	public ArrayList<BAutorisation> selectAutorisations(int utilisateur) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		ArrayList<BAutorisation> select = new ArrayList<>();
		ResultSet resultSet = query.select(
			new String[]{
				"id",
				"menu"
			}
		).from(this.tables.getAutorisation()).where("utilisateur", utilisateur).get().result();
		while (resultSet.next()) {
			BAutorisation temporary = new BAutorisation();
			temporary.setId(resultSet.getInt("id"));
			temporary.setMenu(resultSet.getString("menu"));
			temporary.setUtilisateur(utilisateur);
		}
		return select;
	}
	
	@Override
	public ArrayList<BMenu> selectMenus(int utilisateur) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		ArrayList<BMenu> selectMenus = new ArrayList<>();
		ResultSet resultSet = query.select(
			new String[]{
				"nom",
				"menu_group",
				"lien"
			}
		)
			                      .from(this.tables.getMenu())
														.join(this.tables.getAutorisation() + ".menu", this.tables.getMenu() + ".lien")
			                      .where("utilisateur", utilisateur)
			                      .orderBy("menu_group", OrderBy.ASC)
			                      .get().result();
		while (resultSet.next()) {
			BMenu temporary = new BMenu();
			temporary.setNom(resultSet.getString("nom"));
			temporary.setLien(resultSet.getString("lien"));
			temporary.setGroup(resultSet.getString("menu_group"));
			selectMenus.add(temporary);
		}
		return selectMenus;
	}
	
	@Override
	public boolean clear(int utilisateur) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		return query.where("utilisateur", utilisateur).delete(this.tables.getAutorisation());
	}
	
	@Override
	public boolean add(int utilisateur, String menu) throws SQLException, InvalidExpressionException {
		PostgreSQL query = new PostgreSQL(this.dao.getConnection());
		return query.set("utilisateur", utilisateur).set("menu", menu).insert(this.tables.getAutorisation());
	}
}
