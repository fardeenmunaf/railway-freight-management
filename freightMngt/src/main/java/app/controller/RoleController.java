package app.controller;

import app.model.Role;
import java.util.ArrayList;
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
    private List<Role> roles;

    /**
     * Default constructor that initializes the role list.
     */
    public RoleController() {
        this.roles = new ArrayList<>();
    }

    /**
     * Creates and adds a new role to the system.
     *
     * @param role the Role object to be created
     * @return the created Role object
     */
    public Role createRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
        return role;
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return a list of all Role objects
     */
    public List<Role> getAllRoles() {
        return new ArrayList<>(roles);
    }

    /**
     * Retrieves a role by its unique identifier.
     *
     * @param roleId the unique identifier of the role
     * @return an Optional containing the Role if found, or empty if not found
     */
    public Optional<Role> getRoleById(Integer roleId) {
        return roles.stream()
                .filter(role -> role.getRoleId().equals(roleId))
                .findFirst();
    }

    /**
     * Updates an existing role in the system.
     *
     * @param roleId the unique identifier of the role to update
     * @param updatedRole the Role object with updated information
     * @return true if the role was updated successfully, false otherwise
     */
    public boolean updateRole(Integer roleId, Role updatedRole) {
        Optional<Role> existingRole = getRoleById(roleId);
        if (existingRole.isPresent()) {
            int index = roles.indexOf(existingRole.get());
            roles.set(index, updatedRole);
            return true;
        }
        return false;
    }

    /**
     * Deletes a role from the system.
     *
     * @param roleId the unique identifier of the role to delete
     * @return true if the role was deleted successfully, false otherwise
     */
    public boolean deleteRole(Integer roleId) {
        return roles.removeIf(role -> role.getRoleId().equals(roleId));
    }
}

