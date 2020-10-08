package lib.querybuilder.clauses;

public class Limit {
	private int limit, offset;
	
	public Limit(int limit) {
		this.limit = limit;
		this.offset = 0;
	}
	
	public Limit(int limit, int offset) {
		this.limit = limit;
		this.offset = offset;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public int getOffset() {
		return offset;
	}
}
