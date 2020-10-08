package mg.adequa.payloads;

public class PlAutreEntree {
	private String dateOperation, modeDePayment, libelleOperation, numeroDePieceJustificative, observation;
	private boolean libelleEnregistre;
	private int somme;
	
	public String getDateOperation() {
		return dateOperation;
	}
	
	public void setDateOperation(String dateOperation) {
		this.dateOperation = dateOperation;
	}
	
	public String getModeDePayment() {
		return modeDePayment;
	}
	
	public void setModeDePayment(String modeDePayment) {
		this.modeDePayment = modeDePayment;
	}
	
	public String getLibelleOperation() {
		return libelleOperation;
	}
	
	public void setLibelleOperation(String libelleOperation) {
		this.libelleOperation = libelleOperation;
	}
	
	public boolean isLibelleEnregistre() {
		return libelleEnregistre;
	}
	
	public void setLibelleEnregistre(boolean libelleEnregistre) {
		this.libelleEnregistre = libelleEnregistre;
	}
	
	public int getSomme() {
		return somme;
	}
	
	public void setSomme(int somme) {
		this.somme = somme;
	}
	
	public String getNumeroDePieceJustificative() {
		return numeroDePieceJustificative;
	}
	
	public void setNumeroDePieceJustificative(String numeroDePieceJustificative) {
		this.numeroDePieceJustificative = numeroDePieceJustificative;
	}
	
	public String getObservation() {
		return observation;
	}
	
	public void setObservation(String observation) {
		this.observation = observation;
	}
}
