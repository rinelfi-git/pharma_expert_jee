package mg.adequa.tableviews;

public class TFournisseur {
	private int id;
	private String nom, localisation, description, action;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
		StringBuilder builder = new StringBuilder("<div class=\"btn-group-vertical\">");
		builder.append("<button class=\"btn btn-adequa modification\" data-toggle=\"modal\" data-target=\"#modifier\" data-identifiant=\"" + this.id + "\">Modifier</button>")
			.append("</div>");
		this.action = builder.toString();
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAction() {
		return action;
	}
}
