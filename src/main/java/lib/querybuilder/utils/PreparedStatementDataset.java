package lib.querybuilder.utils;

import java.sql.PreparedStatement;

public class PreparedStatementDataset<T> {
	private T value;
	
	public PreparedStatementDataset() {
	}
	
	public PreparedStatementDataset(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
}
