package mg.adequa.beans;

public class BPersonnel extends BPersonne{
	protected String email, numero;
	protected int direction, poste;
	
	public String getNumero() {
		return numero;
	}
	
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
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
}
