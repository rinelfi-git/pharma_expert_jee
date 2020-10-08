package mg.adequa.utils.jwt;

public class Session<E> {
	private String id, type, dateDeCreationString, dateExpirationString;
	private E contenu;
	private long dateDeCreation, dateExpiration;
	
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
	
	public String getDateDeCreationString() {
		return dateDeCreationString;
	}
	
	public void setDateDeCreationString(String dateDeCreationString) {
		this.dateDeCreationString = dateDeCreationString;
	}
	
	public String getDateExpirationString() {
		return dateExpirationString;
	}
	
	public void setDateExpirationString(String dateExpirationString) {
		this.dateExpirationString = dateExpirationString;
	}
	
	public E getContenu() {
		return contenu;
	}
	
	public void setContenu(E contenu) {
		this.contenu = contenu;
	}
	
	public long getDateDeCreation() {
		return dateDeCreation;
	}
	
	public void setDateDeCreation(long dateDeCreation) {
		this.dateDeCreation = dateDeCreation;
	}
	
	public long getDateExpiration() {
		return dateExpiration;
	}
	
	public void setDateExpiration(long dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
}
