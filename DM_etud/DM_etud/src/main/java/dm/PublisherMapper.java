package dm;

import dm.Publisher;
import util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Mapper class for managing persistence of Publisher objects.
 */
public class PublisherMapper extends AbstractMapper<Publisher> {
    // Cache for storing Publisher objects
    private Map<Integer, Publisher> cache = new HashMap<>();

    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO Publisher (id, name) VALUES (?, ?)";
    private static final String FIND_MANY_SQL = "SELECT * FROM Publisher WHERE name = ?";

    // Insert a new Publisher
    public void insert(Publisher publisher) throws MapperException {
        try (Connection connection = DB.createDefaultDB().getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setInt(1, publisher.getId());
            statement.setString(2, publisher.getName());
            statement.executeUpdate();
            cache.put(publisher.getId(), publisher);
        } catch (SQLException e) {
            // Logging or re-throwing the exception as a MapperException
            throw new MapperException("Error inserting publisher: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    @Override
    protected List<Publisher> abstractFindMany(String sql, Object[] params) throws MapperException {
        List<Publisher> publishers = new ArrayList<>();
        try (Connection connection = DB.createDefaultDB().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    Publisher publisher = new Publisher(id, name);
                    publishers.add(publisher);
                    cache.put(id, publisher);
                }
            }
        } catch (SQLException e) {
            // Handle the SQL Exception, rethrow as MapperException
            throw new MapperException("Error finding publishers: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
        return publishers;
    }

    // Find Publishers by name
    public List<Publisher> findByName(String name) throws MapperException {
        return abstractFindMany(FIND_MANY_SQL, new Object[]{name});
    }
}
