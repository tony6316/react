package dm;

/**
 * Abstract class representing a domain object with a unique identifier.
 */
public abstract class DomainObject {
    private int id;

    // Constructor
    public DomainObject(int id) {
        this.id = id;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int id) {
        this.id = id;
    }
}
