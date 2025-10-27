package app.controller;

import app.model.Role;
import app.repositories.RoleRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Role entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Role objects.
 * This controller manages role data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RoleController {
    private final RoleRepository repository;

    /**
     * Default constructor that initializes the controller with the role repository.
     */
    public RoleController() {
        this.repository = Repositories.getInstance().getRoleRepository();
    }

    /**
     * Creates and adds a new role to the system.
     *
     * @param role the Role object to be created
     * @return the created Role object
     */
    public Role createRole(Role role) {
        if (role != null) {
            repository.addRole(role);
        }
        return role;
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return a list of all Role objects
     */
    public List<Role> getAllRoles() {
        return repository.getAllRoles();
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param roleId the unique identifier of the role
     * @return an Optional containing the Role if found, or empty if not found
     */
    public Optional<Role> getRoleById(Integer roleId) {
        Role role = repository.getRoleById(roleId);
        return Optional.ofNullable(role);
    }

    /**
     * Retrieves a role by its name.
     *
     * @param name the name of the role
     * @return an Optional containing the Role if found, or empty if not found
     */
    public Optional<Role> getRoleByName(String name) {
        Role role = repository.getRoleByName(name);
        return Optional.ofNullable(role);
    }

    /**
     * Updates an existing role in the system.
     *
     * @param roleId the unique identifier of the role to update
     * @param updatedRole the Role object with updated information
     * @return true if the role was updated successfully, false otherwise
     */
    public boolean updateRole(Integer roleId, Role updatedRole) {
        if (updatedRole == null) {
            return false;
        }
        return repository.updateRole(roleId, updatedRole);
    }

    /**
     * Deletes a role from the system.
     *
     * @param roleId the unique identifier of the role to delete
     * @return true if the role was deleted successfully, false otherwise
     */
    public boolean deleteRole(Integer roleId) {
        return repository.deleteRole(roleId);
    }

    /**
     * Gets the next available role ID.
     *
     * @return the next role ID to be used
     */
    public Integer getNextRoleId() {
        return repository.getNextRoleId();
    }

    /**
     * Checks if a role with the given name exists in the repository.
     *
     * @param name the role name
     * @return true if a role with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return repository.containsRoleName(name);
    }

    /**
     * Clears all roles from the system.
     */
    public void clearAllRoles() {
        repository.clear();
    }
}

