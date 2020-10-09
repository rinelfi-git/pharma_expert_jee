package mg.adequa.tableviews;

public class TDocteur {
	private int id;
	private String nomPrenom, service, action;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNomPrenom() {
		return nomPrenom.trim();
	}
	
	public void setNomPrenom(String nomPrenom) {
		this.nomPrenom = nomPrenom;
	}
	
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public String getAction() {
		return action = "<div class=\"btn-group-vertical\">" +
			                "<button class=\"btn btn-adequa modification\" data-toggle=\"modal\" data-target=\"#modifier\" data-identifiant=\"" + this.id + "\">Modifier</button>" +
			                "<!--<button class=\"btn btn-adequa suppression\" data-identifiant=\"" + this.id + "\">Supprimer</button>-->" +
			                "</div>";
	}
}
