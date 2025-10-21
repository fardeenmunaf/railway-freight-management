package app.controller;

import app.model.User;
import java.util.ArrayList;
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
    private List<User> users;

    /**
     * Default constructor that initializes the user list.
     */
    public UserController() {
        this.users = new ArrayList<>();
    }

    /**
     * Creates and adds a new user to the system.
     *
     * @param user the User object to be created
     * @return the created User object
     */
    public User createUser(User user) {
        if (user != null) {
            users.add(user);
        }
        return user;
    }

    /**
     * Retrieves all users from the system.
     *
     * @return a list of all User objects
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param userId the unique identifier of the user
     * @return an Optional containing the User if found, or empty if not found
     */
    public Optional<User> getUserById(Integer userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    /**
     * Updates an existing user in the system.
     *
     * @param userId the unique identifier of the user to update
     * @param updatedUser the User object with updated information
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(Integer userId, User updatedUser) {
        Optional<User> existingUser = getUserById(userId);
        if (existingUser.isPresent()) {
            int index = users.indexOf(existingUser.get());
            users.set(index, updatedUser);
            return true;
        }
        return false;
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId the unique identifier of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     */
    public boolean deleteUser(Integer userId) {
        return users.removeIf(user -> user.getUserId().equals(userId));
    }
}

