package mg.adequa.services.dao.postgresql;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.BilanDao;

public class BilanModel implements BilanDao {
	private DaoFactory daoFactory;
	
	public BilanModel(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	@Override
	public int getProduit() {
		return 0;
	}
	
	@Override
	public int getCaisse() {
		return 0;
	}
	
	@Override
	public int getBanque() {
		return 0;
	}
	
	@Override
	public int ajouterProduit(int montant) {
		return 0;
	}
	
	@Override
	public int ajouterCaisse(int montant) {
		return 0;
	}
	
	@Override
	public int ajouterBanque(int montant) {
		return 0;
	}
	
	@Override
	public int enleverProduit(int montant) {
		return 0;
	}
	
	@Override
	public int enleverCaisse(int montant) {
		return 0;
	}
	
	@Override
	public int enleverBanque(int montant) {
		return 0;
	}
}
