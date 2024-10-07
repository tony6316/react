package dm;

/**
 * Domain class representing a publisher.
 */
public class Publisher {
    // Attributes representing the details of a publisher
    private int id;
    private String name;

    // Constructor
    public Publisher(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter and setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString method for easy representation
    @Override
    public String toString() {
        return "Publisher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}