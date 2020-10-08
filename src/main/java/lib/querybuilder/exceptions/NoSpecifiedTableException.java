package lib.querybuilder.exceptions;

public class NoSpecifiedTableException extends Exception {
	public NoSpecifiedTableException() {
		super("Votre requête manque une table");
	}
}
