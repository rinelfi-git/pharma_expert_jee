package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BSession;
import mg.adequa.beans.BUtilisateur;

import java.sql.SQLException;

public interface DSession {
	boolean insert(BSession<BUtilisateur> session) throws Exception;
	boolean delete(String id) throws SQLException, InvalidExpressionException;
	boolean exists(String id) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	boolean addTimer(String id) throws SQLException, InvalidExpressionException;
	BSession<BUtilisateur> get(String id) throws SQLException, NoSpecifiedTableException, NoConnectionException;
}
