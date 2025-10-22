package app.controller;

import app.model.User;
import app.repositories.UserRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing User entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for User objects.
 * This controller manages user data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class UserController {
    private final UserRepository repository;

    /**
     * Default constructor that initializes the controller with the user repository.
     */
    public UserController() {
        this.repository = Repositories.getInstance().getUserRepository();
    }

    /**
     * Creates and adds a new user to the system.
     *
     * @param user the User object to be created
     * @return the created User object
     */
    public User createUser(User user) {
        if (user != null) {
            repository.addUser(user);
        }
        return user;
    }

    /**
     * Retrieves all users from the system.
     *
     * @return a list of all User objects
     */
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserById(Integer userId) {
        User user = repository.getUserById(userId);
        return Optional.ofNullable(user);
    }

    /**
     * Retrieves a user by its username.
     *
     * @param username the username of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserByUsername(String username) {
        User user = repository.getUserByUsername(username);
        return Optional.ofNullable(user);
    }

    /**
     * Updates an existing user in the system.
     *
     * @param userId the unique identifier of the user to update
     * @param updatedUser the User object with updated information
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(Integer userId, User updatedUser) {
        return repository.updateUser(userId, updatedUser);
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId the unique identifier of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     */
    public boolean deleteUser(Integer userId) {
        return repository.deleteUser(userId);
    }

    /**
     * Gets the next available user ID.
     *
     * @return the next user ID to be used
     */
    public Integer getNextUserId() {
        return repository.getNextUserId();
    }

    /**
     * Checks if a user with the given username exists in the repository.
     *
     * @param username the username
     * @return true if a user with this username exists, false otherwise
     */
    public boolean containsUsername(String username) {
        return repository.containsUsername(username);
    }

    /**
     * Clears all users from the system.
     */
    public void clearAllUsers() {
        repository.clear();
    }
}

