package mg.adequa.utils;

public class DatatableSearch {
	private String value;
	private boolean regex;
	
	public DatatableSearch() {
		this.value = null;
		this.regex = false;
	}
	
	public DatatableSearch(String value, boolean regex) {
		this.value = value;
		this.regex = regex;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean hasRegex() {
		return regex;
	}
	
	public void setRegex(boolean regex) {
		this.regex = regex;
	}
}
