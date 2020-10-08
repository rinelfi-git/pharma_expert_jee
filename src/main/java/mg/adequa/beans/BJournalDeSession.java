package mg.adequa.beans;

import java.util.Date;

public class BJournalDeSession {
	private int id, comptePersonnel;
	private String action;
	private Date date;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getComptePersonnel() {
		return comptePersonnel;
	}
	
	public void setComptePersonnel(int comptePersonnel) {
		this.comptePersonnel = comptePersonnel;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
}
