package dm;

import util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract Mapper class providing common persistence functionality for all mappers.
 * @param <T> The type of domain object to be persisted by the mapper.
 */
public abstract class AbstractMapper<T> {

    // Abstract method to be implemented by subclasses to find multiple objects
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

    // Example of a common method to insert data
    public void insert(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }

    // Example of a common method to update data
    public void update(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }

    // Example of a common method to delete data
    public void delete(String sql, Object[] params) throws SQLException {
        executeUpdate(sql, params);
    }
}
