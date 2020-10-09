package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.payloads.PNotification;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DNotification;
import mg.adequa.utils.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SNotification extends HttpServlet {
	private DaoFactory dao;
	private DNotification dNotification;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.dao = PostgreSQL.getInstance();
		this.dNotification = this.dao.getNotification();
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
			case "get_last":
				response.getWriter().print(new Gson().toJson(this.getLast(Integer.valueOf(arrayUri[3]), Integer.valueOf(arrayUri[4]))));
				break;
			case "clean":
				response.getWriter().print(new Gson().toJson(this.clean(Integer.valueOf(arrayUri[3]))));
				break;
		}
	}
	
	private Map<String, Object> getLast(int limite, int poste) throws IOException {
		Map<String, Object> getLast = new HashMap<>();
		try {
			getLast.put("counter", this.dNotification.countNew(poste));
			getLast.put("notifications", this.dNotification.getLastimited(limite, poste));
		} catch (NoConnectionException e) {
			e.printStackTrace();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		}
		return getLast;
	}
	
	private Map<String, Object> clean(int poste) throws IOException {
		Map<String, Object> clean = new HashMap<>();
		Transaction transaction = new Transaction(this.dao);
		try {
			transaction.begin();
			this.dNotification.clean(poste);
			clean.put("counter", 0);
			clean.put("notifications", new ArrayList<>());
		} catch (SQLException throwables) {
			transaction.rollback();
			throwables.printStackTrace();
		} catch (InvalidExpressionException e) {
			transaction.rollback();
			e.printStackTrace();
		}
		return clean;
	}
}
