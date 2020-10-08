package lib.querybuilder.exceptions;

public class NoConnectionException extends Exception{
	public NoConnectionException() {
		super("Aucune connexion n'est ouvert");
	}
}
