package mg.adequa.tableviews;

public class JournalEntreeSortieTV {
	private int id, somme;
	private String dateOperation, libelleOperation, pieceJustificative, observation;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
	
	public String getLibelleOperation() {
		return libelleOperation;
	}
	
	public void setLibelleOperation(String libelleOperation) {
		this.libelleOperation = libelleOperation;
	}
	
	public String getPieceJustificative() {
		return pieceJustificative;
	}
	
	public void setPieceJustificative(String pieceJustificative) {
		this.pieceJustificative = pieceJustificative;
	}
	
	public String getObservation() {
		return observation;
	}
	
	public void setObservation(String observation) {
		this.observation = observation;
	}
}
