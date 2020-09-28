package mg.adequa.services.dao.models;

import mg.adequa.payloadserialization.AchatPL;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DocteurDao;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;
import java.util.Map;

public class DocteurModel implements DocteurDao {
	private DaoFactory daoFactory;
	
	public DocteurModel(DaoFactory daoFactory) {this.daoFactory = daoFactory;}
	
	@Override
	public String makeQuery(DatatableParameter constraint) {
		return null;
	}
	
	@Override
	public ArrayList<Map> makeDatatable(String query, DatatableParameter constraints) {
		return null;
	}
	
	@Override
	public int dataRecordsTotal() {
		return 0;
	}
	
	@Override
	public AchatPL select(int reference) {
		return null;
	}
}
