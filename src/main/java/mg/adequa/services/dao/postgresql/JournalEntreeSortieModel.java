package mg.adequa.services.dao.postgresql;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;

public class JournalEntreeSortieModel implements JournalEntreeSortieDao{
	private DaoFactory daoFactory;
	
	public JournalEntreeSortieModel(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	@Override
	public int sommeEntrees(int mois, int annee) {
		return 0;
	}
	
	@Override
	public int sommeSorties(int mois, int annee) {
		return 0;
	}
}
