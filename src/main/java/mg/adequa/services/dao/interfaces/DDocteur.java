package mg.adequa.services.dao.interfaces;

import mg.adequa.beans.BDocteur;
import mg.adequa.payloads.PDocteur;
import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.tableviews.TvDocteur;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DDocteur {
	// Datatables
	QueryBuilder makeQuery(DatatableParameter constraints) throws SQLException;
	
	ArrayList<TvDocteur> makeDatatable(QueryBuilder query, DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	int dataRecordsTotal() throws NoSpecifiedTableException, SQLException, NoConnectionException;
	
	// Datatables
	PDocteur select(int reference) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	boolean insert(BDocteur docteur) throws Exception;
	
	boolean update(BDocteur docteur, int reference) throws Exception;
}
