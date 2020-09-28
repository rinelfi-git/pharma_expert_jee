package mg.adequa.servlets;

import com.google.gson.Gson;
import mg.adequa.beans.BilanTableauDeBord;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.BilanDao;
import mg.adequa.services.dao.interfaces.JournalEntreeSortieDao;
import mg.adequa.utils.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TableauDeBordServlet extends HttpServlet {
	private BilanDao bilanDao;
	private JournalEntreeSortieDao journalEntreeSortieDao;
	private DaoFactory daoFactory;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.daoFactory = PostgreSQL.getInstance();
		this.bilanDao = this.daoFactory.getBilanDao();
		this.journalEntreeSortieDao = this.daoFactory.getJournalEntreeSortieDao();
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (uriUtils.toArray()[1]) {
			case "evolution_compte_financier":
				response.getWriter().print(new Gson().toJson(this.evolutionCompteFinancier(request)));
				break;
		}
	}
	
	private BilanTableauDeBord dernierBilan(HttpServletRequest request) {
		BilanTableauDeBord bilan = new BilanTableauDeBord();
		bilan.setTotalProduitEnStock(this.bilanDao.getProduit());
		bilan.setCompteCaisse(this.bilanDao.getCaisse());
		bilan.setCompteEnBanque(this.bilanDao.getBanque());
		bilan.setCapital(this.bilanDao.getProduit() + this.bilanDao.getCaisse() + this.bilanDao.getBanque());
		return bilan;
	}
	
	private ArrayList<Map> evolutionCompteFinancier(HttpServletRequest request) throws IOException {
		Map<String, Integer> post = new Gson().fromJson(request.getReader(), HashMap.class);
		ArrayList<Map> output = new ArrayList<>();
		
		Calendar maintenant = Calendar.getInstance(),
			ilYaDouzeMois = (Calendar) maintenant.clone(),
			arretDeBoucle = Calendar.getInstance();
		ilYaDouzeMois.add(Calendar.MONTH, -12);
		
		int moisDebut = post.get("moisDebut") != null ? post.get("moisDebut") : ilYaDouzeMois.get(Calendar.MONTH) + 1,
			anneeDebut = post.get("anneeDebut") != null ? post.get("anneeDebut") : ilYaDouzeMois.get(Calendar.YEAR),
			moisFin = post.get("moisFin") != null ? post.get("moisFin") : maintenant.get(Calendar.MONTH) + 1,
			anneeFin = post.get("anneeFin") != null ? post.get("anneeFin") : maintenant.get(Calendar.YEAR);
		
		arretDeBoucle.set(Calendar.SECOND, arretDeBoucle.get(Calendar.SECOND) + 1);
		arretDeBoucle.set(Calendar.YEAR, anneeFin);
		arretDeBoucle.set(Calendar.MONTH, (moisFin - 1) % 12);
		
		Calendar compteurDeDate = Calendar.getInstance();
		compteurDeDate.set(anneeDebut, moisDebut, 1);
		
		for (; compteurDeDate.getTime().getTime() < arretDeBoucle.getTime().getTime(); compteurDeDate.add(Calendar.MONTH, 1)) {
			Map<String, Integer> evolutionCompteFinancier = new HashMap<>();
			evolutionCompteFinancier.put("mois", compteurDeDate.get(Calendar.MONTH) + 1);
			evolutionCompteFinancier.put("annee", compteurDeDate.get(Calendar.YEAR));
			evolutionCompteFinancier.put("entree", this.journalEntreeSortieDao.sommeEntrees(compteurDeDate.get(Calendar.MONTH) + 1, compteurDeDate.get(Calendar.YEAR)));
			evolutionCompteFinancier.put("sortie", this.journalEntreeSortieDao.sommeSorties(compteurDeDate.get(Calendar.MONTH) + 1, compteurDeDate.get(Calendar.YEAR)));
			output.add(evolutionCompteFinancier);
		}
		return output;
	}
}
