package mg.adequa.services.dao.interfaces;

public interface BilanDao {
	int getProduit();
	int getCaisse();
	int getBanque();
	int ajouterProduit(int montant);
	int ajouterCaisse(int montant);
	int ajouterBanque(int montant);
	int enleverProduit(int montant);
	int enleverCaisse(int montant);
	int enleverBanque(int montant);
}
