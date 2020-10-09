package mg.adequa.services.dao.postgresql;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.implementations.PostgreSQLQueryBuilder;
import mg.adequa.dbentity.DbTables;
import mg.adequa.payloads.PNotification;
import mg.adequa.services.dao.DaoFactory;
import mg.adequa.services.dao.PostgreSQL;
import mg.adequa.services.dao.interfaces.DNotification;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MNotification implements DNotification {
	private DaoFactory dao;
	private DbTables tables;
	
	public MNotification(DaoFactory dao) {
		this.dao = dao;
		this.tables = new DbTables();
	}
	
	
	@Override
	public ArrayList<PNotification> getLastimited(int limit, int poste) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		ArrayList<PNotification> getLast = new ArrayList<>();
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		Map<String, String> transposition = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		transposition.put(
			"CASE " +
				"WHEN EXTRACT(DAY from date)::integer = " + calendar.get(Calendar.DATE) +
				" AND EXTRACT(MONTH from date)::integer = " + (calendar.get(Calendar.MONTH) + 1)+
				" AND EXTRACT(YEAR from date)::integer = " + calendar.get(Calendar.YEAR)+
				" THEN to_char(date, 'HH24:MI') " +
				"ELSE to_char(date, 'DD Month YYYY HH24:MI') " +
				"END"
			,
			"date"
		);
		transposition.put("level", "star_level");
		ResultSet resultSet = query.select(new String[]{"title", "message"}).select(transposition)
			.from(this.tables.getNotification())
			.where("poste", poste)
			.orderBy("id", OrderBy.DESC)
			.limit(limit)
			.get()
			.result();
			while (resultSet.next()) {
				PNotification notification = new PNotification();
				notification.setTitle(resultSet.getString("title"));
				notification.setMessage(resultSet.getString("message"));
				notification.setDate(resultSet.getString("date"));
				notification.setStarLevel(resultSet.getString("star_level"));
				getLast.add(notification);
			}
		return getLast;
	}
	
	@Override
	public ArrayList<PNotification> selectAll(int poste) throws NoSpecifiedTableException, SQLException, NoConnectionException {
		ArrayList<PNotification> getLast = new ArrayList<>();
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		Map<String, String> transposition = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		transposition.put(
			"CASE " +
				"WHEN EXTRAXT(DAY from date)::integer = " + calendar.get(Calendar.DATE) +
				" AND EXTRAXT(MONTH from date)::integer = " + (calendar.get(Calendar.MONTH) + 1)+
				" AND EXTRAXT(YEAR from date)::integer = " + calendar.get(Calendar.YEAR)+
				" THEN to_char(date, 'HH24:MI') " +
				"ELSE to_char(date, 'DD Month YYYY HH24:MI') " +
				"END"
			,
			"date"
		);
		transposition.put("level", "star_level");
		ResultSet resultSet = query.select(new String[]{"title", "message"}).select(transposition)
			                      .from(this.tables.getNotification())
			                      .where("poste", poste)
			                      .orderBy("id", OrderBy.DESC)
			                      .get()
			                      .result();
		while (resultSet.next()) {
			PNotification notification = new PNotification();
			notification.setTitle(resultSet.getString("title"));
			notification.setMessage(resultSet.getString("message"));
			notification.setDate(resultSet.getString("date"));
			notification.setStarLevel(resultSet.getString("star_level"));
			getLast.add(notification);
		}
		return getLast;
	}
	
	@Override
	public boolean clean(int poste) throws SQLException, InvalidExpressionException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query.where("poste", poste).delete(this.tables.getNotification());
	}
	
	@Override
	public int countNew(int poste) throws NoConnectionException, SQLException, NoSpecifiedTableException {
		QueryBuilder query = new PostgreSQLQueryBuilder(this.dao.getConnection());
		return query.where("poste", poste).where("new", true).rowCount(this.tables.getNotification());
	}
}
