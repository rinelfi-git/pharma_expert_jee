package mg.adequa.tableviews;

import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BAutorisation;
import mg.adequa.beans.BMenu;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DMenu;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class TAutorisation {
	private static int round;
	private int id, utilisateur;
	private String action, nom, list;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getList() {
		return list;
	}
	
	public int getUtilisateur() {
		return utilisateur;
	}
	
	public void setUtilisateur(int utilisateur) {
		this.utilisateur = utilisateur;
	}
	
	public void setList(ArrayList<BAutorisation> autorisations) throws NoConnectionException, SQLException, NoSpecifiedTableException {
		int count = TAutorisation.round++;
		StringBuilder formattedAutorisation = new StringBuilder("<div id=\"accordion" + count + "\">");
		String lastMenuGroup = "";
		StringBuilder collapseContent = new StringBuilder();
		DaoFactory dao = PostgreSQL.getInstance();
		DMenu menu = dao.getMenu();
		for (BAutorisation autorisation : autorisations) {
			BMenu menuInfo = menu.selectMenuOfAutorisation(autorisation.getMenu());
			if (!menuInfo.getGroup().equals(lastMenuGroup)) {
				if (collapseContent.toString().length() > 0) collapseContent.append("</ul></div></div></div>");
				formattedAutorisation.append(collapseContent.toString())
				.append("<div class=\"card card-adequat\">")
				.append("<div class=\"card-header\" id=\"heading" + DigestUtils.sha1(menuInfo.getGroup()) + count + "\">")
				.append("<button class=\"btn btn-adequa\" data-toggle=\"collapse\" data-target=\"#collapse" + DigestUtils.sha1(menuInfo.getGroup()) + count + "\" aria-expanded=\"false\" aria-controls=\"collapse" + DigestUtils.sha1(menuInfo.getGroup()) + count + "\">" + menuInfo.getGroup() + "</button>")
				.append("</div>")
				.append("<div id=\"collapse" + DigestUtils.sha1(menuInfo.getGroup()) + count + "\" class=\"collapse\" aria-labelledby=\"heading"  + DigestUtils.sha1(menuInfo.getGroup()) + count + "\" data-parent=\"#accordion" + count + "\">")
				.append("<div class=\"card-body\">")
				.append("<ul class=\"list-group\">");
				collapseContent.append("<li class=\"list-group-item\">" + menuInfo.getNom() + "</li>");
				lastMenuGroup = menuInfo.getGroup();
			} else {
				collapseContent.append("<li class=\"list-group-item\">" + menuInfo.getNom() + "</li>");
			}
		}
		collapseContent.append("</ul></div></div></div></div>");
		this.list = formattedAutorisation.append(collapseContent.toString()).toString();
	}
}
