package lib.querybuilder.clauses;

public class OrderBy {
	public static String ASC = "ASC";
	public static String DESC = "DESC";
	private String field, ordering;
	
	public OrderBy(String field, String ordering) {
		this.field = field;
		this.ordering = ordering;
	}
	
	public OrderBy(String field) {
		this.field = field;
		this.ordering = OrderBy.ASC;
	}
	
	public String getField() {
		return field;
	}
	
	public String getOrdering() {
		return ordering;
	}
}
