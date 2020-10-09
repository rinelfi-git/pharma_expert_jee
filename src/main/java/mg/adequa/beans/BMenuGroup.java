package mg.adequa.beans;

import java.util.ArrayList;

public class BMenuGroup {
	private String nom;
	private ArrayList<BMenu> menus;
	
	public BMenuGroup() {
		this.menus = new ArrayList<>();
	}
	
	public BMenuGroup(String nom, ArrayList<BMenu> menus) {
		this.nom = nom;
		this.menus = menus;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public ArrayList<BMenu> getMenus() {
		return menus;
	}
	
	public void setMenus(ArrayList<BMenu> menus) {
		this.menus = menus;
	}
}
