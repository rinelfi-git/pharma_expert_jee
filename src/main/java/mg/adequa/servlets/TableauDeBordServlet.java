package mg.adequa.servlets;

import com.google.gson.Gson;
import mg.adequa.beans.BilanTableauDeBord;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PsqlDao;
import mg.adequa.services.dao.interfaces.BilanDao;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;
import mg.adequa.utils.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TableauDeBordServlet extends HttpServlet {
	private BilanDao bilanDao;
	private JournalEntreeSortieDao journalEntreeSortieDao;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PsqlDao.getInstance();
		this.bilanDao = this.daoFactory.getBilanDao();
		this.journalEntreeSortieDao = this.daoFactory.getJournalEntreeSortieDao();
	}
	
	@Override
	public void doOptions(HttpServletRequest request, HttpServletResponse response) {}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (uriUtils.toArray()[1]) {
			case "dernier_bilan":
				response.getWriter().print(new Gson().toJson(this.dernierBilan(request)));
				break;
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {}
	
	private BilanTableauDeBord dernierBilan(HttpServletRequest request) {
		BilanTableauDeBord bilan = new BilanTableauDeBord();
		bilan.setTotalProduitEnStock(this.bilanDao.getProduit());
		bilan.setCompteCaisse(this.bilanDao.getCaisse());
		bilan.setCompteEnBanque(this.bilanDao.getBanque());
		bilan.setCapital(this.bilanDao.getProduit() + this.bilanDao.getCaisse() + this.bilanDao.getBanque());
		return bilan;
	}
}
