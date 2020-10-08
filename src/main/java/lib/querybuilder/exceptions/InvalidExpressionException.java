package lib.querybuilder.exceptions;

public class InvalidExpressionException extends Exception {
	public InvalidExpressionException() {super("Expression invalide");}
	
	public InvalidExpressionException(String message) {super(message);}
}
