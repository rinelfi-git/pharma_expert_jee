package lib.querybuilder.clauses;

public class Join {
	public static final String INNER = "INNER";
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "INNER";
	private String joinType, table1, table2, key1, key2;
	
	public Join() {
		this.joinType = "INNER";
	}
	
	public Join(String joinType, String table1, String table2, String key1, String key2) {
		this.joinType = joinType;
		this.table1 = table1;
		this.table2 = table2;
		this.key1 = key1;
		this.key2 = key2;
	}
	
	public Join(String table1, String table2, String key1, String key2) {
		this.joinType = "INNER";
		this.table1 = table1;
		this.table2 = table2;
		this.key1 = key1;
		this.key2 = key2;
	}
	
	@Override
	public String toString() {
		return this.joinType + " JOIN " + this.table1 + " ON " + this.table1 + "." + this.key1 + "=" + this.table2 + "." + this.key2;
	}
	
	public boolean hasNull() {
		return this.table2 == null || this.table1 == null || this.key1 == null || this.key2 == null || this.table2.equals("") || this.table1.equals("") || this.key1.equals("") || this.key2.equals("");
	}
}
