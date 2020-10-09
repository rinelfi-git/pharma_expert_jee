package mg.adequa.services.dao.interfaces;

import mg.adequa.tableviews.TJournalEntreeSortie;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;

public interface JournalEntreeSortieDao {
	// Datatables
	String makeEntreeQuery(DatatableParameter constraints);
	
	String makeSortieQuery(DatatableParameter constraints);
	
	ArrayList<TJournalEntreeSortie> makeDatatable(String query, DatatableParameter constraints);
	
	int dataRecordsTotalSortie();
	int dataRecordsTotalEntree();
	
	// Datatables
	int sommeEntrees(int mois, int annee);
	
	int sommeSorties(int mois, int annee);
}
