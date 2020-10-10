package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BMenu;
import mg.adequa.dbentity.DbTables;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DMenu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MMenu implements DMenu {
	private DaoFactory dao;
	private DbTables tables;
	
	public MMenu(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	@Override
	public ArrayList<String> selectGroup() throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQL(this.dao.getConnection());
		ArrayList<String> selectGroup = new ArrayList<>();
		ResultSet resultSet = query.select().from(this.tables.getMenuGroup()).get().result();
		while (resultSet.next()) selectGroup.add(resultSet.getString("nom"));
		return  selectGroup;
	}
	
	@Override
	public String selectMenuGroupOf(String lien) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQL(this.dao.getConnection());
		String selectMenuGroupOf = null;
		ResultSet resultSet = query.select("menu_group").from(this.tables.getMenu()).where("lien", lien).get().result();
		if (resultSet.next()) selectMenuGroupOf = resultSet.getString("menu_group");
		return  selectMenuGroupOf;
	}
	
	@Override
	public BMenu selectMenuOfAutorisation(String lien) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		QueryBuilder query = new PostgreSQL(this.dao.getConnection());
		BMenu selectMenuOf = null;
		ResultSet resultSet = query.select(new String[]{"menu_group", "nom"}).from(this.tables.getMenu()).where("lien", lien).get().result();
		if (resultSet.next()){
			selectMenuOf = new BMenu();
			selectMenuOf.setGroup(resultSet.getString("menu_group"));
			selectMenuOf.setLien(lien);
			selectMenuOf.setNom(resultSet.getString("nom"));
		}
		return  selectMenuOf;
	}
}
