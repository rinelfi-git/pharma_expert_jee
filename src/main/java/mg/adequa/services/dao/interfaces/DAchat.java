package mg.adequa.services.dao.interfaces;

import mg.adequa.payloads.PAchat;
import mg.adequa.utils.DatatableParameter;

import java.util.ArrayList;
import java.util.Map;

public interface DAchat {
	// Datatables
	String makeQuery(DatatableParameter constraints);
	ArrayList<Map> makeDatatable(String query, DatatableParameter constraints);
	int dataRecordsTotal();
	// Datatables
	PAchat select(int reference);
	ArrayList<PAchat> select();
	boolean insertOnly(PAchat pAchat);
	boolean insertAndLog(PAchat pAchat);
	boolean update(int reference, PAchat pAchat);
}
