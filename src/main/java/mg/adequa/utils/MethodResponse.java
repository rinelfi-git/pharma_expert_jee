package mg.adequa.utils;

import java.util.ArrayList;

public class MethodResponse {
	private boolean status;
	private String message;
	private ArrayList<String> tables;
	
	public MethodResponse() {
		this.status = true;
		this.message = "";
		this.tables = new ArrayList<>();
	}
	
	public MethodResponse setRequestState(boolean state) {
		this.status = state;
		return this;
	}
	
	public boolean hasRequestSuccess() {
		return this.status;
	}
	
	public MethodResponse appendTable(String table) {
		this.tables.add(table);
		return this;
	}
	
	public MethodResponse validate() {
		if (!this.status) {
			this.message = "Des erreurs ont pu être trouvés dans les tables suivantes : {\n";
			int tableLength = this.tables.size();
			for (String table : this.tables) {
				this.message += "  -  [" + table + "];\n";
			}
			this.message = this.message.substring(0, this.message.length() - 2);
			this.message += "\n}\n";
		} else this.message = "Aucune erreur n'a été trouvé";
		return this;
	}
}
