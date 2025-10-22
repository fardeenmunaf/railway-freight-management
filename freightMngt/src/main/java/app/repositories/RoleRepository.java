package app.repositories;

import app.model.Role;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing roles in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RoleRepository {

    private final List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        if (role != null && role.getRoleId() != null) {
            if (!containsRole(role.getRoleId())) {
                roles.add(role);
            }
        }
    }

    public List<Role> getAllRoles() {
        return new ArrayList<>(roles);
    }

    public Role getRoleById(Integer roleId) {
        for (Role role : roles) {
            if (roleId.equals(role.getRoleId())) {
                return role;
            }
        }
        return null;
    }

    public Role getRoleByName(String roleName) {
        for (Role role : roles) {
            if (roleName != null && roleName.equals(role.getRoleName())) {
                return role;
            }
        }
        return null;
    }

    public boolean updateRole(Integer roleId, Role updatedRole) {
        Role existingRole = getRoleById(roleId);
        if (existingRole != null) {
            int index = roles.indexOf(existingRole);
            roles.set(index, updatedRole);
            return true;
        }
        return false;
    }

    public boolean deleteRole(Integer roleId) {
        return roles.removeIf(role -> roleId.equals(role.getRoleId()));
    }

    public Integer getNextRoleId() {
        int maxId = 0;
        for (Role role : roles) {
            if (role.getRoleId() != null && role.getRoleId() > maxId) {
                maxId = role.getRoleId();
            }
        }
        return maxId + 1;
    }

    private boolean containsRole(Integer roleId) {
        return getRoleById(roleId) != null;
    }

    public boolean containsRoleName(String roleName) {
        return getRoleByName(roleName) != null;
    }

    public void clear() {
        roles.clear();
    }
}

