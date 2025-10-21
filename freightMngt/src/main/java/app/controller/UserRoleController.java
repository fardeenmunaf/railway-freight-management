package app.controller;

import app.model.UserRole;
import app.repositories.UserRoleRepository;
import app.repositories.Repositories;
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
    private final UserRoleRepository repository;

    /**
     * Default constructor that initializes the controller with the user role repository.
     */
    public UserRoleController() {
        this.repository = Repositories.getInstance().getUserRoleRepository();
    }

    /**
     * Creates and adds a new user role to the system.
     *
     * @param userRole the UserRole object to be created
     * @return the created UserRole object
     */
    public UserRole createUserRole(UserRole userRole) {
        if (userRole != null) {
            repository.addUserRole(userRole);
        }
        return userRole;
    }

    /**
     * Retrieves all user roles from the system.
     *
     * @return a list of all UserRole objects
     */
    public List<UserRole> getAllUserRoles() {
        return repository.getAllUserRoles();
    }

    /**
     * Retrieves all roles for a specific user.
     *
     * @param userId the user identifier
     * @return a list of all UserRole objects for the specified user
     */
    public List<UserRole> getUserRolesByUser(Integer userId) {
        return repository.getUserRolesByUserId(userId);
    }

    /**
     * Deletes a user role from the system.
     *
     * @param userId the user identifier
     * @param roleId the role identifier
     * @return true if the user role was deleted successfully, false otherwise
     */
    public boolean deleteUserRole(Integer userId, Integer roleId) {
        return repository.deleteUserRole(userId, roleId);
    }

    /**
     * Clears all user roles from the system.
     */
    public void clearAllUserRoles() {
        repository.clear();
    }
}
