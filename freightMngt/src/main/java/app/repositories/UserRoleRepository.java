package app.repositories;

import app.model.UserRole;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing user roles in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class UserRoleRepository {

    private final List<UserRole> userRoles = new ArrayList<>();

    public void addUserRole(UserRole userRole) {
        if (userRole != null) {
            userRoles.add(userRole);
        }
    }

    public List<UserRole> getAllUserRoles() {
        return new ArrayList<>(userRoles);
    }

    public List<UserRole> getUserRolesByUserId(Integer userId) {
        List<UserRole> result = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            if (userRole.getUser() != null && userId != null &&
                userId.equals(userRole.getUser().getUserId())) {
                result.add(userRole);
            }
        }
        return result;
    }

    public boolean deleteUserRole(Integer userId, Integer roleId) {
        return userRoles.removeIf(ur ->
            ur.getUser() != null && ur.getUserRole() != null &&
            userId.equals(ur.getUser().getUserId()) &&
            roleId.equals(ur.getUserRole().getRoleId()));
    }

    public void clear() {
        userRoles.clear();
    }
}
