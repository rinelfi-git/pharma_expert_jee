package mg.adequa.utils;

public class DatatableColumn {
	private int data;
	private String name;
	private boolean searchable, orderable;
	
	public int getData() {
		return data;
	}
	
	public void setData(int data) {
		this.data = data;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSearchable() {
		return searchable;
	}
	
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	
	public boolean isOrderable() {
		return orderable;
	}
	
	public void setOrderable(boolean orderable) {
		this.orderable = orderable;
	}
}
