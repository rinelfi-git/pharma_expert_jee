package mg.adequa.utils;

public class NumberFormat {
	private String data;
	
	public NumberFormat(int number) {this.data = String.valueOf(number);}
	
	public NumberFormat(String number) {this.data = number;}
	
	public String format(String separateurDeMillier) {
		StringBuilder builder = new StringBuilder();
		for (int i = (this.data.length() - 1), counter = 0; i >= 0; i--, counter++) {
			if (counter % 3 == 0 && counter > 0) builder.insert(0, this.data.charAt(i) + separateurDeMillier);
			else builder.insert(0, this.data.charAt(i));
		}
		return builder.toString();
	}
	
	public String format() {
		StringBuilder builder = new StringBuilder();
		for (int i = (this.data.length() - 1), counter = 0; i >= 0; i--, counter++) {
			if (counter % 3 == 0 && counter > 0) builder.insert(0, this.data.charAt(i) + ",");
			else builder.insert(0, this.data.charAt(i));
		}
		return builder.toString();
	}
}
