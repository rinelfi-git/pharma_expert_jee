package mg.adequa.services.dao.interfaces;

import lib.querybuilder.implementations.PostgreSQL;
import mg.adequa.beans.BDocteur;
import mg.adequa.payloads.PDocteur;
import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.tableviews.TDocteur;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DDocteur {
	// Datatables
	PostgreSQL makeQuery(DatatableParameter constraints) throws SQLException;
	
	ArrayList<TDocteur> makeDatatable(QueryBuilder query, DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	int dataRecordsTotal() throws NoSpecifiedTableException, SQLException, NoConnectionException;
	
	// Datatables
	PDocteur select(int reference) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	boolean insert(BDocteur docteur) throws Exception;
	
	boolean update(BDocteur docteur, int reference) throws Exception;
}
