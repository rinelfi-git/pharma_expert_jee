package mg.adequa.tableviews;

public class AutreEntreeTV {
	private String dateOperation, libelleOperation, numeroDePieceJustificative, observation, action;
	private int id, somme;
	
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
	
	public String getAction() {
		this.action = "<div class=\"btn-group-vertical\">\n" +
			              "<button class=\"btn btn-adequa modification\" data-toggle=\"modal\" data-target=\"#modifier\" data-identifiant=\"" + this.id + "\">Modifier</button>\n" +
			              "<!--<button class=\"btn btn-adequa suppression\" data-identifiant=\"" + this.id + "\">Supprimer</button>-->\n" +
			              "</div>";
		return action;
	}
	
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
}
