package mg.adequa.services.dao.interfaces;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BDocteur;
import mg.adequa.beans.BJournalDeSession;
import mg.adequa.payloads.PDocteur;
import mg.adequa.tableviews.TJournalDeSession;
import mg.adequa.tableviews.TvDocteur;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DJournalDeSession {
	// Datatables
	QueryBuilder makeQuery(DatatableParameter constraints) throws SQLException;
	
	ArrayList<TJournalDeSession> makeDatatable(QueryBuilder query, DatatableParameter constraints) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	int dataRecordsTotal() throws NoSpecifiedTableException, SQLException, NoConnectionException;
	
	// Datatables
	
	boolean insert(BJournalDeSession journal) throws Exception;
	
	boolean delete() throws Exception;
}
