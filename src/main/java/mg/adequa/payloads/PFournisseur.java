package mg.adequa.payloads;

public class PFournisseur {
	private int id;
	private String nom, localisation, informationComplementaire;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getLocalisation() {
		return localisation;
	}
	
	public void setLocalisation(String localisation) {
		this.localisation = localisation;
	}
	
	public String getInformationComplementaire() {
		return informationComplementaire;
	}
	
	public void setInformationComplementaire(String informationComplementaire) {
		this.informationComplementaire = informationComplementaire;
	}
}
