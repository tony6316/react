package dm;

import business.Book;
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
 * Data Mapper class for managing persistence of Book objects.
 */
public class BookMapper extends AbstractMapper {
    // Cache for storing Book objects
    private Map<String, Book> cache = new HashMap<>();

    // Static instance for Singleton pattern
    private static BookMapper instance;

    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO Book (isbn, title, author, price) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Book SET title = ?, author = ?, price = ? WHERE isbn = ?";
    private static final String DELETE_SQL = "DELETE FROM Book WHERE isbn = ?";
    private static final String FIND_SQL = "SELECT * FROM Book WHERE isbn = ?";
    private static final String FIND_MANY_SQL = "SELECT * FROM Book WHERE author = ?";
    private static final String DELETE_ALL_SQL = "DELETE FROM Book";

    // Private constructor for Singleton
    private BookMapper() {
    }

    // Public method to return the single instance of BookMapper
    public static BookMapper getMapper() {
        if (instance == null) {
            instance = new BookMapper();
        }
        return instance;
    }

    // Insert a new Book
    public String insert(Book book) throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setDouble(4, book.getPrice());
            statement.executeUpdate();
            cache.put(book.getIsbn(), book);
            return book.getIsbn();  // Retourner l'ISBN après insertion
        } catch (SQLException e) {
            throw new BookMapperException("Error inserting book: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Update an existing Book
    public void update(Book book) throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setDouble(3, book.getPrice());
            statement.setString(4, book.getIsbn());
            statement.executeUpdate();
            cache.put(book.getIsbn(), book);
        } catch (SQLException e) {
            throw new BookMapperException("Error updating book: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Delete a Book by ISBN
    public void delete(String isbn) throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, isbn);
            statement.executeUpdate();
            cache.remove(isbn);
        } catch (SQLException e) {
            throw new BookMapperException("Error deleting book: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Delete all Books
    public void deleteAll() throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL)) {
            statement.executeUpdate();
            cache.clear();
        } catch (SQLException e) {
            throw new BookMapperException("Error deleting all books: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Find a Book by ISBN
    public Book find(String isbn) throws BookMapperException {
        if (cache.containsKey(isbn)) {
            return cache.get(isbn);
        }
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_SQL)) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    double price = resultSet.getDouble("price");
                    Book book = new Book(isbn, title, author, price);
                    cache.put(isbn, book);
                    return book;
                }
            }
        } catch (SQLException e) {
            throw new BookMapperException("Error finding book: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
        return null;
    }

    // Find multiple Books by author
    @Override
    protected List<Book> abstractFindMany(String sql, Object[] params) throws BookMapperException {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String isbn = resultSet.getString("isbn");
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    double price = resultSet.getDouble("price");
                    Book book = new Book(isbn, title, author, price);
                    books.add(book);
                    cache.put(isbn, book);
                }
            }
        } catch (SQLException e) {
            throw new BookMapperException("Error finding books: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
        return books;
    }

    // Find Books by author
    public List<Book> findByAuthor(String author) throws BookMapperException {
        return abstractFindMany(FIND_MANY_SQL, new Object[]{author});
    }
}
