package dm;

import business.Book;
import util.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data Mapper class for managing persistence of Book objects.
 */
public class BookMapper extends AbstractMapper<Book> {


    // Cache for storing Book objects
    private Map<String, Book> cache = new HashMap<>();


    // Constructeur de BookMapper
    public BookMapper() throws MapperException {
        super(Book.class); // Passez la classe Book au constructeur de la classe parente
        try (Connection connection = DB.createDB("myDB").getConnection()) {
            // Supprimer la table si elle existe et la recréer
            String dropAndCreateTableSQL = getCreateTableStmt();
            try (PreparedStatement statement = connection.prepareStatement(dropAndCreateTableSQL)) {
                statement.executeUpdate();
                System.out.println("Table 'Book' recréée avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création de la table : " + e.getMessage());
            throw new MapperException("Erreur lors de la création de la table pour la classe Book", e.getSQLState(), e.getErrorCode());
        }
    }

    // Static instance for Singleton pattern
    private static BookMapper instance;

    // SQL Statements
    private static final String INSERT_SQL = "INSERT INTO Book (isbn, title, author, price) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_SQL = "UPDATE Book SET title = ?, author = ?, price = ? WHERE isbn = ?;";
    private static final String DELETE_SQL = "DELETE FROM Book WHERE isbn = ?;";
    private static final String FIND_SQL = "SELECT * FROM Book WHERE isbn = ?;";
    private static final String FIND_MANY_SQL = "SELECT * FROM Book WHERE author = ?;";
    private static final String DELETE_ALL_SQL = "DELETE FROM Book;";


    // Public method to return the single instance of BookMapper


    // Insert a new Book
    public String insert(Book book) throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setDouble(4, book.getPrice());
            statement.executeUpdate();
            cache.put(book.getIsbn(), book); // Add book to cache after insertion
            return book.getIsbn();  // Return ISBN after insertion
        } catch (SQLException e) {
            throw new BookMapperException("Error inserting book with ISBN: " + book.getIsbn() + ". " + e.getMessage(), e.getSQLState(), e.getErrorCode());
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
            cache.put(book.getIsbn(), book);  // Update the cache with new book data
        } catch (SQLException e) {
            throw new BookMapperException("Error updating book with ISBN: " + book.getIsbn() + ". " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Delete a Book by ISBN
    public void delete(String isbn) throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, isbn);
            statement.executeUpdate();
            cache.remove(isbn);  // Remove the book from the cache
        } catch (SQLException e) {
            throw new BookMapperException("Error deleting book with ISBN: " + isbn + ". " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
    }

    // Overloaded delete method to accept a Book object
    public void delete(Book book) throws BookMapperException {
        delete(book.getIsbn());
    }

    // Delete all Books
    public void deleteAll() throws BookMapperException {
        try (Connection connection = DB.createDB("myDB").getConnection()) {
            // Tentative de suppression de toutes les lignes de la table 'Book'
            try (PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL)) {
                int rowsAffected = statement.executeUpdate(); // Renvoie le nombre de lignes affectées
                System.out.println(rowsAffected + " lignes supprimées.");
                cache.clear();  // Vider le cache après suppression
            }
        } catch (SQLException e) {
            // Vérifier si l'erreur provient du fait que la table n'existe pas
            if (e.getSQLState().equals("42X05")) {  // Le code SQL pour "table does not exist" dans Derby
                System.out.println("Table 'Book' n'existe pas. Aucune suppression effectuée.");
            } else {
                throw new BookMapperException("Erreur lors de la suppression de tous les livres : " + e.getMessage(), e.getSQLState(), e.getErrorCode());
            }
        }
    }



    // Find a Book by ISBN
    public Book find(String isbn) throws BookMapperException {
        if (cache.containsKey(isbn)) {
            return cache.get(isbn);  // Return book from cache if it exists
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
                    cache.put(isbn, book);  // Add the book to the cache
                    return book;
                }
            }
        } catch (SQLException e) {
            throw new BookMapperException("Error finding book with ISBN: " + isbn + ". " + e.getMessage(), e.getSQLState(), e.getErrorCode());
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
                    books.add(book);  // Add each found book to the list
                    cache.put(isbn, book);  // Update cache with found book
                }
            }
        } catch (SQLException e) {
            throw new BookMapperException("Error finding books: " + e.getMessage(), e.getSQLState(), e.getErrorCode());
        }
        return books;
    }

    // Public method to return the single instance of BookMapper (Singleton)
    public static BookMapper getMapper() {
        if (instance == null) {
            try {
                instance = new BookMapper();  // Crée l'instance de BookMapper si elle n'existe pas encore
            } catch (MapperException e) {
                // Gestion de l'exception sans la propager
                System.err.println("Erreur lors de l'initialisation de BookMapper : " + e.getMessage());
                e.printStackTrace();
                // Vous pouvez gérer l'exception de manière appropriée ici, par exemple en renvoyant null ou en lançant une RuntimeException
                throw new RuntimeException("Impossible d'initialiser BookMapper", e);
            }
        }
        return instance;
    }


    // Find Books by author and return as Set<Book>
    public Set<Book> findByAuthor(String author) throws BookMapperException {
        List<Book> bookList = abstractFindMany(FIND_MANY_SQL, new Object[]{author});
        return new HashSet<>(bookList);  // Convert List to Set to ensure uniqueness
    }

    // Find Books based on criteria (title or author)
    public Set<Book> findMany(String criteria) throws BookMapperException {
        String sql = "SELECT * FROM Book WHERE title LIKE ? OR author LIKE ?";
        List<Book> bookList = abstractFindMany(sql, new Object[]{"%" + criteria + "%", "%" + criteria + "%"});
        return new HashSet<>(bookList);  // Convert List to Set to ensure uniqueness
    }

    // Méthode pour retourner l'instruction de création de table
    @Override
    protected String getCreateTableStmt() {
        return "DROP TABLE IF EXISTS Book; "
                + "CREATE TABLE Book ("
                + "isbn VARCHAR(20) PRIMARY KEY, "
                + "title VARCHAR(100), "
                + "author VARCHAR(100), "
                + "price DOUBLE);";
    }
}
