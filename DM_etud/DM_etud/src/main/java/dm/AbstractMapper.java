package dm;

import util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;

/**
 * Abstract Mapper class providing common persistence functionality for all mappers.
 * @param <T> The type of domain object to be persisted by the mapper.
 */
public abstract class AbstractMapper<T> {

    // Constructor to ensure table creation
    public AbstractMapper(Class<T> clazz) throws MapperException {
        try {
            // Vérification si la table existe déjà avant de tenter de la créer
            String createTableStmt = getCreateTableStmt();
            System.out.println("Requête SQL pour la création de la table : " + createTableStmt);

            // Créer la table uniquement si elle n'existe pas
            createTableIfNotExists(createTableStmt);
            System.out.println("Table '" + clazz.getSimpleName() + "' créée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création de la table : " + e.getMessage());
            throw new MapperException("Erreur lors de la création de la table pour la classe : " + clazz.getSimpleName(), e.getSQLState(), e.getErrorCode());
        }
    }



    // Method to create the table if it doesn't already exist
    private void createTableIfNotExists(String createTableSQL) throws SQLException {
        System.out.println("Requête SQL pour la création de la table : " + createTableSQL);
        try (Connection connection = DB.createDefaultDB().getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        }
    }


    // Abstract method for subclasses to provide the SQL for creating the table
    protected abstract String getCreateTableStmt();

    // Abstract method to be implemented by subclasses for finding multiple objects
    protected abstract List<T> abstractFindMany(String sql, Object[] params) throws SQLException, MapperException;

    // Method to execute an update query (INSERT, UPDATE, DELETE)
    protected void executeUpdate(String sql, Object[] params) throws SQLException {
        try (Connection connection = DB.createDefaultDB().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            statement.executeUpdate();
        }
    }

    // Common method to insert data
    public void insert(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }

    // Common method to update data
    public void update(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }

    // Common method to delete data
    public void delete(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }
}
