package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BFournisseur;
import mg.adequa.payloads.PFournisseur;
import mg.adequa.tableviews.TFournisseur;
import mg.adequa.utils.DatatableParameter;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DFournisseur {
	ArrayList<TFournisseur> makeDatatable(DatatableParameter constraints) throws NoSpecifiedTableException, SQLException, NoConnectionException;
	int dataRecordsTotal() throws SQLException;
	ArrayList<PFournisseur> select() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	PFournisseur select(int id) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	boolean insert(BFournisseur fournisseur) throws SQLException, InvalidExpressionException;
	boolean update(int id, BFournisseur fournisseur) throws SQLException, InvalidExpressionException;
}
