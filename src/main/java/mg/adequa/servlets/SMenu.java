package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BMenu;
import mg.adequa.beans.BMenuGroup;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DMenu;
import mg.adequa.utils.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SMenu extends HttpServlet {
	private DaoFactory dao;
	private DMenu dMenu;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.dao = PostgreSQL.getInstance();
		this.dMenu = this.dao.getMenu();
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
			case "load_menu_groups":
				try {
					response.getWriter().print(new Gson().toJson(this.loadMenuGroups(request)));
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
	
	private ArrayList<BMenuGroup> loadMenuGroups(HttpServletRequest request) throws NoConnectionException, SQLException, NoSpecifiedTableException {
		ArrayList<BMenuGroup> loadMenuGroups = new ArrayList<>();
		ArrayList<String> groups = this.dMenu.selectGroup();
		for(String group: groups) loadMenuGroups.add(new BMenuGroup(group, this.dMenu.selectMenuOfGroup(group)));
		return loadMenuGroups;
	}
}
