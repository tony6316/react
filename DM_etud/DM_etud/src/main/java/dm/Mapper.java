package dm;

import java.util.Set;

/**
 *  Define operations must be implemented by a subclass of AbstractMapper
 */
public interface Mapper<T,E extends MapperException> {
    /**
     * Insert a new object in database
     *
     * @param newObject object to insert
     * @return oid
     * @throws E if something goes wrong...
     */
    Object insert(T newObject) throws E;

    /**
     * Find an object with oid
     *
     * @param oid id (primary key)
     * @return a object matching oid
     * @throws E if something goes wrong...
     */
    T find(Object oid) throws E;

    /**
     * Find many objects with a criterion
     *
     * @return a set of objects matching the criterion
     * @throws E if something goes wrong...
     */
    Set<T> findMany(Object criterion) throws E;

    /**
     * Update an object
     *
     * @param objToUpdate object to update
     * @throws E if something goes wrong...
     */
    void update(T objToUpdate) throws E;

    /**
     * Delete an object
     *
     * @param objToDelete object to delete
     * @throws E if something goes wrong...
     */
    void delete(T objToDelete) throws E;

    /**
     * Delete all objects
     *
     * @throws E if something goes wrong...
     */
    void deleteAll() throws E;
}
