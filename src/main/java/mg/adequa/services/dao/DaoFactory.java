package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.*;

import java.sql.Connection;
import java.sql.SQLException;

public interface DaoFactory {
	Connection getConnection() throws SQLException;
	DaoAchat getAchat();
	DaoDocteur getDocteur();
	DaoBilan getBilan();
	JournalEntreeSortieDao getJournalEntreeSortie();
	DaoPersonne getPersonne();
}
