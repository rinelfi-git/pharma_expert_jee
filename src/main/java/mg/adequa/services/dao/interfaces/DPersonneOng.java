package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.dbentity.BPersonneOng;
import mg.adequa.payloads.PPersonneOng;
import mg.adequa.tableviews.TPersonneOng;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DPersonneOng {
	ArrayList<TPersonneOng> makeDatatable(DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	int dataRecordsTotal() throws SQLException;
	boolean insert(BPersonneOng personneOng) throws SQLException, InvalidExpressionException;
	boolean update(int id, BPersonneOng personneOng) throws SQLException, InvalidExpressionException;
	ArrayList<PPersonneOng> select() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	PPersonneOng select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException;
}
