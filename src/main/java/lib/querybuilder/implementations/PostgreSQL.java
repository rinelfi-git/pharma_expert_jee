package lib.querybuilder.implementations;

import lib.querybuilder.QueryBuilder;
import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;
import lib.querybuilder.utils.PreparedStatementDataset;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PostgreSQL extends QueryBuilder {
	protected final ArrayList<String> iLikeClauses;
	protected final ArrayList<String> orILikeClauses;
	protected final ArrayList<PreparedStatementDataset> iLikeDataSet;
	protected final ArrayList<PreparedStatementDataset> orILikeDataSet;
	
	{
		this.iLikeClauses = new ArrayList<>();
		this.orILikeClauses = new ArrayList<>();
		this.iLikeDataSet = new ArrayList<>();
		this.orILikeDataSet = new ArrayList<>();
		this.index = 0;
	}
	
	public PostgreSQL() {}
	
	public PostgreSQL(Connection connection) {super(connection);}
	
	@Override
	public PostgreSQL openConnection(Connection connection) {
		this.connection = connection;
		return this;
	}
	
	/*
	 *
	 *
	 *
	 * ILIKE CLAUSE
	 *
	 *
	 *
	 * */
	
	// AND
	public <V> PostgreSQL iLike(String field, V value) {
		this.iLikeClauses.add(field + " ILIKE ?");
		this.iLikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> PostgreSQL iLike(Map<String, V> clause) {
		clause.forEach((key, value) -> {
			this.iLikeClauses.add(key + " ILIKE ?");
			this.iLikeDataSet.add(new PreparedStatementDataset<V>(value));
		});
		return this;
	}
	
	// OR
	public <V> PostgreSQL orILike(String field, V value) {
		this.orILikeClauses.add(field + " ILIKE ?");
		this.orILikeDataSet.add(new PreparedStatementDataset<V>(value));
		return this;
	}
	
	public <V> PostgreSQL orILike(Map<String, V> clause) {
		clause.forEach((key, value) -> {
			this.orILikeClauses.add(key + " LIKE ?");
			this.orILikeDataSet.add(new PreparedStatementDataset<V>(value));
		});
		return this;
	}
	
	protected void compileILikeClause() {
		this.query += " ";
		for (String ilike : this.iLikeClauses) this.query += " AND " + ilike;
	}
	
	protected void compileOrILikeClause() {
		this.query += " ";
		for (String ilike : this.orILikeClauses) this.query += " OR " + ilike;
	}
	
	@Override
	public void prepareClauses() throws SQLException {
		super.prepareClauses();
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
	protected void compileClauses() {
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
	
	@Override
	public QueryBuilder get() throws NoConnectionException, SQLException, NoSpecifiedTableException {
		if (this.connection == null) throw new NoConnectionException();
		this.compileSelect();
		this.preparedStatement = connection.prepareStatement(this.query);
		this.prepareClauses();
		return this;
	}
}
