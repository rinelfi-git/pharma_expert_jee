package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BUtilisateur;

import java.sql.SQLException;

public interface DLogin {
	boolean urtilisateurExiste(String login) throws SQLException;
	String getPassword(String login) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	BUtilisateur getData(String login) throws SQLException, NoSpecifiedTableException, NoConnectionException;
}
