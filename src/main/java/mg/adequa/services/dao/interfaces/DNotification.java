package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.payloads.PNotification;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DNotification {
	ArrayList<PNotification> getLastimited(int limit, int poste) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	ArrayList<PNotification> selectAll(int poste) throws NoSpecifiedTableException, SQLException, NoConnectionException;
	boolean clean(int poste) throws SQLException, InvalidExpressionException;
	int countNew(int poste) throws NoConnectionException, SQLException, NoSpecifiedTableException;
}
