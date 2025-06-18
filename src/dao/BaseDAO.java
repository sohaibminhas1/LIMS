package dao;

import java.sql.*;
import java.util.List;

/**
 * Base Data Access Object interface defining common database operations
 */
public interface BaseDAO<T> {
    
    /**
     * Insert a new entity into the database
     * @param entity The entity to insert
     * @return true if insertion was successful, false otherwise
     */
    boolean insert(T entity);
    
    /**
     * Update an existing entity in the database
     * @param entity The entity to update
     * @return true if update was successful, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Delete an entity from the database by ID
     * @param id The ID of the entity to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(String id);
    
    /**
     * Find an entity by its ID
     * @param id The ID to search for
     * @return The entity if found, null otherwise
     */
    T findById(String id);
    
    /**
     * Get all entities from the database
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Get database connection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    Connection getConnection() throws SQLException;
}
