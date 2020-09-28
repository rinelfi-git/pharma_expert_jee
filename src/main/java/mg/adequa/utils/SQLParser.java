package mg.adequa.utils;

import java.sql.Date;

public class SQLParser {
	private Object value;
	public SQLParser() {}
	
	public String toString(String value) {
		return value.replaceAll("'", "''");
	}
}
