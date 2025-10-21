package app.controller;

import app.model.UserRole;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing UserRole entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for UserRole objects.
 * This controller manages user role data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class UserRoleController {
    private List<UserRole> userRoles;

    /**
     * Default constructor that initializes the user role list.
     */
    public UserRoleController() {
        this.userRoles = new ArrayList<>();
    }

    /**
     * Creates and adds a new user role to the system.
     *
     * @param userRole the UserRole object to be created
     * @return the created UserRole object
     */
    public UserRole createUserRole(UserRole userRole) {
        if (userRole != null) {
            userRoles.add(userRole);
        }
        return userRole;
    }

    /**
     * Retrieves all user roles from the system.
     *
     * @return a list of all UserRole objects
     */
    public List<UserRole> getAllUserRoles() {
        return new ArrayList<>(userRoles);
    }

    /**
     * Retrieves a user role by its unique identifier.
     *
     * @param userId the unique identifier of the user role
     * @return an Optional containing the UserRole if found, or empty if not found
     */
    public Optional<UserRole> getUserRoleById(Integer userId) {
        return userRoles.stream()
                .filter(userRole -> userRole.getUser().getUserId().equals(userId))
                .findFirst();
    }

    /**
     * Updates an existing user role in the system.
     *
     * @param userRoleId the unique identifier of the user role to update
     * @param updatedUserRole the UserRole object with updated information
     * @return true if the user role was updated successfully, false otherwise
     */
    public boolean updateUserRole(Integer userRoleId, UserRole updatedUserRole) {
        Optional<UserRole> existingUserRole = getUserRoleById(userRoleId);
        if (existingUserRole.isPresent()) {
            int index = userRoles.indexOf(existingUserRole.get());
            userRoles.set(index, updatedUserRole);
            return true;
        }
        return false;
    }

    /**
     * Deletes a user role from the system.
     *
     * @param userId the unique identifier of the user role to delete
     * @return true if the user role was deleted successfully, false otherwise
     */
    public boolean deleteUserRole(Integer userId) {
        return userRoles.removeIf(userRole -> userRole.getUser().getUserId().equals(userId));
    }
}
