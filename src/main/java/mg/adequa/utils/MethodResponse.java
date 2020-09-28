package mg.adequa.utils;

public class MethodResponse {
	private boolean requestSuccess;
	private String message;
	private String[] tables;
	
	public MethodResponse() {
		this.requestSuccess = true;
		this.message = "";
		this.tables = new String[]{};
	}
	
	public MethodResponse setRequestState(boolean state) {
		this.requestSuccess = state;
		return this;
	}
	
	public boolean hasRequestSuccess() {
		return this.requestSuccess;
	}
	
	public MethodResponse appendTable(String table) {
		final int oldLength = this.tables.length;
		String[] oldTables = this.tables.clone();
		this.tables = new String[oldLength + 1];
		for(int i = 0; i < (oldLength + 1); i++) {
			this.tables[i] = oldTables[i];
		}
		this.tables[oldLength] = table;
		return this;
	}
	
	public MethodResponse validate() {
		if(!this.requestSuccess) {
			this.message = "Des erreurs ont pu être trouvés dans les tables suivantes : {\n";
			int tableLength = this.tables.length;
			for(int i = 0; i < tableLength; i++) {
				this.message += "  -  [" + this.tables[i] + "];\n";
			}
			this.message = this.message.substring(0, this.message.length() - 2);
			this.message += "\n}\n";
		}
		else this.message = "Aucune erreur n'a été trouvé";
		return this;
	}
}
