package mg.adequa.utils;

public class UriUtils {
	String uri;
	
	public UriUtils() {}
	
	public UriUtils(String uri) {this.uri = uri;}
	
	public String[] toArray() {
		String[] tmp = this.uri.split("/");
		if(tmp[0].equals("")) return this.removeSegmentAt(0, tmp);
		else return tmp;
	}
	
	private String[] removeSegmentAt(int index, String[] array) {
		String[] output = new String[array.length - 1];
		for(int inputCounter = 0, outputCounter = 0; inputCounter < array.length; inputCounter++) {
			if(inputCounter != index) {
				output[outputCounter] = array[inputCounter];
				outputCounter++;
			}
		}
		return output;
	}
	
	public String getLastSegment() {
		return this.toArray()[this.toArray().length - 1];
	}
}
