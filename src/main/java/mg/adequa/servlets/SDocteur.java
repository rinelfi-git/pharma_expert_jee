package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BDocteur;
import mg.adequa.beans.BPersonne;
import mg.adequa.payloads.PDocteur;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DDocteur;
import mg.adequa.services.dao.interfaces.DPersonne;
import mg.adequa.tableviews.TvDocteur;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SDocteur extends HttpServlet {
	private DDocteur dDocteur;
	private DPersonne dPersonne;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.dDocteur = this.daoFactory.getDocteur();
		this.dPersonne = this.daoFactory.getPersonne();
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		String[] arrayUri = uriUtils.toArray();
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (arrayUri[2]) {
			case "select":
				response.getWriter().print(new Gson().toJson(this.select(Integer.valueOf(arrayUri[3]))));
				break;
		}
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
				break;
			case "update":
				break;
		}
	}
	
	private DatatablePresentation makeDatatable(HttpServletRequest request) {
		DatatableParameter constraints = new DatatableParameter();
		DatatablePresentation presentation = new DatatablePresentation();
		// ArrayList<DatatableColumn> columns = this.getColumnsFromRequest(request);
		// constraints.setColumns(columns.toArray(new DatatableColumn[0]));
		constraints.setDraw(Integer.valueOf(request.getParameter("draw")));
		constraints.setLimitLength(Integer.valueOf(request.getParameter("length") != null ? request.getParameter("length") : "-1"));
		constraints.setLimitStart(Integer.valueOf(request.getParameter("start") != null ? request.getParameter("start") : "-1"));
		constraints.setOrderColumn(Integer.valueOf(request.getParameter("order[0][column]") != null ? request.getParameter("order[0][column]") : "-1"));
		constraints.setOrderDirection(request.getParameter("order[0][dir]"));
		constraints.setSearch(new DatatableSearch(request.getParameter("search[value]"), Boolean.valueOf(request.getParameter("search[regex]"))));
		
		ArrayList<TvDocteur> incomingData = new ArrayList<>();
		try {
			incomingData = dDocteur.makeDatatable(dDocteur.makeQuery(constraints), constraints);
		} catch (SQLException | NoSpecifiedTableException | NoConnectionException throwables) {
			throwables.printStackTrace();
		}
		ArrayList<String[]> data = new ArrayList<>();
		for (TvDocteur retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getNomPrenom(),
				retrievedData.getService(),
				retrievedData.getAction()
			});
		}
		presentation.setDraw(constraints.getDraw());
		try {
			presentation.setRecordsTotal(this.dDocteur.dataRecordsTotal());
		} catch (NoSpecifiedTableException | SQLException | NoConnectionException throwables) {
			throwables.printStackTrace();
		}
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private PDocteur select(int id) {
		PDocteur select = null;
		try {
			select = dDocteur.select(id);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		} catch (NoConnectionException e) {
			e.printStackTrace();
		}
		return select;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException {
		PDocteur pDocteur = new Gson().fromJson(request.getReader(), PDocteur.class);
		MethodResponse insert = new MethodResponse();
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		BPersonne personne = new BPersonne();
		personne.setNom(pDocteur.getNom());
		personne.setPrenom(pDocteur.getPrenom());
		try {
			if (this.dPersonne.insert(personne)) {
				
				BDocteur docteur = new BDocteur();
				docteur.setIdPersonne(this.dPersonne.lastId());
				docteur.setNom(pDocteur.getNom());
				docteur.setPrenom(pDocteur.getPrenom());
				docteur.setServiceHospitalier(pDocteur.getServiceHospitalier());
				
				if (this.dDocteur.insert(docteur)) {
					transaction.commit();
				} else {
					transaction.rollback();
					insert.setRequestState(false).appendTable("docteur");
				}
			} else {
				transaction.rollback();
				insert.setRequestState(false).appendTable("personne");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insert.validate();
	}
	
	private MethodResponse update(HttpServletRequest request, int id) throws IOException {
		PDocteur pDocteur = new Gson().fromJson(request.getReader(), PDocteur.class);
		MethodResponse update = new MethodResponse();
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		BPersonne personne = new BPersonne();
		personne.setNom(pDocteur.getNom());
		personne.setPrenom(pDocteur.getPrenom());
		try {
			if (this.dPersonne.update(personne, id)) {
				
				BDocteur docteur = new BDocteur();
				docteur.setIdPersonne(this.dPersonne.lastId());
				docteur.setNom(pDocteur.getNom());
				docteur.setPrenom(pDocteur.getPrenom());
				docteur.setServiceHospitalier(pDocteur.getServiceHospitalier());
				
				if (this.dDocteur.update(docteur, id))
					transaction.commit();
				else {
					transaction.rollback();
					update.setRequestState(false).appendTable("docteur");
				}
			} else {
				transaction.rollback();
				update.setRequestState(false).appendTable("personne");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return update.validate();
	}
}
