package lib.querybuilder;

import lib.querybuilder.clauses.Join;
import lib.querybuilder.clauses.Limit;
import lib.querybuilder.clauses.OrderBy;
import lib.querybuilder.clauses.Pair;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.utils.PreparedStatementDataset;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class QueryBuilder {
	protected Connection connection;
	protected PreparedStatement preparedStatement;
	protected String query;
	protected String table;
	protected String[] tables;
	protected final ArrayList<Join> joins;
	protected final ArrayList<String> selectClauses;
	protected final ArrayList<String> whereClauses;
	protected final ArrayList<String> orWhereClauses;
	protected final ArrayList<String> likeClauses;
	protected final ArrayList<String> orLikeClauses;
	protected final ArrayList<Pair> setValues;
	protected final ArrayList<PreparedStatementDataset> whereDataSet;
	protected final ArrayList<PreparedStatementDataset> orWhereDataSet;
	protected final ArrayList<PreparedStatementDataset> likeDataSet;
	protected final ArrayList<PreparedStatementDataset> orLikeDataSet;
	protected final ArrayList<OrderBy> orderBy;
	protected final ArrayList<String> groupBy;
	protected Limit limit;
	protected int index;
	
	{
		this.selectClauses = new ArrayList<>();
		this.whereClauses = new ArrayList<>();
		this.orWhereClauses = new ArrayList<>();
		this.likeClauses = new ArrayList<>();
		this.orLikeClauses = new ArrayList<>();
		this.whereDataSet = new ArrayList<>();
		this.orWhereDataSet = new ArrayList<>();
		this.likeDataSet = new ArrayList<>();
		this.orLikeDataSet = new ArrayList<>();
		this.joins = new ArrayList<>();
		this.setValues = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		this.groupBy = new ArrayList<>();
		this.index = 0;
	}
	
	public QueryBuilder() {}
	
	public QueryBuilder(Connection connection) {this.connection = connection;}
	
	public QueryBuilder openConnection(Connection connection) {
		this.connection = connection;
		return this;
	}
	
	public QueryBuilder setTables(String... tables) {
		this.tables = tables;
		return null;
	}
	
	public <V> QueryBuilder set(String field, V value) {
		this.setValues.add(new Pair(field, value));
		return this;
	}
	
	public <V> QueryBuilder set(Map<String, V> pair) {
		for (String key: pair.keySet()) {
			this.setValues.add(new Pair(key, pair.get(key)));
		}
		return this;
	}
	
	public boolean insert(String table) throws InvalidExpressionException, SQLException {
		if (this.setValues.size() == 0) throw new InvalidExpressionException();
		int valueSize = this.setValues.size();
		this.query = "INSERT INTO";
		this.query += " " + table + "(";
		for (int i = 0; i < valueSize; i++) {
			this.query += this.setValues
				              .get(i)
				              .getKey()
				              .toString() + ", ";
		}
		this.query = this.query.substring(0, this.query.length() - 2) + ")";
		this.query += " VALUES(";
		for (int i = 0; i < valueSize; i++) {
			this.query += "?, ";
		}
		this.query = this.query.substring(0, this.query.length() - 2) + ")";
		this.preparedStatement = this.connection.prepareStatement(this.query);
		this.compileSet();
		return this.preparedStatement.executeUpdate() > 0;
	}
	
	public boolean update(String table) throws InvalidExpressionException, SQLException {
		if (this.setValues.size() == 0) throw new InvalidExpressionException("Aucun champ n'est specifié");
		int valueSize = this.setValues.size();
		this.query = "UPDATE " + table + " SET ";
		for (int i = 0; i < valueSize; i++) {
			this.query += this.setValues
				              .get(i)
				              .getKey()
				              .toString() + " = ?, ";
		}
		this.query = this.query.substring(0, this.query.length() - 2);
		this.preparedStatement = this.connection.prepareStatement(this.query);
		this.compileSet();
		return this.preparedStatement.executeUpdate() > 0;
	}
	
	public boolean delete(String table) throws SQLException {
		this.query = "DELETE FROM " + table;
		if (!(this.whereClauses.size() == 0 && this.orWhereClauses.size() == 0 && this.likeClauses.size() == 0 && this.orLikeClauses.size() == 0)) {
			this.checkAndPrepareClauses();
			this.preparedStatement = this.connection.prepareStatement(this.query);
			this.combineAndCompileClauses();
		} else this.preparedStatement = this.connection.prepareStatement(this.query);
		return this.preparedStatement.executeUpdate() > 0;
	}
	
	public QueryBuilder select() {
		this.selectClauses.add("*");
		return this;
	}
	
	public QueryBuilder select(String query) {
		String[] formattedQuery = query
			                          .trim()
			                          .replaceAll(", ", ",")
			                          .split(",");
		for (String tmp : formattedQuery) this.selectClauses.add(tmp);
		return this;
	}
	
	public QueryBuilder select(int index, String query) {
		String[] formattedQuery = query
			                          .trim()
			                          .replaceAll(", ", ",")
			                          .split(",");
		for (String tmp : formattedQuery) this.selectClauses.add(this.tables[index] + "." + tmp);
		return this;
	}
	
	public QueryBuilder select(String[] query) {
		for (String tmp : query) this.selectClauses.add(tmp);
		return this;
	}
	
	public QueryBuilder select(List<String> query) {
		for (String tmp : query) this.selectClauses.add(tmp);
		return this;
	}
	
	public QueryBuilder select(Map<String, String> select) {
		select.forEach((key, value) -> this.selectClauses.add(key + " AS " + value));
		return this;
	}
	
	public QueryBuilder select(String field, String rename) {
		this.selectClauses.add(field + " AS " + rename);
		return this;
	}
	
	public <V> QueryBuilder where(String field, V value) {
		this.whereClauses.add(field + " = ?");
		this.whereDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder where(Map<String, V> where) {
		for (String key : where.keySet()) {
			this.whereClauses.add(key + " = ?");
			this.whereDataSet.add(new PreparedStatementDataset<V>(where.get(key)));
		}
		return this;
	}
	
	public <V> QueryBuilder orWhere(String field, V value) {
		this.orWhereClauses.add(field + " = ?");
		this.orWhereDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder orWhere(Map<String, V> orWhere) {
		for (String key : orWhere.keySet()) {
			this.orWhereClauses.add(key + " = ?");
			this.orWhereDataSet.add(new PreparedStatementDataset<V>(orWhere.get(key)));
		}
		return this;
	}
	
	public QueryBuilder from(String table) {
		this.table = table;
		return this;
	}
	
	public QueryBuilder from(int table) {
		this.table = this.tables[table];
		return this;
	}
	
	public QueryBuilder join(String table1, String table2) {
		this.joins.add(new Join(Join.INNER, table1.split("\\.")[0], table2.split("\\.")[0], table1.split("\\.")[1], table2.split("\\.")[1]));
		return this;
	}
	
	public QueryBuilder leftJoin(String table1, String table2) {
		this.joins.add(new Join(Join.LEFT, table1.split("\\.")[0], table2.split("\\.")[0], table1.split("\\.")[1], table2.split("\\.")[1]));
		return this;
	}
	
	public QueryBuilder rightJoin(String table1, String table2) {
		this.joins.add(new Join(Join.RIGHT, table1.split("\\.")[0], table2.split("\\.")[0], table1.split("\\.")[1], table2.split("\\.")[1]));
		return this;
	}
	
	public <V> QueryBuilder like(String field, V value) {
		this.likeClauses.add(field + " LIKE ?");
		this.likeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder like(Map<String, V> where) {
		for (String key : where.keySet()) {
			this.likeClauses.add(key + " LIKE ?");
			this.likeDataSet.add(new PreparedStatementDataset<V>(where.get(key)));
		}
		return this;
	}
	
	public <V> QueryBuilder orLike(String field, V value) {
		this.orLikeClauses.add(field + " LIKE ?");
		this.orLikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder orLike(Map<String, V> orLike) {
		for (String key : orLike.keySet()) {
			this.orLikeClauses.add(key + " LIKE ?");
			this.orLikeDataSet.add(new PreparedStatementDataset<V>(orLike.get(key)));
		}
		return this;
	}
	
	/*
	 *
	 *
	 *
	 * LIKE CLAUSE
	 *
	 *
	 *
	 * */
	
	public void combineAndCompileClauses() throws SQLException {
		for (PreparedStatementDataset a : this.whereDataSet) {
			if (a.getValue() instanceof String) this.preparedStatement.setString(++index, (String) a.getValue());
			else if (a.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) a.getValue());
			else if (a.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) a.getValue());
			else if (a.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) a.getValue());
			else if (a.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) a.getValue());
			else if (a.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) a.getValue());
			else if (a.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) a.getValue());
			else if (a.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) a.getValue());
			else if (a.getValue() instanceof java.util.Date) this.preparedStatement.setDate(++index, new java.sql.Date(((java.util.Date) a.getValue()).getTime()));
		}
		for (PreparedStatementDataset b : this.orWhereDataSet) {
			if (b.getValue() instanceof String) this.preparedStatement.setString(++index, (String) b.getValue());
			else if (b.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) b.getValue());
			else if (b.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) b.getValue());
			else if (b.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) b.getValue());
			else if (b.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) b.getValue());
			else if (b.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) b.getValue());
			else if (b.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) b.getValue());
			else if (b.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) b.getValue());
			else if (b.getValue() instanceof java.util.Date) this.preparedStatement.setDate(++index, new java.sql.Date(((java.util.Date) b.getValue()).getTime()));
		}
		for (PreparedStatementDataset c : this.likeDataSet) {
			if (c.getValue() instanceof String) this.preparedStatement.setString(++index, (String) c.getValue());
			else if (c.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) c.getValue());
			else if (c.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) c.getValue());
			else if (c.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) c.getValue());
			else if (c.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) c.getValue());
			else if (c.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) c.getValue());
			else if (c.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) c.getValue());
			else if (c.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) c.getValue());
			else if (c.getValue() instanceof java.util.Date) this.preparedStatement.setDate(++index, new java.sql.Date(((java.util.Date) c.getValue()).getTime()));
		}
		for (PreparedStatementDataset d : this.orLikeDataSet) {
			if (d.getValue() instanceof String) this.preparedStatement.setString(++index, (String) d.getValue());
			else if (d.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) d.getValue());
			else if (d.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) d.getValue());
			else if (d.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) d.getValue());
			else if (d.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) d.getValue());
			else if (d.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) d.getValue());
			else if (d.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) d.getValue());
			else if (d.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) d.getValue());
			else if (d.getValue() instanceof java.util.Date) this.preparedStatement.setDate(++index, new java.sql.Date(((java.util.Date) d.getValue()).getTime()));
		}
	}
	
	public String compiledQuery() throws NoSpecifiedTableException {
		if (this.query == null) compileSelect();
		return this.query;
	}
	
	public void close() throws SQLException {
		if (this.preparedStatement != null) this.preparedStatement.close();
		if (this.connection != null) this.connection.close();
	}
	
	public QueryBuilder orderBy(String field, String ordering) {
		this.orderBy.add(new OrderBy(field, ordering));
		return this;
	}
	
	public QueryBuilder orderBy(String field) {
		this.orderBy.add(new OrderBy(field));
		return this;
	}
	
	public QueryBuilder groupBy(String field) {
		this.groupBy.add(field);
		return this;
	}
	
	public QueryBuilder limit(int limit, int offset) {
		this.limit = new Limit(limit, offset);
		return this;
	}
	
	public QueryBuilder limit(int limit) {
		this.limit = new Limit(limit);
		return this;
	}
	
	public ResultSet result() throws SQLException {
		return this.preparedStatement.executeQuery();
	}
	
	public int rowCount(String table) throws SQLException, NoSpecifiedTableException, NoConnectionException {
		this.query = "SELECT COUNT(*) as count FROM " + table;
		this.checkAndPrepareClauses();
		this.preparedStatement = this.connection.prepareStatement(this.query);
		this.combineAndCompileClauses();
		ResultSet resultSet = this.preparedStatement.executeQuery();
		int rowCount = 0;
		while (resultSet.next()) rowCount = resultSet.getInt("count");
		return rowCount;
	}
	
	public int count(String table) throws SQLException {
		this.query = "SELECT COUNT(*) AS count FROM " + table;
		int count = 0;
		ResultSet resultSet = this.connection
			                      .createStatement()
			                      .executeQuery(this.query);
		if(resultSet.next()) count = resultSet.getInt("count");
		return count;
	}
	
	protected void checkAndPrepareClauses() {
		if (this.whereClauses.size() > 0 || this.orWhereClauses.size() > 0 || this.likeClauses.size() > 0 || this.orLikeClauses.size() > 0) this.query += " WHERE";
		if (this.whereClauses.size() > 0) this.compileWhereClause();
		if (this.orWhereClauses.size() > 0) this.compileOrWhereClause();
		if (this.likeClauses.size() > 0) this.compileLikeClause();
		if (this.orLikeClauses.size() > 0) this.compileOrLikeClause();
		this.query = this.query
			             .replaceAll("  ", " ")
			             .replaceAll("WHERE OR", "WHERE")
			             .replaceAll("WHERE AND", "WHERE");
	}
	
	protected QueryBuilder compileSelect() throws NoSpecifiedTableException {
		if (this.table == null) throw new NoSpecifiedTableException();
		for (Join join : this.joins) if (join.hasNull()) throw new NoSpecifiedTableException();
		this.query = "SELECT ";
		if (this.selectClauses.size() == 0) this.selectClauses.add("*");
		for (String select : this.selectClauses) this.query += select + ", ";
		this.query = this.query.substring(0, this.query.length() - 2);
		this.query += " FROM ";
		this.query += this.table;
		if (this.joins.size() > 0) for (Join join : this.joins) this.query += " " + join;
		this.checkAndPrepareClauses();
		if (this.orderBy.size() > 0) {
			this.query += " ORDER BY ";
			for (OrderBy orderBy : this.orderBy) this.query += orderBy.getField() + " " + orderBy.getOrdering() + ", ";
			this.query = this.query
				             .substring(0, this.query.length() - 2)
				             .replaceAll("  ", " ");
		}
		if (this.groupBy.size() > 0) {
			this.query += " GROUP BY ";
			for (String groupBy : this.groupBy) this.query += groupBy + ", ";
			this.query = this.query
				             .substring(0, this.query.length() - 2)
				             .replaceAll("  ", " ");
		}
		if (this.limit != null) {
			this.query += " LIMIT " + this.limit.getLimit() + " OFFSET " + this.limit.getOffset();
			this.query = this.query.replaceAll("  ", " ");
		}
		return this;
	}
	
	public void compileWhereClause() {
		this.query += " ";
		for (String where : this.whereClauses) this.query += " AND " + where;
	}
	
	public void compileOrWhereClause() {
		this.query += " ";
		for (String where : this.orWhereClauses) this.query += " OR " + where;
	}
	
	public void compileLikeClause() {
		this.query += " ";
		for (String like : this.likeClauses) this.query += " AND " + like;
	}
	
	public void compileOrLikeClause() {
		this.query += " ";
		for (String like : this.orLikeClauses) this.query += " OR " + like;
	}
	
	public QueryBuilder get() throws NoConnectionException, SQLException, NoSpecifiedTableException {
		if (this.connection == null) throw new NoConnectionException();
		this.compileSelect();
		this.preparedStatement = connection.prepareStatement(this.query);
		this.combineAndCompileClauses();
		return this;
	}
	
	protected void compileSet() throws SQLException {
		for (Pair keyValue : this.setValues) {
			if (keyValue.getValue() instanceof String) this.preparedStatement.setString(++index, (String) keyValue.getValue());
			else if (keyValue.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) keyValue.getValue());
			else if (keyValue.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) keyValue.getValue());
			else if (keyValue.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) keyValue.getValue());
			else if (keyValue.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) keyValue.getValue());
			else if (keyValue.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) keyValue.getValue());
			else if (keyValue.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) keyValue.getValue());
			else if (keyValue.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) keyValue.getValue());
			else if (keyValue.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((java.util.Date) keyValue.getValue()).getTime()));
		}
	}
}
