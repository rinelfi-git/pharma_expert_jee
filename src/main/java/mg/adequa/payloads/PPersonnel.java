package mg.adequa.payloads;

public class PPersonnel extends PPersonne {
	protected String email, numero, nomPoste, nomDirection;
	protected int direction, poste;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getPoste() {
		return poste;
	}
	
	public void setPoste(int poste) {
		this.poste = poste;
	}
	
	public String getNomPoste() {
		return nomPoste;
	}
	
	public void setNomPoste(String nomPoste) {
		this.nomPoste = nomPoste;
	}
	
	public String getNomDirection() {
		return nomDirection;
	}
	
	public void setNomDirection(String nomDirection) {
		this.nomDirection = nomDirection;
	}
}
