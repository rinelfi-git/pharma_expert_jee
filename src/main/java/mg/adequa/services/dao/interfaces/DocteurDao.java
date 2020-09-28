package mg.adequa.services.dao.interfaces;

import mg.adequa.payloadserialization.AchatPL;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;
import java.util.Map;

public interface DocteurDao {
	// Datatables
	String makeQuery(DatatableParameter constraint);
	ArrayList<Map> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	AchatPL select(int reference);
}
