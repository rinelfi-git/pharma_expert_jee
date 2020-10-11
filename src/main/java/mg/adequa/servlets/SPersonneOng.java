package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BPersonne;
import mg.adequa.dbentity.BPersonneOng;
import mg.adequa.payloads.PPersonneOng;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DPersonne;
import mg.adequa.services.dao.interfaces.DPersonneOng;
import mg.adequa.tableviews.TAutorisation;
import mg.adequa.tableviews.TPersonneOng;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SPersonneOng extends HttpServlet {
	private DaoFactory daoFactory;
	private DPersonneOng personneOngDao;
	private DPersonne personneDao;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.personneOngDao = this.daoFactory.getPersonneOng();
		this.personneDao = this.daoFactory.getPersonne();
	}
	
	@Override
	public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf8");
		request.setCharacterEncoding("utf8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("utf8");
		request.setCharacterEncoding("utf8");
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
				try {
					response.getWriter().print(new Gson().toJson(this.makeDatatable(request)));
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				} catch (NoConnectionException e) {
					e.printStackTrace();
				} catch (NoSpecifiedTableException e) {
					e.printStackTrace();
				}
				break;
			case "insert":
				try {
					response.getWriter().print(new Gson().toJson(this.insert(request)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "update":
				try {
					response.getWriter().print(new Gson().toJson(this.update(request)));
				} catch (InvalidExpressionException e) {
					e.printStackTrace();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("utf8");
		request.setCharacterEncoding("utf8");
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		String[] arrayUri = uriUtils.toArray();
		switch (arrayUri[2]) {
			case "select":
				if (arrayUri.length > 3) {
					try {
						response.getWriter().print(new Gson().toJson(this.select(Integer.valueOf(arrayUri[3]))));
					} catch (NoConnectionException e) {
						e.printStackTrace();
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					} catch (NoSpecifiedTableException e) {
						e.printStackTrace();
					}
				}
				else {
					try {
						response.getWriter().print(new Gson().toJson(this.select()));
					} catch (NoConnectionException e) {
						e.printStackTrace();
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					} catch (NoSpecifiedTableException e) {
						e.printStackTrace();
					}
				}
					break;
		}
	}
	
	private DatatablePresentation makeDatatable(HttpServletRequest request) throws SQLException, NoConnectionException, NoSpecifiedTableException {
		DatatableParameter constraints = new DatatableParameter();
		DatatablePresentation presentation = new DatatablePresentation();
		constraints.setDraw(Integer.valueOf(request.getParameter("draw")));
		constraints.setLimitLength(Integer.valueOf(request.getParameter("length") != null ? request.getParameter("length") : "-1"));
		constraints.setLimitStart(Integer.valueOf(request.getParameter("start") != null ? request.getParameter("start") : "-1"));
		constraints.setOrderColumn(Integer.valueOf(request.getParameter("order[0][column]") != null ? request.getParameter("order[0][column]") : "-1"));
		constraints.setOrderDirection(request.getParameter("order[0][dir]"));
		constraints.setSearch(new DatatableSearch(request.getParameter("search[value]"), Boolean.valueOf(request.getParameter("search[regex]"))));
		
		ArrayList<TPersonneOng> incomingData = this.personneOngDao.makeDatatable(constraints);
		ArrayList<String[]> data = new ArrayList<>();
		TAutorisation.initCount();
		for (TPersonneOng retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getNomPrenom(),
				retrievedData.getOng(),
				retrievedData.getAction()
			});
		}
		presentation.setDraw(constraints.getDraw());
		presentation.setRecordsTotal(this.personneOngDao.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws Exception {
		MethodResponse insert = new MethodResponse();
		PPersonneOng personneOngPayload = new Gson().fromJson(request.getReader(), PPersonneOng.class);
		Transaction transaction = new Transaction(this.daoFactory);
		
		BPersonne personne = new BPersonne();
		personne.setNom(personneOngPayload.getNom());
		personne.setPrenom(personneOngPayload.getPrenom());
		transaction.begin();
		if (this.personneDao.insert(personne)) {
			BPersonneOng personneOng = new BPersonneOng();
			personneOng.setId(this.personneDao.lastId());
			personneOng.setOng(personneOngPayload.getOng());
			if (!this.personneOngDao.insert(personneOng)) insert.setRequestState(false).appendTable("persone ong");
		} else insert.setRequestState(false).appendTable("personne");
		
		if (insert.hasRequestSuccess()) transaction.commit();
		else transaction.rollback();
		
		return insert.validate();
	}
	
	private MethodResponse update(HttpServletRequest request) throws Exception {
		MethodResponse update = new MethodResponse();
		PPersonneOng personneOngPayload = new Gson().fromJson(request.getReader(), PPersonneOng.class);
		Transaction transaction = new Transaction(this.daoFactory);
		
		BPersonne personne = new BPersonne();
		personne.setNom(personneOngPayload.getNom());
		personne.setPrenom(personneOngPayload.getPrenom());
		transaction.begin();
		if (this.personneDao.update(personneOngPayload.getId(), personne)) {
			BPersonneOng personneOng = new BPersonneOng();
			personneOng.setId(personneOngPayload.getId());
			personneOng.setOng(personneOngPayload.getOng());
			if (!this.personneOngDao.update(personneOngPayload.getId(), personneOng)) update.setRequestState(false).appendTable("persone ong");
		} else update.setRequestState(false).appendTable("personne");
		
		if (update.hasRequestSuccess()) transaction.commit();
		else transaction.rollback();
		
		return update.validate();
	}
	
	private ArrayList<PPersonneOng> select() throws NoConnectionException, SQLException, NoSpecifiedTableException {
		return this.personneOngDao.select();
	}
	
	private PPersonneOng select(int id) throws NoConnectionException, SQLException, NoSpecifiedTableException {
		return this.personneOngDao.select(id);
	}
}
