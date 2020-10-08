package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BDocteur;
import mg.adequa.beans.BJournalDeSession;
import mg.adequa.beans.BPersonne;
import mg.adequa.payloads.PDocteur;
import mg.adequa.payloads.PJournalDeSession;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DJournalDeSession;
import mg.adequa.tableviews.TJournalDeSession;
import mg.adequa.tableviews.TvDocteur;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SJournalDeSession extends HttpServlet {
	private DaoFactory dao;
	private DJournalDeSession dJournalDeSession;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.dao = PostgreSQL.getInstance();
		this.dJournalDeSession = this.dao.getJournalDeSession();
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		String[] arrayUri = uriUtils.toArray();
		switch (arrayUri[2]) {
			case "make_datatable":
				response.getWriter().print(new Gson().toJson(this.makeDatatable(request)));
				break;
			case "insert":
				response.getWriter().print(new Gson().toJson(this.insert(request)));
				break;
			case "delete":
				response.getWriter().print(new Gson().toJson(this.delete()));
				break;
		}
	}
	
	private DatatablePresentation makeDatatable(HttpServletRequest request) {
		DatatableParameter constraints = new DatatableParameter();
		DatatablePresentation presentation = new DatatablePresentation();
		constraints.setDraw(Integer.valueOf(request.getParameter("draw")));
		constraints.setLimitLength(Integer.valueOf(request.getParameter("length") != null ? request.getParameter("length") : "-1"));
		constraints.setLimitStart(Integer.valueOf(request.getParameter("start") != null ? request.getParameter("start") : "-1"));
		constraints.setOrderColumn(Integer.valueOf(request.getParameter("order[0][column]") != null ? request.getParameter("order[0][column]") : "-1"));
		constraints.setOrderDirection(request.getParameter("order[0][dir]"));
		constraints.setSearch(new DatatableSearch(request.getParameter("search[value]"), Boolean.valueOf(request.getParameter("search[regex]"))));
		
		ArrayList<TJournalDeSession> incomingData = new ArrayList<>();
		try {
			incomingData = dJournalDeSession.makeDatatable(dJournalDeSession.makeQuery(constraints), constraints);
		} catch (SQLException | NoSpecifiedTableException | NoConnectionException throwables) {
			throwables.printStackTrace();
		}
		ArrayList<String[]> data = new ArrayList<>();
		for (TJournalDeSession retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getDateHeure(),
				retrievedData.getUtilisateur(),
				retrievedData.getTache()
			});
		}
		presentation.setDraw(constraints.getDraw());
		try {
			presentation.setRecordsTotal(this.dJournalDeSession.dataRecordsTotal());
		} catch (NoSpecifiedTableException | SQLException | NoConnectionException throwables) {
			throwables.printStackTrace();
		}
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException {
		PJournalDeSession pJournalDeSession = new Gson().fromJson(request.getReader(), PJournalDeSession.class);
		MethodResponse insert = new MethodResponse();
		Transaction transaction = new Transaction(this.dao);
		transaction.begin();
		BJournalDeSession journalDeSession = new BJournalDeSession();
		journalDeSession.setComptePersonnel(pJournalDeSession.getComptePersonnel());
		journalDeSession.setAction(pJournalDeSession.getAction());
		try {
			if (this.dJournalDeSession.insert(journalDeSession)) transaction.commit();
			else {
				transaction.rollback();
				insert.setRequestState(false).appendTable("journal_de_session");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insert.validate();
	}
	
	private MethodResponse delete() {
		MethodResponse delete = new MethodResponse();
		Transaction transaction = new Transaction(this.dao);
		transaction.begin();
		try {
			if(this.dJournalDeSession.delete()) transaction.commit();
			else {
				transaction.rollback();
				delete.setRequestState(false).appendTable("journal_de_session");
			}
			return delete.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delete.validate();
	}
}
