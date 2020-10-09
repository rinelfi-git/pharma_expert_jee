package mg.adequa.payloads;

import mg.adequa.beans.BMenu;

public class PAutorisation {
	private int utilisateur;
	private BMenu[] menus;
	
	public int getUtilisateur() {
		return utilisateur;
	}
	
	public void setUtilisateur(int utilisateur) {
		this.utilisateur = utilisateur;
	}
	
	public BMenu[] getMenus() {
		return menus;
	}
	
	public void setMenus(BMenu[] menus) {
		this.menus = menus;
	}
}
