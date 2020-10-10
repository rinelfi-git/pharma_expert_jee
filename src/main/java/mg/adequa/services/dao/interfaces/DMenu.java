package mg.adequa.services.dao.interfaces;

import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BMenu;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DMenu {
	ArrayList<String> selectGroup() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	String selectMenuGroupOf(String lien) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	BMenu selectMenuOfAutorisation(String lien) throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	ArrayList<BMenu> selectMenuOfGroup(String group) throws SQLException, NoSpecifiedTableException, NoConnectionException;
}
