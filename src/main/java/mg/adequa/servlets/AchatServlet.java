package mg.adequa.servlets;

import com.google.gson.Gson;
import mg.adequa.payloadserialization.AchatPL;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.interfaces.AchatDao;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AchatServlet extends HttpServlet {
	private DaoFactory daoFactory;
	private AchatDao achatDao;
	
	public AchatServlet() {super();}
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.achatDao = this.daoFactory.getAchatDao();
	}
	
	public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (uriUtils.getLastSegment()) {
			case "make_datatable":
				response.setContentType("application/json");
				response.getWriter().print(new Gson().toJson(this.makeDatatable(request)));
				break;
			case "insert":
				response.setContentType("application/json");
				response.getWriter().print(new Gson().toJson(this.insert(request)));
				break;
		}
	}
	
	private DatatablePresentation makeDatatable(HttpServletRequest request) {
		DatatableParameter constraints = new DatatableParameter();
		DatatablePresentation presentation = new DatatablePresentation();
		
		ArrayList<DatatableColumn> columns = new ArrayList<>();
		boolean hasColumnParameter = false;
		for (int index = 0; hasColumnParameter; index++) {
			if (request.getParameter("columns[" + index + "][name]") != null) {
				DatatableColumn column = new DatatableColumn();
				column.setData(Integer.valueOf(request.getParameter("columns[" + index + "][data]")));
				column.setName(request.getParameter("columns[" + index + "][name]"));
				column.setOrderable(Boolean.valueOf(request.getParameter("columns[" + index + "][orderable]")));
				column.setSearchable(Boolean.valueOf(request.getParameter("columns[" + index + "][searchable]")));
				columns.add(column);
			} else hasColumnParameter = false;
		}
		constraints.setColumns(columns.toArray(new DatatableColumn[0]));
		constraints.setDraw(Integer.valueOf(request.getParameter("draw")));
		constraints.setLimitLength(Integer.valueOf(request.getParameter("length")));
		constraints.setLimitStart(Integer.valueOf(request.getParameter("start")));
		constraints.setOrderColumn(Integer.valueOf(request.getParameter("order[0][column]")));
		constraints.setOrderDirection(request.getParameter("order[0][dir]"));
		constraints.setSearch(new DatatableSearch(request.getParameter("search[value]"), Boolean.valueOf(request.getParameter("search[regex]"))));
		
		String queries = achatDao.makeQuery(constraints);
		ArrayList<Map> incomingData = achatDao.makeDatatable(queries, constraints);
		ArrayList<String[]> data = new ArrayList<>();
		for (Map<String, Object> retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.get("dateOperation").toString(),
				retrievedData.get("facture").toString(),
				retrievedData.get("fournisseur").toString(),
				retrievedData.get("somme") + " Ar",
				retrievedData.get("modeDePayement") != null ? retrievedData.get("modeDePayement").toString() : "",
				retrievedData.get("reference") != null ? retrievedData.get("reference").toString() : "",
				retrievedData.get("payement").toString(),
				retrievedData.get("dateEcheance").toString(),
				"<div class=\"btn-group-vertical\">\n" +
					"<button class=\"btn btn-adequa modification\" data-toggle=\"modal\" data-target=\"#modifier\" data-identifiant=\"" + retrievedData.get("id") + "\">Modifier</button>\n" +
					"</div>"
			});
		}
		presentation.setDraw(constraints.getDraw());
		presentation.setRecordsTotal(this.achatDao.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException {
		Map<String, Object> post = new Gson().fromJson(request.getReader(), (Type) HashMap.class);
		MethodResponse methodResponse = new MethodResponse();
		
		AchatPL achatPL = new AchatPL();
		achatPL.setLibelle(post.get("libelle").toString());
		achatPL.setFacture(Integer.valueOf(post.get("facture").toString()));
		achatPL.setEnAttente((Boolean) post.get("payementEnAttente"));
		achatPL.setDateOperation(post.get("dateOperation").toString());
		achatPL.setSomme(Integer.valueOf(post.get("montant").toString()));
		achatPL.setModeDePayement(post.get("modeDePayement") != null ? post.get("modeDePayement").toString() : "");
		achatPL.setReferece(post.get("reference") != null ? post.get("reference").toString() : "");
		achatPL.setDateEcheance(post.get("dateEcheance").toString());
		
		boolean querySucceded;
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		if (achatPL.isEnAttente()) querySucceded = this.achatDao.insertOnly(achatPL);
		else querySucceded = this.achatDao.insertAndLog(achatPL);
		if (!querySucceded) {
			methodResponse.setRequestState(false).appendTable("achat");
			transaction.rollback();
		} else transaction.commit();
		return methodResponse.validate();
	}
}
