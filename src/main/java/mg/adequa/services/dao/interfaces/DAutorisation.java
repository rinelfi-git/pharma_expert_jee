package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BAutorisation;
import mg.adequa.beans.BMenu;
import mg.adequa.tableviews.TAutorisation;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DAutorisation {
	PostgreSQL makeQuery(DatatableParameter constraints) throws SQLException;
	
	ArrayList<TAutorisation> makeDatatable(PostgreSQL queryBuilder, DatatableParameter constraints) throws NoSpecifiedTableException, SQLException, NoConnectionException;
	
	int dataRecordsTotal() throws SQLException;
	
	ArrayList<BAutorisation> selectAutorisations(int utilisateur) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	ArrayList<BMenu> selectMenus(int utilisateur) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	boolean clear(int utilisateur) throws SQLException, InvalidExpressionException;
	boolean add(int utilisateur, String menu) throws SQLException, InvalidExpressionException;
}
