package mg.adequa.services.dao.interfaces;

public interface DaoBilan {
	int getProduit();
	int getCaisse();
	int getBanque();
	boolean ajouterProduit(int montant);
	boolean ajouterCaisse(int montant);
	boolean ajouterBanque(int montant);
	boolean enleverProduit(int montant);
	boolean enleverCaisse(int montant);
	boolean enleverBanque(int montant);
}
