package mg.adequa.servlets;

import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class SLogin extends HttpServlet {
	private DSession dSession;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.dSession = this.daoFactory.getSession();
	}
}
