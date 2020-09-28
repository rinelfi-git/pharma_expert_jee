package mg.adequa.services.dao;

import mg.adequa.services.dao.interfaces.AchatDao;
import mg.adequa.services.dao.interfaces.BilanDao;
import mg.adequa.services.dao.interfaces.DocteurDao;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;

import java.sql.Connection;
import java.sql.SQLException;

public interface DaoFactory {
	Connection getConnection() throws SQLException;
	AchatDao getAchatDao();
	DocteurDao getDocteurDao();
	BilanDao getBilanDao();
	JournalEntreeSortieDao getJournalEntreeSortieDao();
}
