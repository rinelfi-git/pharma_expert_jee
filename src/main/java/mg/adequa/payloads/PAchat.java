package mg.adequa.payloads;

public class PAchat {
	private int id, facture, somme;
	private String dateOperation, libelle, modeDePayement, referece, dateEcheance;
	private boolean enAttente;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getFacture() {
		return facture;
	}
	
	public void setFacture(int facture) {
		this.facture = facture;
	}
	
	public int getSomme() {
		return somme;
	}
	
	public void setSomme(int somme) {
		this.somme = somme;
	}
	
	public String getDateOperation() {
		return dateOperation;
	}
	
	public void setDateOperation(String dateOperation) {
		this.dateOperation = dateOperation;
	}
	
	public String getLibelle() {
		return libelle;
	}
	
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public String getModeDePayement() {
		return modeDePayement;
	}
	
	public void setModeDePayement(String modeDePayement) {
		this.modeDePayement = modeDePayement;
	}
	
	public String getReferece() {
		return referece;
	}
	
	public void setReferece(String referece) {
		this.referece = referece;
	}
	
	public String getDateEcheance() {
		return dateEcheance;
	}
	
	public void setDateEcheance(String dateEcheance) {
		this.dateEcheance = dateEcheance;
	}
	
	public boolean isEnAttente() {
		return enAttente;
	}
	
	public void setEnAttente(boolean enAttente) {
		this.enAttente = enAttente;
	}
}
