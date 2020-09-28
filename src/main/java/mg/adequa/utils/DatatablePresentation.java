package mg.adequa.utils;

import java.util.ArrayList;

public class DatatablePresentation<T> {
	private int draw, recordsTotal, recordsFiltered;
	private ArrayList<T> data;
	
	public int getDraw() {
		return draw;
	}
	
	public void setDraw(int draw) {
		this.draw = draw;
	}
	
	public int getRecordsTotal() {
		return recordsTotal;
	}
	
	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}
	
	public int getRecordsFiltered() {
		return recordsFiltered;
	}
	
	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
	public ArrayList<T> getData() {
		return data;
	}
	
	public void setData(ArrayList<T> data) {
		this.data = data;
	}
}
