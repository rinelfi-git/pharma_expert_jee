package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BFournisseur;
import mg.adequa.payloads.PFournisseur;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DFournisseur;
import mg.adequa.tableviews.TAutorisation;
import mg.adequa.tableviews.TFournisseur;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SFournisseur extends HttpServlet {
	private DaoFactory dao;
	private DFournisseur fournisseurDao;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.dao = PostgreSQL.getInstance();
		this.fournisseurDao = this.dao.getFournisseur();
	}
	
	@Override
	public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
				} catch (InvalidExpressionException e) {
					e.printStackTrace();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
				break;
			case "update":
				try {
					response.getWriter().print(new Gson().toJson(this.update(request)));
				} catch (InvalidExpressionException e) {
					e.printStackTrace();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
				break;
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			case "select":
				if (arrayUri[3] != null) {
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
		
		ArrayList<TFournisseur> incomingData = fournisseurDao.makeDatatable(constraints);
		ArrayList<String[]> data = new ArrayList<>();
		TAutorisation.initCount();
		for (TFournisseur retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getNom(),
				retrievedData.getLocalisation(),
				retrievedData.getDescription(),
				retrievedData.getAction()
			});
		}
		presentation.setDraw(constraints.getDraw());
		presentation.setRecordsTotal(this.fournisseurDao.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private MethodResponse insert(HttpServletRequest request) throws IOException, InvalidExpressionException, SQLException {
		MethodResponse insert = new MethodResponse();
		PFournisseur pFournisseur = new Gson().fromJson(request.getReader(), PFournisseur.class);
		Transaction transaction = new Transaction(this.dao);
		
		BFournisseur fournisseur = new BFournisseur();
		fournisseur.setNom(pFournisseur.getNom());
		fournisseur.setLocalisation(pFournisseur.getLocalisation());
		fournisseur.setInformationComplementaire(pFournisseur.getInformationComplementaire());
		transaction.begin();
		if (this.fournisseurDao.insert(fournisseur)) transaction.commit();
		else {
			transaction.rollback();
			insert.setRequestState(false).appendTable("fournisseur");
		}
		return insert.validate();
	}
	
	private MethodResponse update(HttpServletRequest request) throws IOException, InvalidExpressionException, SQLException {
		MethodResponse update = new MethodResponse();
		PFournisseur fournisseurPayload = new Gson().fromJson(request.getReader(), PFournisseur.class);
		Transaction transaction = new Transaction(this.dao);
		
		BFournisseur fournisseur = new BFournisseur();
		fournisseur.setNom(fournisseurPayload.getNom());
		fournisseur.setLocalisation(fournisseurPayload.getLocalisation());
		fournisseur.setInformationComplementaire(fournisseurPayload.getInformationComplementaire());
		transaction.begin();
		if (this.fournisseurDao.update(fournisseurPayload.getId(), fournisseur)) transaction.commit();
		else {
			transaction.rollback();
			update.setRequestState(false).appendTable("fournisseur");
		}
		return update.validate();
	}
	
	private ArrayList<PFournisseur> select() throws NoConnectionException, SQLException, NoSpecifiedTableException {
		return this.fournisseurDao.select();
	}
	
	private PFournisseur select(int id) throws NoConnectionException, SQLException, NoSpecifiedTableException {
		return this.fournisseurDao.select(id);
	}
}
