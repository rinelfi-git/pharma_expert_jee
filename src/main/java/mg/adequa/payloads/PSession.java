package mg.adequa.payloads;

import java.util.Date;

public class PSession<C> {
	private String id, type;
	private Date dateCreation, dateExpiration;
	private C contenu;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Date getDateCreation() {
		return dateCreation;
	}
	
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	public Date getDateExpiration() {
		return dateExpiration;
	}
	
	public void setDateExpiration(Date dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
	
	public C getContenu() {
		return contenu;
	}
	
	public void setContenu(C contenu) {
		this.contenu = contenu;
	}
}
