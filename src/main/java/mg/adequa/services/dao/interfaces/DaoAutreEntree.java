package mg.adequa.services.dao.interfaces;

import mg.adequa.payloads.PlAutreEntree;
import mg.adequa.tableviews.AutreEntreeTV;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;

public interface DaoAutreEntree {
	// Datatables
	String makeQuery(DatatableParameter constraints);
	ArrayList<AutreEntreeTV> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	PlAutreEntree select(int reference);
	boolean insert(PlAutreEntree data);
	boolean insertAndArchive(PlAutreEntree data);
}
