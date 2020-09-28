package mg.adequa.servlets;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PsqlDao;
import mg.adequa.services.dao.interfaces.DocteurDao;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;

public class DocteurServlet extends HttpServlet {
	private DocteurDao docteurDao;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PsqlDao.getInstance();
		this.docteurDao = this.daoFactory.getDocteurDao();
	}
	
	@Override
	public void doOptions(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		switch (uriUtils.toArray()[1]) {
			case "select":
				break;
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		switch (uriUtils.toArray()[1]) {
			case "make_datatable":
				break;
			case "insert":
				break;
			case "update":
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
		
		String queries = docteurDao.makeQuery(constraints);
		ArrayList<Map> incomingData = docteurDao.makeDatatable(queries, constraints);
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
		presentation.setRecordsTotal(this.docteurDao.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
}
