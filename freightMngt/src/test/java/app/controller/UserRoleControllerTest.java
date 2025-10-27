package app.controller;

import app.model.UserRole;
import app.model.User;
import app.model.Role;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserRoleControllerTest {

    private UserRoleController controller;
    private UserRole userRole1;
    private UserRole userRole2;
    private UserRole userRole3;
    private User user1;
    private User user2;
    private Role role1;
    private Role role2;
    private Role role3;

    @Before
    public void setUp() {
        Repositories.getInstance().getUserRoleRepository().clear();

        controller = new UserRoleController();

        Company company = new Company(1, "Company A", "CA", "123456789");

        user1 = new User(1, "john_doe", "john@example.com", "pass123", company, true);
        user2 = new User(2, "jane_smith", "jane@example.com", "pass456", company, true);

        role1 = new Role(1, "Admin");
        role2 = new Role(2, "Manager");
        role3 = new Role(3, "Operator");

        userRole1 = new UserRole(user1, role1);
        userRole2 = new UserRole(user1, role2);
        userRole3 = new UserRole(user2, role3);
    }

    @Test
    public void testCreateUserRole() {
        UserRole result = controller.createUserRole(userRole1);

        assertNotNull(result);
        assertEquals(userRole1, result);
        assertEquals(1, controller.getAllUserRoles().size());
    }

    @Test
    public void testCreateUserRoleWithNull() {
        UserRole result = controller.createUserRole(null);

        assertNull(result);
        assertEquals(0, controller.getAllUserRoles().size());
    }

    @Test
    public void testGetAllUserRoles() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);
        controller.createUserRole(userRole3);

        List<UserRole> userRoles = controller.getAllUserRoles();

        assertEquals(3, userRoles.size());
        assertTrue(userRoles.contains(userRole1));
        assertTrue(userRoles.contains(userRole2));
        assertTrue(userRoles.contains(userRole3));
    }

    @Test
    public void testGetAllUserRolesEmpty() {
        List<UserRole> userRoles = controller.getAllUserRoles();

        assertNotNull(userRoles);
        assertEquals(0, userRoles.size());
    }

    @Test
    public void testGetUserRolesByUser() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);
        controller.createUserRole(userRole3);

        List<UserRole> user1Roles = controller.getUserRolesByUser(1);

        assertEquals(2, user1Roles.size());
        assertTrue(user1Roles.contains(userRole1));
        assertTrue(user1Roles.contains(userRole2));
        assertFalse(user1Roles.contains(userRole3));
    }

    @Test
    public void testGetUserRolesByUserNoRoles() {
        controller.createUserRole(userRole1);

        List<UserRole> userRoles = controller.getUserRolesByUser(999);

        assertNotNull(userRoles);
        assertEquals(0, userRoles.size());
    }

    @Test
    public void testDeleteUserRoleSuccess() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);
        controller.createUserRole(userRole3);

        boolean result = controller.deleteUserRole(1, 1);

        assertTrue(result);
        assertEquals(2, controller.getAllUserRoles().size());

        List<UserRole> user1Roles = controller.getUserRolesByUser(1);
        assertEquals(1, user1Roles.size());
        assertFalse(user1Roles.contains(userRole1));
        assertTrue(user1Roles.contains(userRole2));
    }

    @Test
    public void testDeleteUserRoleNotFound() {
        controller.createUserRole(userRole1);

        boolean result = controller.deleteUserRole(999, 999);

        assertFalse(result);
        assertEquals(1, controller.getAllUserRoles().size());
    }

    @Test
    public void testClearAllUserRoles() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);
        controller.createUserRole(userRole3);
        assertEquals(3, controller.getAllUserRoles().size());

        controller.clearAllUserRoles();

        assertEquals(0, controller.getAllUserRoles().size());
    }

    @Test
    public void testConstructor() {
        UserRoleController newController = new UserRoleController();

        assertNotNull(newController);
        assertNotNull(newController.getAllUserRoles());
    }

    @Test
    public void testMultipleUserRoleOperations() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);
        controller.createUserRole(userRole3);
        assertEquals(3, controller.getAllUserRoles().size());

        List<UserRole> user1Roles = controller.getUserRolesByUser(1);
        assertEquals(2, user1Roles.size());

        List<UserRole> user2Roles = controller.getUserRolesByUser(2);
        assertEquals(1, user2Roles.size());

        assertTrue(controller.deleteUserRole(1, 2));
        assertEquals(2, controller.getAllUserRoles().size());

        user1Roles = controller.getUserRolesByUser(1);
        assertEquals(1, user1Roles.size());
    }

    @Test
    public void testUserWithMultipleRoles() {
        controller.createUserRole(userRole1);
        controller.createUserRole(userRole2);

        List<UserRole> user1Roles = controller.getUserRolesByUser(1);

        assertEquals(2, user1Roles.size());

        boolean hasAdminRole = user1Roles.stream()
                .anyMatch(ur -> ur.getUserRole().getRoleName().equals("Admin"));
        boolean hasManagerRole = user1Roles.stream()
                .anyMatch(ur -> ur.getUserRole().getRoleName().equals("Manager"));

        assertTrue(hasAdminRole);
        assertTrue(hasManagerRole);
    }
}

