package lib.querybuilder.implementations;

import lib.querybuilder.QueryBuilder;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostgreSQLQueryBuilder implements QueryBuilder {
	private Connection connection;
	private PreparedStatement preparedStatement;
	private String query;
	private String table;
	private String[] tables;
	private final ArrayList<Join> joins;
	private final ArrayList<String> selectClauses;
	private final ArrayList<String> whereClauses;
	private final ArrayList<String> orWhereClauses;
	private final ArrayList<String> likeClauses;
	private final ArrayList<String> orLikeClauses;
	private final ArrayList<Pair> setValues;
	private final ArrayList<PreparedStatementDataset> whereDataSet;
	private final ArrayList<PreparedStatementDataset> orWhereDataSet;
	private final ArrayList<PreparedStatementDataset> likeDataSet;
	private final ArrayList<PreparedStatementDataset> orLikeDataSet;
	private final ArrayList<OrderBy> orderBy;
	private final ArrayList<String> groupBy;
	private Limit limit;
	private int index;
	private final ArrayList<String> iLikeClauses;
	private final ArrayList<String> orILikeClauses;
	private final ArrayList<PreparedStatementDataset> iLikeDataSet;
	private final ArrayList<PreparedStatementDataset> orILikeDataSet;
	
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
		this.iLikeClauses = new ArrayList<>();
		this.orILikeClauses = new ArrayList<>();
		this.iLikeDataSet = new ArrayList<>();
		this.orILikeDataSet = new ArrayList<>();
		this.index = 0;
	}
	
	public PostgreSQLQueryBuilder() {}
	
	public PostgreSQLQueryBuilder(Connection connection) {this.connection = connection;}
	
	@Override
	public QueryBuilder openConnection(Connection connection) {
		this.connection = connection;
		return this;
	}
	
	@Override
	public QueryBuilder setTables(String... tables) {
		this.tables = tables;
		return null;
	}
	
	@Override
	public <V> QueryBuilder set(String field, V value) {
		this.setValues.add(new Pair(field, value));
		return this;
	}
	
	@Override
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
	
	@Override
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
	
	@Override
	public boolean delete(String table) throws InvalidExpressionException, SQLException {
		this.query = "DELETE FROM " + table;
		if (!(this.whereClauses.size() == 0 && this.orWhereClauses.size() == 0 && this.likeClauses.size() == 0 && this.orLikeClauses.size() == 0 && this.iLikeClauses.size() == 0 && this.orILikeClauses.size() == 0)) {
			this.checkAndPrepareClauses();
			this.preparedStatement = this.connection.prepareStatement(this.query);
			this.combineAndCompileClauses();
		} else this.preparedStatement = this.connection.prepareStatement(this.query);
		return this.preparedStatement.executeUpdate() > 0;
	}
	
	@Override
	public QueryBuilder select() {
		this.selectClauses.add("*");
		return this;
	}
	
	@Override
	public QueryBuilder select(String query) {
		String[] formattedQuery = query
			                          .trim()
			                          .replaceAll(", ", ",")
			                          .split(",");
		for (String tmp : formattedQuery) this.selectClauses.add(tmp);
		return this;
	}
	
	@Override
	public QueryBuilder select(int index, String query) {
		String[] formattedQuery = query
			                          .trim()
			                          .replaceAll(", ", ",")
			                          .split(",");
		for (String tmp : formattedQuery) this.selectClauses.add(this.tables[index] + "." + tmp);
		return this;
	}
	
	@Override
	public QueryBuilder select(String[] query) {
		for (String tmp : query) this.selectClauses.add(tmp);
		return this;
	}
	
	@Override
	public QueryBuilder select(List<String> query) {
		for (String tmp : query) this.selectClauses.add(tmp);
		return this;
	}
	
	@Override
	public QueryBuilder select(Map<String, String> select) {
		select.forEach((key, value) -> this.selectClauses.add(key + " AS " + value));
		return this;
	}
	
	@Override
	public QueryBuilder select(String field, String rename) {
		this.selectClauses.add(field + " AS " + rename);
		return this;
	}
	
	@Override
	public <V> QueryBuilder where(String field, V value) {
		this.whereClauses.add(field + " = ?");
		this.whereDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	@Override
	public <V> QueryBuilder where(Map<String, V> where) {
		for (String key : where.keySet()) {
			this.whereClauses.add(key + " = ?");
			this.whereDataSet.add(new PreparedStatementDataset<V>(where.get(key)));
		}
		return this;
	}
	
	@Override
	public <V> QueryBuilder orWhere(String field, V value) {
		this.orWhereClauses.add(field + " = ?");
		this.orWhereDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	@Override
	public <V> QueryBuilder orWhere(Map<String, V> orWhere) {
		for (String key : orWhere.keySet()) {
			this.orWhereClauses.add(key + " = ?");
			this.orWhereDataSet.add(new PreparedStatementDataset<V>(orWhere.get(key)));
		}
		return this;
	}
	
	@Override
	public QueryBuilder from(String table) {
		this.table = table;
		return this;
	}
	
	@Override
	public QueryBuilder from(int table) {
		this.table = this.tables[table];
		return this;
	}
	
	@Override
	public QueryBuilder join(String type, String table1, String table2) {
		this.joins.add(new Join(type, table1.split("\\.")[0], table2.split("\\.")[0], table1.split("\\.")[1], table2.split("\\.")[1]));
		return this;
	}
	
	@Override
	public QueryBuilder join(String type, int index1, String field1, int index2, String field2) {
		this.joins.add(new Join(type, this.tables[index1], this.tables[index2], field1, field2));
		return this;
	}
	
	@Override
	public QueryBuilder join(String table1, String table2) {
		this.joins.add(new Join(table1.split("\\.")[0], table2.split("\\.")[0], table1.split("\\.")[1], table2.split("\\.")[1]));
		return this;
	}
	
	@Override
	public QueryBuilder join(int index1, String field1, int index2, String field2) {
		this.joins.add(new Join(this.tables[index1], this.tables[index2], field1, field2));
		return this;
	}
	
	@Override
	public <V> QueryBuilder like(String field, V value) {
		this.likeClauses.add(field + " LIKE ?");
		this.likeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	@Override
	public <V> QueryBuilder like(Map<String, V> where) {
		for (String key : where.keySet()) {
			this.likeClauses.add(key + " LIKE ?");
			this.likeDataSet.add(new PreparedStatementDataset<V>(where.get(key)));
		}
		return this;
	}
	
	@Override
	public <V> QueryBuilder orLike(String field, V value) {
		this.orLikeClauses.add(field + " LIKE ?");
		this.orLikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	@Override
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
	
	// AND
	public <V> QueryBuilder iLike(String field, V value) {
		this.iLikeClauses.add("AND " + field + " ILIKE ?");
		this.iLikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder iLike(Map<String, V> where) {
		for (String key : where.keySet()) {
			this.likeClauses.add(key + " ILIKE ?");
			this.likeDataSet.add(new PreparedStatementDataset<V>(where.get(key)));
		}
		return this;
	}
	
	// OR
	public <V> QueryBuilder orILike(String field, V value) {
		this.orILikeClauses.add(field + " ILIKE ?");
		this.orILikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> QueryBuilder orILike(Map<String, V> orLike) {
		for (String key : orLike.keySet()) {
			this.likeClauses.add(key + " LIKE ?");
			this.likeDataSet.add(new PreparedStatementDataset<V>(orLike.get(key)));
		}
		return this;
	}
	
	private void compileILikeClause() {
		this.query += " ";
		for (String like : this.iLikeClauses) this.query += " AND " + like;
	}
	
	private void compileOrILikeClause() {
		this.query += " ";
		for (String like : this.orILikeClauses) this.query += " OR " + like;
	}
	
	@Override
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
			else if (a.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) a.getValue()).getTime()));
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
			else if (b.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) b.getValue()).getTime()));
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
			else if (c.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) c.getValue()).getTime()));
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
			else if (d.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) d.getValue()).getTime()));
		}
		for (PreparedStatementDataset e : this.iLikeDataSet) {
			if (e.getValue() instanceof String) this.preparedStatement.setString(++index, (String) e.getValue());
			else if (e.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) e.getValue());
			else if (e.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) e.getValue());
			else if (e.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) e.getValue());
			else if (e.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) e.getValue());
			else if (e.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) e.getValue());
			else if (e.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) e.getValue());
			else if (e.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) e.getValue());
			else if (e.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) e.getValue()).getTime()));
		}
		for (PreparedStatementDataset f : this.orILikeDataSet) {
			if (f.getValue() instanceof String) this.preparedStatement.setString(++index, (String) f.getValue());
			else if (f.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) f.getValue());
			else if (f.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) f.getValue());
			else if (f.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) f.getValue());
			else if (f.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) f.getValue());
			else if (f.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) f.getValue());
			else if (f.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) f.getValue());
			else if (f.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) f.getValue());
			else if (f.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) f.getValue()).getTime()));
		}
	}
	
	@Override
	public String compiledQuery() throws NoSpecifiedTableException {
		if (this.query == null) compileSelect();
		return this.query;
	}
	
	@Override
	public void close() throws SQLException {
		if (this.preparedStatement != null) this.preparedStatement.close();
		if (this.connection != null) this.connection.close();
	}
	
	@Override
	public QueryBuilder orderBy(String field, String ordering) {
		this.orderBy.add(new OrderBy(field, ordering));
		return this;
	}
	
	@Override
	public QueryBuilder orderBy(String field) {
		this.orderBy.add(new OrderBy(field));
		return this;
	}
	
	@Override
	public QueryBuilder groupBy(String field) {
		this.groupBy.add(field);
		return this;
	}
	
	@Override
	public QueryBuilder limit(int limit, int offset) {
		this.limit = new Limit(limit, offset);
		return this;
	}
	
	@Override
	public QueryBuilder limit(int limit) {
		this.limit = new Limit(limit);
		return this;
	}
	
	@Override
	public ResultSet result() throws SQLException {
		return this.preparedStatement.executeQuery();
	}
	
	@Override
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
	
	@Override
	public int count(String table) throws SQLException {
		int count = 0;
		this.query = "SELECT COUNT(*) AS count FROM " + table;
		ResultSet resultSet = this.connection
			                      .createStatement()
			                      .executeQuery(this.query);
		if (resultSet.next()) count = resultSet.getInt(0);
		return count;
	}
	
	private void checkAndPrepareClauses() {
		if (this.whereClauses.size() > 0 || this.orWhereClauses.size() > 0 || this.likeClauses.size() > 0 || this.orLikeClauses.size() > 0 || this.orILikeClauses.size() > 0 || this.iLikeClauses.size() > 0) this.query += " WHERE";
		if (this.whereClauses.size() > 0) this.compileWhereClause();
		if (this.orWhereClauses.size() > 0) this.compileOrWhereClause();
		if (this.likeClauses.size() > 0) this.compileLikeClause();
		if (this.orLikeClauses.size() > 0) this.compileOrLikeClause();
		if (this.iLikeClauses.size() > 0) this.compileILikeClause();
		if (this.orILikeClauses.size() > 0) this.compileOrILikeClause();
		this.query = this.query
			             .replaceAll("  ", " ")
			             .replaceAll("WHERE OR", "WHERE")
			             .replaceAll("WHERE AND", "WHERE");
	}
	
	private QueryBuilder compileSelect() throws NoSpecifiedTableException {
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
	
	@Override
	public void compileWhereClause() {
		this.query += " ";
		for (String where : this.whereClauses) this.query += " AND " + where;
	}
	
	@Override
	public void compileOrWhereClause() {
		this.query += " ";
		for (String where : this.orWhereClauses) this.query += " OR " + where;
	}
	
	@Override
	public void compileLikeClause() {
		this.query += " ";
		for (String like : this.likeClauses) this.query += " AND " + like;
	}
	
	@Override
	public void compileOrLikeClause() {
		this.query += " ";
		for (String like : this.orLikeClauses) this.query += " OR " + like;
	}
	
	@Override
	public QueryBuilder get() throws NoConnectionException, SQLException, NoSpecifiedTableException {
		if (this.connection == null) throw new NoConnectionException();
		this.compileSelect();
		this.preparedStatement = connection.prepareStatement(this.query);
		this.combineAndCompileClauses();
		return this;
	}
	
	private void compileSet() throws SQLException {
		for (Pair keyValue : this.setValues) {
			if (keyValue.getValue() instanceof String) this.preparedStatement.setString(++index, (String) keyValue.getValue());
			else if (keyValue.getValue() instanceof Integer) this.preparedStatement.setInt(++index, (Integer) keyValue.getValue());
			else if (keyValue.getValue() instanceof Float) this.preparedStatement.setFloat(++index, (Float) keyValue.getValue());
			else if (keyValue.getValue() instanceof Double) this.preparedStatement.setDouble(++index, (Double) keyValue.getValue());
			else if (keyValue.getValue() instanceof Long) this.preparedStatement.setLong(++index, (Long) keyValue.getValue());
			else if (keyValue.getValue() instanceof Character) this.preparedStatement.setString(++index, (String) keyValue.getValue());
			else if (keyValue.getValue() instanceof Boolean) this.preparedStatement.setBoolean(++index, (Boolean) keyValue.getValue());
			else if (keyValue.getValue() instanceof Timestamp) this.preparedStatement.setTimestamp(++index, (Timestamp) keyValue.getValue());
			else if (keyValue.getValue() instanceof Date) this.preparedStatement.setDate(++index, new java.sql.Date(((Date) keyValue.getValue()).getTime()));
		}
	}
}
