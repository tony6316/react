package dm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import business.Book;

/**
 * Test class using Surefire Maven plugin
 * Use 'mvn test' to run it
 * The test order is not significant.
 */
public class TestBook {
    private static BookMapper bm = BookMapper.getMapper();
    private static Book JPAbook = new Book("978-1430219569",
            "JPA 2: Mastering the Java™ Persistence API",
            "Keith",
            37.49f);

    /* ***********  CREATE tests *********** */

    public void testInsertNewBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // save a new book in db
        String book1Id = (String) bm.insert(JPAbook);
        // insert must return isbn as key
        assert(book1Id != null && book1Id.equals("978-1430219569"));
    }

    public void testInsertExistingBook() {
        try {
            // clean db
            bm.deleteAll();
            // save a new book in db
            bm.insert(JPAbook);
            // save the same book again and trigger a exception
            bm.insert(JPAbook);
            // the execution should never come so far...
            assert(false);
        }
        catch (BookMapperException be) {
            // an exception was thrown so everything is OK...
            assert(true);
        }
    }

    public void testInsertBookWithoutId() {
        try {
            // clean db
            bm.deleteAll();
            // create a book without an isbn number (key)
            Book badBook = new Book(null,
                    "JPA 2: Mastering the Java™ Persistence API",
                    "Keith",
                    37.49f);
            // save a new book in db
            bm.insert(badBook);
            // the execution should never come so far...
            assert(false);
        }
        catch (BookMapperException be) {
            // an exception was thrown so everything is OK...
            assert(true);
        }
    }

    /* ***********  RETRIEVE tests *********** */

    public void testFindExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // save a new book in db
        bm.insert(JPAbook);
        // find this book...
        Book book1 = (Book) bm.find("978-1430219569");
        // both book objects must be equal...
        assert(book1.equals(JPAbook));
    }

    public void testFindManyExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // save a new book in db
        bm.insert(JPAbook);
        Book anotherJPABook = new Book("978-1484234198",
                "ProJPA 2 in Java EE 8: An In-Depth Guide to Java Persistence APIs",
                "Keith",
                37.49f);
        // save another book by the same author in db
        bm.insert(anotherJPABook);
        // find all books by the same author
        Set<Book> books = bm.findMany("Keith");
        // expected result...
        Set<Book> expectedBooks = new HashSet<Book>(Arrays.asList(JPAbook,anotherJPABook));
        // check that the result is the expected one...
        assert(books.containsAll(expectedBooks));
    }

    public void testFindNotExistingBook() throws BookMapperException {
        // search for a nonexistent book must return null (not an exception)
        Book book1 = (Book) bm.find("XXXXXXXXXXXXXX");
        // this search must failed...
        assert(book1 == null);
    }

    /* ***********  UPDATE tests *********** */

    public void testUpdateExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // save a new book in db
        bm.insert(JPAbook);
        // create a book copy to update the author
        Book bookToUpdate = new Book(JPAbook);
        bookToUpdate.setAuthor("Schincariol");
        // update the book
        bm.update(bookToUpdate);
        // search this updated book
        Book updatedBook = bm.find("978-1430219569");
        // check if the author has been updated
        assert(updatedBook.getAuthor().equals("Schincariol"));
    }

    public void testUpdateNotExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // create a new book not in db
        Book bookToUpdate = new Book("xxxx",
                "Another book",
                "Doe",
                11f);
        try {
            // try to update this unknown book...
            bm.update(bookToUpdate);
            // the execution should never come so far...
            assert(false);
        }
        catch(BookMapperException be) {
            // an exception was thrown so everything is OK...
            assert(true);
        }
    }

    /* ***********  DELETE tests *********** */

    public void testDeleteExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // save a new book in db
        bm.insert(JPAbook);
        // delete this book
        bm.delete(JPAbook);
        Book noBook = bm.find("978-1430219569");
        assert(noBook == null);
    }

    public void testDeleteNotExistingBook() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // create a book that is not in the database...
        Book anotherBook = new Book("xxxx",
                "Another book",
                "Doe",
                11f);
        try {
            // try to delete this unknown book...
            bm.delete(anotherBook);
            // the execution should never come so far...
            assert(false);
        }
        catch(BookMapperException be) {
            // an exception was thrown so everything is OK...
            assert(true);
        }
    }

    public void testDeleteAll() throws BookMapperException {
        // clean db
        bm.deleteAll();
        // are there still books ? (% = any author)
        Set<Book> books = bm.findMany("%");
        assert(books.isEmpty() == true);
    }
}
