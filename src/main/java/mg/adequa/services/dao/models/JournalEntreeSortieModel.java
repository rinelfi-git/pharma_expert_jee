package mg.adequa.services.dao.models;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;

public class JournalEntreeSortieModel implements JournalEntreeSortieDao{
	private DaoFactory daoFactory;
	
	public JournalEntreeSortieModel(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
}
