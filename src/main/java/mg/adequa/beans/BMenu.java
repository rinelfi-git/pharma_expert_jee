package mg.adequa.beans;

public class BMenu {
	private String nom, lien, group;
	
	public BMenu(String nom, String lien, String group) {
		this.nom = nom;
		this.lien = lien;
		this.group = group;
	}
	
	public BMenu() {}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getLien() {
		return lien;
	}
	
	public void setLien(String lien) {
		this.lien = lien;
	}
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
}
