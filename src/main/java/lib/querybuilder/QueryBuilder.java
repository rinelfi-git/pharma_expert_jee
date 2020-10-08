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
	<V> QueryBuilder where(String field, V value);
	
	<V> QueryBuilder where(Map<String, V> where);
	
	// OR
	<V> QueryBuilder orWhere(String field, V value);
	
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
	<V> QueryBuilder like(String field, V value);
	
	<V> QueryBuilder like(Map<String, V> where);
	
	// OR
	<V> QueryBuilder orLike(String field, V value);
	
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
	
	int rowCount() throws SQLException, NoSpecifiedTableException, NoConnectionException;
	
	int count() throws SQLException;
	
	int count(String table) throws SQLException;
	
	void checkClauses();
}
