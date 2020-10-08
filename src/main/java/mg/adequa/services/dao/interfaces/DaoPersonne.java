package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BPersonne;

import java.sql.SQLException;

public interface DaoPersonne {
	boolean insert(BPersonne personne) throws Exception;
	int lastId() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	boolean update(BPersonne personne, int reference) throws Exception;
}
