package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Encapsulate JDBC settings for database access
 */
public class DB {
    private String driver;
    private String URLdb;
    private Connection connection;

    private DB(String dbName) throws ClassNotFoundException, SQLException {
        // Derby JDBC driver
        //this.driver = "org.apache.derby.jdbc.ClientDriver";
        this.driver = "org.apache.derby.client.ClientAutoloadedDriver";
        // JDBC URL to access Derby database (db will be created if it doesn't exist)
        this.URLdb = "jdbc:derby://localhost:1527/" + dbName + ";create=true";
        // driver loading
        Class.forName(driver);
        // connection to database
        this.connection = DriverManager.getConnection(URLdb);
    }

    /**
     * Factory method to create a DB object
     * @param dbName database name
     * @return DB object used to create prepared statement
     */
    public static DB createDB(String dbName) {
        if (dbName == null || "".equals(dbName))
            throw new IllegalArgumentException("DB:: Database name is null or empty");
        else {
            try {
                return new DB(dbName);
            } catch (Exception ex) {
                throw new RuntimeException("DB:: " + ex.getMessage());
            }
        }
    }

    /**
     * Factory method to create a DB object with default name 'faizandier'
     * @return DB object used to create prepared statement
     */
    public static DB createDefaultDB() {
        return createDB("faizandier");
    }

    /**
     * Get the database connection
     * @return the current connection
     */
    public Connection getConnection() {
        return this.connection;
    }

    /**
     * Test if a table exists in the database.
     * @param tableName name of the table
     * @return true if the table exists.
     * @throws SQLException if connection failed.
     */
    public boolean isTableExist(String tableName)
            throws SQLException {
        DatabaseMetaData dbmd = this.connection.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, tableName.toUpperCase(), null);
        return rs.next();
    }

    /**
     * Creates a table from a string containing the SQL code allowing the creation.
     * @param createSQLStatement
     * @throws SQLException for wrong statement
     */
    public void createTable(String createSQLStatement)
            throws SQLException {
        Statement createStmt = this.connection.createStatement();
        createStmt.execute(createSQLStatement);
    }

    /**
     * Create a SQL prepared (templated) statement
     * @param stmtString SQL statement (with '?' for parameters)
     * @return a prepared statement
     * @throws SQLException for wrong statement
     */
    public PreparedStatement prepare(String stmtString)
            throws SQLException {
        return this.connection.prepareStatement(stmtString);
    }
}
