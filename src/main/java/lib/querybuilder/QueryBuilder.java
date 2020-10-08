package lib.querybuilder;

import lib.querybuilder.exceptions.InvalidExpressionException;
import lib.querybuilder.exceptions.NoConnectionException;
import lib.querybuilder.exceptions.NoSpecifiedTableException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface QueryBuilder {
	QueryBuilder openConnection(Connection connection);
	
	QueryBuilder setTables(String... tables);
	
	/*
	 *
	 *
	 *
	 * SETTING VALUES
	 *
	 *
	 *
	 * */
	
	<V> QueryBuilder set(String field, V value);
	
	/*
	 *
	 *
	 *
	 * INSERT/UPDATE/DELETE
	 *
	 *
	 *
	 * */
	
	boolean insert(String table) throws InvalidExpressionException, SQLException;
	
	boolean update(String table) throws InvalidExpressionException, SQLException;
	
	boolean delete(String table) throws InvalidExpressionException;
	
	/*
	 *
	 *
	 *
	 * SELECT CLAUSE
	 *
	 *
	 *
	 * */
	
	QueryBuilder select();
	
	QueryBuilder select(String query);
	
	QueryBuilder select(int index, String query);
	
	QueryBuilder select(String[] query);
	
	QueryBuilder select(List<String> query);
	
	QueryBuilder select(Map<String, String> select);
	
	QueryBuilder select(String field, String rename);
	
	/*
	 *
	 *
	 *
	 * WHERE CLAUSE
	 *
	 *
	 *
	 * */
	
	// AND
	QueryBuilder where(String field, String value);
	
	QueryBuilder where(int index, String field, String value);
	
	QueryBuilder where(String field, int value);
	
	QueryBuilder where(String field, float value);
	
	QueryBuilder where(String field, double value);
	
	QueryBuilder where(String field, long value);
	
	QueryBuilder where(String field, char value);
	
	QueryBuilder where(String field, boolean value);
	
	QueryBuilder where(String field, Date value);
	
	<V> QueryBuilder where(Map<String, V> where);
	
	// OR
	QueryBuilder orWhere(String field, String value);
	
	QueryBuilder orWhere(int index, String field, String value);
	
	QueryBuilder orWhere(String field, int value);
	
	QueryBuilder orWhere(String field, float value);
	
	QueryBuilder orWhere(String field, double value);
	
	QueryBuilder orWhere(String field, long value);
	
	QueryBuilder orWhere(String field, char value);
	
	QueryBuilder orWhere(String field, boolean value);
	
	QueryBuilder orWhere(String field, Date value);
	
	<V> QueryBuilder orWhere(Map<String, V> orWhere);
	
	/*
	 *
	 *
	 *
	 * FROM CLAUSE
	 *
	 *
	 *
	 * */
	
	QueryBuilder from(String table);
	
	QueryBuilder from(int table);
	
	/*
	 *
	 *
	 *
	 * JOIN CLAUSE
	 *
	 *
	 *
	 * */
	
	QueryBuilder join(String type, String table1, String table2);
	
	QueryBuilder join(String type, int index1, String field1, int index2, String field2);
	
	QueryBuilder join(String table1, String table2);
	
	QueryBuilder join(int index1, String field1, int index2, String field2);
	
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
	QueryBuilder like(String field, String value);
	
	QueryBuilder like(int index, String field, String value);
	
	QueryBuilder like(String field, int value);
	
	QueryBuilder like(String field, float value);
	
	QueryBuilder like(String field, double value);
	
	QueryBuilder like(String field, long value);
	
	QueryBuilder like(String field, char value);
	
	QueryBuilder like(String field, boolean value);
	
	QueryBuilder like(String field, Date value);
	
	<V> QueryBuilder like(Map<String, V> where);
	
	// OR
	QueryBuilder orLike(String field, String value);
	
	QueryBuilder orLike(int index, String field, String value);
	
	QueryBuilder orLike(String field, int value);
	
	QueryBuilder orLike(String field, float value);
	
	QueryBuilder orLike(String field, double value);
	
	QueryBuilder orLike(String field, long value);
	
	QueryBuilder orLike(String field, char value);
	
	QueryBuilder orLike(String field, boolean value);
	
	QueryBuilder orLike(String field, Date value);
	
	<V> QueryBuilder orLike(Map<String, V> orLike);
	
	/*
	 *
	 *
	 *
	 *
	 * GETTING STATEMENTS
	 *
	 *
	 *
	 * */
	
	void compileWhereClause();
	
	void compileOrWhereClause();
	
	void compileLikeClause();
	
	void compileOrLikeClause();
	
	QueryBuilder get() throws NoConnectionException, SQLException, NoSpecifiedTableException;
	
	void combineLikeAndWhereClauses() throws SQLException;
	
	String compiledQuery() throws NoSpecifiedTableException;
	
	void close() throws SQLException;
	
	QueryBuilder orderBy(String field, String ordering);
	
	QueryBuilder orderBy(String field);
	
	QueryBuilder groupBy(String field);
	
	QueryBuilder limit(int limit, int offset);
	
	QueryBuilder limit(int limit);
	
	ResultSet result() throws SQLException;
	
	int rowCount() throws SQLException;
	
	int count() throws SQLException;
	
	int count(String table) throws SQLException;
	
	void checkClauses();
}
