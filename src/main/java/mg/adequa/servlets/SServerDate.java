package mg.adequa.servlets;

import com.google.gson.Gson;
import mg.adequa.utils.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SServerDate extends HttpServlet {
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UriUtils uriUtils = new UriUtils(request.getRequestURI());
		String[] arrayUri = uriUtils.toArray();
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Cache-Control,content-type,Accept,DNT,X-CustomHeader,Keep-Alive,User-Agent");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Max-Age", "1728000");
		switch (arrayUri[2]) {
			case "datetime":
				Calendar calendar = Calendar.getInstance();
				Map<String, Object> sortie = new HashMap<>();
				sortie.put("fullDate", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
				sortie.put("fullTime", new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()));
				sortie.put("fullDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				sortie.put("day", calendar.get(Calendar.DATE));
				sortie.put("month", calendar.get(Calendar.MONTH) + 1);
				sortie.put("year", calendar.get(Calendar.YEAR));
				sortie.put("hour", calendar.get(Calendar.HOUR));
				sortie.put("minute", calendar.get(Calendar.MINUTE));
				sortie.put("second", calendar.get(Calendar.SECOND));
				response.getWriter().print(new Gson().toJson(sortie));
				break;
		}
	}
}
