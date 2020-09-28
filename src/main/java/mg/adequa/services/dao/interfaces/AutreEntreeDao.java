package mg.adequa.services.dao.interfaces;

import mg.adequa.payloadserialization.AutreEntreePL;
import mg.adequa.tableviews.AutreEntreeTV;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;

public interface AutreEntreeDao {
	// Datatables
	String makeQuery(DatatableParameter constraints);
	ArrayList<AutreEntreeTV> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	AutreEntreePL select(int reference);
	boolean insert(AutreEntreePL data);
	boolean insertAndArchive(AutreEntreePL data);
}
