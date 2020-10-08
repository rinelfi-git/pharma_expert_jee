package mg.adequa.services.dao.postgresql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mg.adequa.beans.Utilisateur;
import mg.adequa.utils.jwt.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SessionManager {
	public static String SESSION = "session", COOKIE = "cookie";
	private static int DEFAULT_TIMER = 1200;
	private Session<Utilisateur>[] tokens;
	private String fileLocation;
	
	public SessionManager() {
		this.fileLocation = this.loadFile();
		try {
			this.readRecords();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String loadFile() {
		String classExecution = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		File jsonFile = new File((new File(classExecution).getParent()) + File.separatorChar + "json-web-token.json");
		if (!jsonFile.exists()) {
			try {
				jsonFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
				writer.write("[]\n");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jsonFile.getAbsolutePath();
	}
	
	public void readRecords() throws IOException {
		StringBuilder builder = new StringBuilder();
		List<String> reader = Files.readAllLines(Paths.get(this.fileLocation));
		for (String read : reader) builder.append(read);
		String compiledJson = builder.toString().trim();
		Type jwtCollection = new TypeToken<Session<Utilisateur>[]>() {}.getType();
		this.tokens = new Gson().fromJson(compiledJson, jwtCollection);
	}
	
	public SessionManager removeAt(int index) {
		Session<Utilisateur>[] temporaire = new Session[this.tokens.length - 1];
		for (int inputCounter = 0, outputCounter = 0; inputCounter < this.tokens.length; inputCounter++) {
			if (inputCounter != index) {
				temporaire[outputCounter] = this.tokens[inputCounter];
				outputCounter++;
			}
		}
		this.tokens = temporaire;
		return this;
	}
	
	public SessionManager add(int index, Session<Utilisateur> token) {
		Session<Utilisateur>[] temporaire = new Session[this.tokens.length + 1];
		for (int inputCounter = 0, outputCounter = 0; inputCounter < this.tokens.length + 1; inputCounter++, outputCounter++) {
			if (inputCounter == index) {
				temporaire[outputCounter] = token;
				outputCounter++;
			} else temporaire[outputCounter] = this.tokens[inputCounter];
		}
		this.tokens = temporaire;
		return this;
	}
	
	public SessionManager add(Session<Utilisateur> token) {
		Session<Utilisateur>[] temporaire = new Session[this.tokens.length + 1];
		for (int inputCounter = 0; inputCounter < this.tokens.length; inputCounter++) temporaire[inputCounter] = this.tokens[inputCounter];
		temporaire[this.tokens.length] = token;
		this.tokens = temporaire;
		return this;
	}
	
	public SessionManager writeRecords() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileLocation));
			writer.write(new Gson().toJson(this.tokens));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public boolean exists(String id) {
		for (Session<Utilisateur> token : this.tokens) if (id.equals(token.getId())) return true;
		return false;
	}
	
	public SessionManager addTimer(String id) {
		for (Session<Utilisateur> token : this.tokens) {
			if (id.equals(token.getId())){
				token.setDateExpiration(System.currentTimeMillis() + SessionManager.DEFAULT_TIMER);
				token.setDateDeCreationString(new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date(token.getDateDeCreation())));
				this.writeRecords();
			}
		}
		return this;
	}
}
