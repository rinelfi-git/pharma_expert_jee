package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BOng;
import mg.adequa.payloads.POng;
import mg.adequa.tableviews.TOng;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DOng {
	ArrayList<TOng> makeDatatable(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	int dataRecordsTotal() throws SQLException;
	boolean insert(BOng ong) throws SQLException, InvalidExpressionException;
	boolean update(int id, BOng ong) throws SQLException, InvalidExpressionException;
	ArrayList<POng> select() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	POng select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException;
}
