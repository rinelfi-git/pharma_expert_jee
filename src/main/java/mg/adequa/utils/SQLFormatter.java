package mg.adequa.utils;

public class SQLFormatter {
	public static String escapeQuote(String param0) {
		return param0.replaceAll("'", "''");
	}
}
