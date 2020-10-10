package mg.adequa.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BMenu;
import mg.adequa.beans.BMenuGroup;
import mg.adequa.payloads.PAutorisation;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DAutorisation;
import mg.adequa.tableviews.TAutorisation;
import mg.adequa.utils.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SAutorisation extends HttpServlet {
	private DaoFactory dao;
	private DAutorisation dAutorisation;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.dao = PostgreSQL.getInstance();
		this.dAutorisation = this.dao.getAutorisation();
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
			case "set_authorization":
				try {
					response.getWriter().print(new Gson().toJson(this.setAuthorization(request)));
				} catch (InvalidExpressionException e) {
					e.printStackTrace();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
				break;
			case "select_authorized_menu":
				try {
					response.getWriter().print(new Gson().toJson(this.autorisedMenu(request)));
				} catch (NoConnectionException e) {
					e.printStackTrace();
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				} catch (NoSpecifiedTableException e) {
					e.printStackTrace();
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
		
		ArrayList<TAutorisation> incomingData = dAutorisation.makeDatatable(dAutorisation.makeQuery(constraints), constraints);
		ArrayList<String[]> data = new ArrayList<>();
		TAutorisation.initCount();
		for (TAutorisation retrievedData : incomingData) {
			data.add(new String[]{
				retrievedData.getNom(),
				retrievedData.getList(),
				retrievedData.getAction()
			});
		}
		presentation.setDraw(constraints.getDraw());
		presentation.setRecordsTotal(this.dAutorisation.dataRecordsTotal());
		presentation.setRecordsFiltered(data.size());
		presentation.setData(data);
		return presentation;
	}
	
	private ArrayList<BMenuGroup> autorisedMenu(HttpServletRequest request) throws IOException, NoConnectionException, SQLException, NoSpecifiedTableException {
		Type postType = new TypeToken<HashMap<String, Integer>>() {}.getType();
		Map<String, Integer> post = new Gson().fromJson(request.getReader(), postType);
		ArrayList<BMenuGroup> autorisedMenu = new ArrayList<>();
		ArrayList<BMenu> menus = null;
		menus = this.dAutorisation.selectMenus(post.get("utilisateur"));
		for (BMenu menu : menus) {
			String groupName = menu.getGroup();
			if (autorisedMenu.size() > 0) {
				int lastIndex = autorisedMenu.size() - 1;
				BMenuGroup lastGroup = autorisedMenu.get(lastIndex);
				if (lastGroup.getNom().equals(groupName)) {
					autorisedMenu.get(lastIndex).getMenus().add(menu);
				} else {
					BMenuGroup group = new BMenuGroup();
					group.setNom(menu.getGroup());
					group.getMenus().add(menu);
					autorisedMenu.add(group);
				}
			} else {
				BMenuGroup group = new BMenuGroup();
				group.setNom(menu.getGroup());
				group.getMenus().add(menu);
				autorisedMenu.add(group);
			}
		}
		return autorisedMenu;
	}
	
	private MethodResponse setAuthorization(HttpServletRequest request) throws IOException, InvalidExpressionException, SQLException {
		PAutorisation inAutorisation = new Gson().fromJson(request.getReader(), PAutorisation.class);
		MethodResponse response = new MethodResponse();
		Transaction transaction = new Transaction(this.dao);
		transaction.begin();
		if (this.dAutorisation.clear(inAutorisation.getUtilisateur())) {
			for (BMenu menu : inAutorisation.getMenus()) {
				if (!this.dAutorisation.add(inAutorisation.getUtilisateur(), menu.getLien())) {
					transaction.rollback();
					response.appendTable("autorisation").setRequestState(false);
				}
			}
		} else {
			transaction.rollback();
			response.appendTable("autorisation").setRequestState(false);
		}
		if (response.hasRequestSuccess()) transaction.commit();
		return response.validate();
	}
}
