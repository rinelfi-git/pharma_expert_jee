package mg.adequa.payloads;

import java.util.Date;

public class PNotification {
	private int limit, poste;
	private String title, message, starLevel, date;
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public int getPoste() {
		return poste;
	}
	
	public void setPoste(int poste) {
		this.poste = poste;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getStarLevel() {
		return starLevel;
	}
	
	public void setStarLevel(String starLevel) {
		this.starLevel = starLevel;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
}
