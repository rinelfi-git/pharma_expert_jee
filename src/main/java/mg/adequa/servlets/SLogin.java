package mg.adequa.servlets;

import com.google.gson.Gson;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import mg.adequa.beans.BSession;
import mg.adequa.beans.BUtilisateur;
import mg.adequa.payloads.PSession;
import mg.adequa.payloads.PUtilisateur;
import mg.adequa.services.Transaction;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DLogin;
import mg.adequa.services.dao.interfaces.DSession;
import mg.adequa.utils.UriUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SLogin extends HttpServlet {
	private DSession dSession;
	private DLogin dLogin;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.dSession = this.daoFactory.getSession();
		this.dLogin = this.daoFactory.getLogin();
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
			case "check_user":
				response.getWriter().print(new Gson().toJson(this.checkUser(request)));
				break;
			case "match_password":
				response.getWriter().print(new Gson().toJson(this.matchPassword(request)));
				break;
			case "check_token":
				response.getWriter().print(new Gson().toJson(this.isTokenAlive(request)));
				break;
			case "create_session":
				try {
					response.getWriter().print(new Gson().toJson(this.createSession(request)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case "sign_out":
				response.getWriter().print(new Gson().toJson(this.signOut(request)));
				break;
		}
	}
	
	private boolean signOut(HttpServletRequest request) throws IOException {
		Map parameter = new Gson().fromJson(request.getReader(), HashMap.class);
		try {
			return this.dSession.delete(parameter
				                            .get("token")
				                            .toString());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean checkUser(HttpServletRequest request) throws IOException {
		PUtilisateur pUtilisateur = new Gson().fromJson(request.getReader(), PUtilisateur.class);
		try {
			return this.dLogin.urtilisateurExiste(pUtilisateur.getLogin());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		} catch (NoConnectionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean matchPassword(HttpServletRequest request) throws IOException {
		PUtilisateur pUtilisateur = new Gson().fromJson(request.getReader(), PUtilisateur.class);
		try {
			String password = this.dLogin.getPassword(pUtilisateur.getLogin());
			return BCrypt.checkpw(pUtilisateur.getPassword(), password);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoConnectionException e) {
			e.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private PSession<PUtilisateur> retrieveSession(String id) {
		try {
			return this.dSession.get(id);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		} catch (NoConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BSession<BUtilisateur> createSession(HttpServletRequest request) throws Exception {
		PUtilisateur user = new Gson().fromJson(request.getReader(), PUtilisateur.class);
		Transaction transaction = new Transaction(this.daoFactory);
		transaction.begin();
		BUtilisateur utilisateur = this.dLogin.getData(user.getLogin());
		long currentTimestamp = System.currentTimeMillis();
		BSession<BUtilisateur> session = new BSession<>();
		session.setId(BCrypt.hashpw(String.valueOf(currentTimestamp), BCrypt.gensalt()));
		session.setType(BSession.SESSION);
		session.setDateCreation(new Date(currentTimestamp));
		session.setDateExpiration(new Date(currentTimestamp + BSession.DEFAULT_TIMER * 1000));
		session.setContenu(utilisateur);
		if (this.dSession.insert(session)) transaction.commit();
		else transaction.rollback();
		return session;
	}
	
	private boolean isTokenAlive(HttpServletRequest request) throws IOException {
		HashMap post = new Gson().fromJson(request.getReader(), HashMap.class);
		try {
			PSession<PUtilisateur> session = this.dSession.get(post.get("token").toString());
			if (session != null) {
				long currentTimestamp = System.currentTimeMillis();
				if (session.getDateExpiration().getTime() <= currentTimestamp) {
					Transaction transaction = new Transaction(this.daoFactory);
					transaction.begin();
					if (this.dSession.delete(session.getId())) transaction.commit();
					else transaction.rollback();
				} else {
					Transaction transaction = new Transaction(this.daoFactory);
					transaction.begin();
					if (this.dSession.addTimer(session.getId())) transaction.commit();
					else transaction.rollback();
				}
				return this.dSession.exists(session.getId());
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} catch (NoSpecifiedTableException e) {
			e.printStackTrace();
		} catch (NoConnectionException e) {
			e.printStackTrace();
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}
}
