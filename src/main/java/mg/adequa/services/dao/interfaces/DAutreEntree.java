package mg.adequa.services.dao.interfaces;

import mg.adequa.payloads.PAutreEntree;
import mg.adequa.tableviews.AutreEntreeTV;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;

public interface DAutreEntree {
	// Datatables
	String makeQuery(DatatableParameter constraints);
	ArrayList<AutreEntreeTV> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	PAutreEntree select(int reference);
	boolean insert(PAutreEntree data);
	boolean insertAndArchive(PAutreEntree data);
}
