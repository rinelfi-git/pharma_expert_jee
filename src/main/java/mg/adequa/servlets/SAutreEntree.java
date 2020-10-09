package mg.adequa.servlets;

import com.google.gson.Gson;
import mg.adequa.payloads.PAutreEntree;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.interfaces.DAutreEntree;
import mg.adequa.tableviews.AutreEntreeTV;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SAutreEntree extends HttpServlet {
	private DaoFactory daoFactory;
	private DAutreEntree dAutreEntree;
	
	@Override
	public void init() throws ServletException {
		super.init();
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
		
		String queries = dAutreEntree.makeQuery(constraints);
		ArrayList<AutreEntreeTV> incomingData = dAutreEntree.makeDatatable(queries, constraints);
		ArrayList<String[]> data = new ArrayList<>();
		for (AutreEntreeTV retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getDateOperation(),
				retrievedData.getLibelleOperation(),
				retrievedData.getNumeroDePieceJustificative(),
				retrievedData.getSomme() + " Ar",
				retrievedData.getObservation(),
				retrievedData.getAction()
			});
		}
		presentation.setDraw(constraints.getDraw());
		presentation.setRecordsTotal(this.dAutreEntree.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException {
		PAutreEntree post = new Gson().fromJson(request.getReader(), PAutreEntree.class);
		MethodResponse methodResponse = new MethodResponse();
		
		final String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (post.getDateOperation().equals(now)) post.setDateOperation(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		else post.setDateOperation(post.getDateOperation() + " 00:00:00");
		
		boolean querySucceded = false;
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		if (post.getModeDePayment() != null) querySucceded = this.dAutreEntree.insert(post);
		if (!querySucceded) {
			methodResponse.setRequestState(false).appendTable("autre_entree").appendTable("journal_entree");
			transaction.rollback();
		} else transaction.commit();
		return methodResponse.validate();
	}
}
