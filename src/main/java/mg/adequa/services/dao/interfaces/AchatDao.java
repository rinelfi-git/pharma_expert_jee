package mg.adequa.services.dao.interfaces;

import mg.adequa.payloadserialization.AchatPL;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;
import java.util.Map;

public interface AchatDao {
	// Datatables
	String makeQuery(DatatableParameter constraint);
	ArrayList<Map> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	AchatPL select(int reference);
	ArrayList<AchatPL> select();
	boolean insertOnly(AchatPL achatPL);
	boolean insertAndLog(AchatPL achatPL);
	boolean update(int reference, AchatPL achatPL);
}
