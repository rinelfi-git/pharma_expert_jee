package mg.adequa.payloads;

public class PlEvolutionDeCompteFinancierConstraint {
	private int moisDebut, anneeDebut, moisFin, anneeFin;
	
	public PlEvolutionDeCompteFinancierConstraint() {
		this.moisDebut = 0;
		this.anneeDebut = 0;
		this.moisFin = 0;
		this.anneeFin = 0;
	}
	
	public int getMoisDebut() {
		return moisDebut;
	}
	
	public void setMoisDebut(int moisDebut) {
		this.moisDebut = moisDebut;
	}
	
	public int getAnneeDebut() {
		return anneeDebut;
	}
	
	public void setAnneeDebut(int anneeDebut) {
		this.anneeDebut = anneeDebut;
	}
	
	public int getMoisFin() {
		return moisFin;
	}
	
	public void setMoisFin(int moisFin) {
		this.moisFin = moisFin;
	}
	
	public int getAnneeFin() {
		return anneeFin;
	}
	
	public void setAnneeFin(int anneeFin) {
		this.anneeFin = anneeFin;
	}
}
