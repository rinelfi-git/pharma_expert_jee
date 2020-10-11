package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.*;

import java.sql.Connection;
import java.sql.SQLException;

public interface DaoFactory {
	Connection getConnection() throws SQLException;
	
	DAchat getAchat();
	
	DDocteur getDocteur();
	
	DBilan getBilan();
	
	JournalEntreeSortieDao getJournalEntreeSortie();
	
	DPersonne getPersonne();
	
	DSession getSession();
	
	DLogin getLogin();
	
	DJournalDeSession getJournalDeSession();
	
	DNotification getNotification();
	
	DAutorisation getAutorisation();
	
	DMenu getMenu();
	
	DFournisseur getFournisseur();
	DOng getOng();
	DPersonneOng getPersonneOng();
}
