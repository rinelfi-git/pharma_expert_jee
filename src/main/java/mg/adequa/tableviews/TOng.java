package mg.adequa.tableviews;

public class TOng {
	private int id;
	private String libelle, description, action;
	
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
	
	public String getLibelle() {
		return libelle;
	}
	
	public void setLibelle(String libelle) {
		this.libelle = libelle;
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
