package mg.adequa.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mg.adequa.payloads.PAchat;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.interfaces.DaoAchat;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class AchatServlet extends HttpServlet {
	private DaoFactory daoFactory;
	private DaoAchat daoAchat;
	
	public AchatServlet() {super();}
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.daoAchat = this.daoFactory.getAchat();
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
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (uriUtils.toArray()[1]) {
			case "select":
				if(uriUtils.toArray().length > 2) response.getWriter().print(new Gson().toJson(this.select(uriUtils.toArray()[2])));
				else response.getWriter().print(new Gson().toJson(this.select()));
				break;
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.setContentType("application/json");
		switch (uriUtils.getLastSegment()) {
			case "make_datatable":
				response.getWriter().print(new Gson().toJson(this.makeDatatable(request)));
				break;
			case "insert":
				response.getWriter().print(new Gson().toJson(this.insert(request)));
				break;
		}
	}
	
	private ArrayList<DatatableColumn> getColumnsFromRequest(HttpServletRequest request) {
		final ArrayList<DatatableColumn> getColumnsFromRequest = new ArrayList<>();
		boolean hasColumnParameter = false;
		for (int index = 0; hasColumnParameter; index++) {
			if (request.getParameter("columns[" + index + "][name]") != null) {
				DatatableColumn column = new DatatableColumn();
				column.setData(Integer.valueOf(request.getParameter("columns[" + index + "][data]")));
				column.setName(request.getParameter("columns[" + index + "][name]"));
				column.setOrderable(Boolean.valueOf(request.getParameter("columns[" + index + "][orderable]")));
				column.setSearchable(Boolean.valueOf(request.getParameter("columns[" + index + "][searchable]")));
				getColumnsFromRequest.add(column);
			} else hasColumnParameter = false;
		}
		return getColumnsFromRequest;
	}
	
	private DatatablePresentation makeDatatable(HttpServletRequest request) {
		DatatableParameter constraints = new DatatableParameter();
		DatatablePresentation presentation = new DatatablePresentation();
		// ArrayList<DatatableColumn> columns = this.getColumnsFromRequest(request);
		// constraints.setColumns(columns.toArray(new DatatableColumn[0]));
		constraints.setDraw(Integer.valueOf(request.getParameter("draw")));
		constraints.setLimitLength(Integer.valueOf(request.getParameter("length")));
		constraints.setLimitStart(Integer.valueOf(request.getParameter("start")));
		constraints.setOrderColumn(Integer.valueOf(request.getParameter("order[0][column]")));
		constraints.setOrderDirection(request.getParameter("order[0][dir]"));
		constraints.setSearch(new DatatableSearch(request.getParameter("search[value]"), Boolean.valueOf(request.getParameter("search[regex]"))));
		
		String queries = daoAchat.makeQuery(constraints);
		ArrayList<Map> incomingData = daoAchat.makeDatatable(queries, constraints);
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
		presentation.setRecordsTotal(this.daoAchat.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private PAchat select(String uriSegment) {
		return this.daoAchat.select(Integer.valueOf(uriSegment));
	}
	
	private ArrayList<PAchat> select() {
		return this.daoAchat.select();
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException {
		PAchat pAchat = new Gson().fromJson(request.getReader(), PAchat.class);
		MethodResponse methodResponse = new MethodResponse();
		
		boolean querySucceded;
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		if (pAchat.isEnAttente()) querySucceded = this.daoAchat.insertOnly(pAchat);
		else querySucceded = this.daoAchat.insertAndLog(pAchat);
		if (!querySucceded) {
			methodResponse.setRequestState(false).appendTable("achat");
			transaction.rollback();
		} else transaction.commit();
		return methodResponse.validate();
	}
	
	private MethodResponse update(HttpServletRequest request) throws IOException {
		Type achatPlArray = new TypeToken<PAchat[]>(){}.getType();
		PAchat[] pAchat = new Gson().fromJson(request.getReader(), achatPlArray);
		MethodResponse methodResponse = new MethodResponse();
		
		boolean querySucceded;
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		if (pAchat[0].isEnAttente()) querySucceded = this.daoAchat.insertOnly(pAchat[0]);
		else querySucceded = this.daoAchat.insertAndLog(pAchat[0]);
		if (!querySucceded) {
			methodResponse.setRequestState(false).appendTable("achat");
			transaction.rollback();
		} else transaction.commit();
		return methodResponse.validate();
	}
}
