package mg.adequa.utils;

public class DatatableParameter {
	private int draw, orderColumn, limitStart, limitLength;
	private DatatableColumn[] columns;
	private String orderDirection;
	private DatatableSearch search;
	
	public DatatableParameter() {
		this.draw = -1;
		this.orderColumn = -1;
		this.limitStart = -1;
		this.limitLength = -1;
		this.columns = null;
		this.orderDirection = null;
		this.search = null;
	}
	
	public int getDraw() {
		return draw;
	}
	
	public void setDraw(int draw) {
		this.draw = draw;
	}
	
	public int getOrderColumn() {
		return orderColumn;
	}
	
	public void setOrderColumn(int orderColumn) {
		this.orderColumn = orderColumn;
	}
	
	public int getLimitStart() {
		return limitStart;
	}
	
	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}
	
	public int getLimitLength() {
		return limitLength;
	}
	
	public void setLimitLength(int limitLength) {
		this.limitLength = limitLength;
	}
	
	public DatatableColumn[] getColumns() {
		return columns;
	}
	
	public void setColumns(DatatableColumn[] columns) {
		this.columns = columns;
	}
	
	public String getOrderDirection() {
		return orderDirection;
	}
	
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	
	public DatatableSearch getSearch() {
		return search;
	}
	
	public void setSearch(DatatableSearch search) {
		this.search = search;
	}
}
