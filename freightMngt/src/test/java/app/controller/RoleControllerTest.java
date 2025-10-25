package app.controller;

import app.model.Role;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class RoleControllerTest {

    private RoleController controller;
    private Role role1;
    private Role role2;
    private Role role3;

    @Before
    public void setUp() {
        Repositories.getInstance().getRoleRepository().clear();

        controller = new RoleController();

        role1 = new Role(1, "Admin");
        role2 = new Role(2, "Manager");
        role3 = new Role(3, "Operator");
    }

    @Test
    public void testCreateRole() {
        Role result = controller.createRole(role1);

        assertNotNull(result);
        assertEquals(role1, result);
        assertEquals(1, controller.getAllRoles().size());
    }

    @Test
    public void testCreateRoleWithNull() {
        Role result = controller.createRole(null);

        assertNull(result);
        assertEquals(0, controller.getAllRoles().size());
    }

    @Test
    public void testGetAllRoles() {
        controller.createRole(role1);
        controller.createRole(role2);
        controller.createRole(role3);

        List<Role> roles = controller.getAllRoles();

        assertEquals(3, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
        assertTrue(roles.contains(role3));
    }

    @Test
    public void testGetAllRolesEmpty() {
        List<Role> roles = controller.getAllRoles();

        assertNotNull(roles);
        assertEquals(0, roles.size());
    }

    @Test
    public void testGetRoleByIdFound() {
        controller.createRole(role1);
        controller.createRole(role2);

        Optional<Role> result = controller.getRoleById(1);

        assertTrue(result.isPresent());
        assertEquals(role1, result.get());
    }

    @Test
    public void testGetRoleByIdNotFound() {
        controller.createRole(role1);

        Optional<Role> result = controller.getRoleById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetRoleByNameFound() {
        controller.createRole(role1);
        controller.createRole(role2);

        Optional<Role> result = controller.getRoleByName("Admin");

        assertTrue(result.isPresent());
        assertEquals(role1, result.get());
    }

    @Test
    public void testGetRoleByNameNotFound() {
        controller.createRole(role1);

        Optional<Role> result = controller.getRoleByName("SuperAdmin");

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetRoleByNameWithNull() {
        controller.createRole(role1);

        Optional<Role> result = controller.getRoleByName(null);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateRoleSuccess() {
        controller.createRole(role1);

        Role updatedRole = new Role(1, "SuperAdmin");

        boolean result = controller.updateRole(1, updatedRole);

        assertTrue(result);
        Optional<Role> retrievedRole = controller.getRoleById(1);
        assertTrue(retrievedRole.isPresent());
        assertEquals("SuperAdmin", retrievedRole.get().getRoleName());
    }

    @Test
    public void testUpdateRoleNotFound() {
        controller.createRole(role1);

        boolean result = controller.updateRole(999, role2);

        assertFalse(result);
    }

    @Test
    public void testUpdateRoleWithNull() {
        controller.createRole(role1);

        boolean result = controller.updateRole(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteRoleSuccess() {
        controller.createRole(role1);
        controller.createRole(role2);
        controller.createRole(role3);

        boolean result = controller.deleteRole(2);

        assertTrue(result);
        assertEquals(2, controller.getAllRoles().size());
        assertFalse(controller.getRoleById(2).isPresent());
    }

    @Test
    public void testDeleteRoleNotFound() {
        controller.createRole(role1);

        boolean result = controller.deleteRole(999);

        assertFalse(result);
        assertEquals(1, controller.getAllRoles().size());
    }

    @Test
    public void testGetNextRoleId() {
        Integer nextId = controller.getNextRoleId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllRoles() {
        controller.createRole(role1);
        controller.createRole(role2);
        controller.createRole(role3);
        assertEquals(3, controller.getAllRoles().size());

        controller.clearAllRoles();

        assertEquals(0, controller.getAllRoles().size());
    }

    @Test
    public void testContainsNameTrue() {
        controller.createRole(role1);
        controller.createRole(role2);

        boolean result = controller.containsName("Admin");

        assertTrue(result);
    }

    @Test
    public void testContainsNameFalse() {
        controller.createRole(role1);
        controller.createRole(role2);

        boolean result = controller.containsName("SuperAdmin");

        assertFalse(result);
    }

    @Test
    public void testContainsNameWithNull() {
        controller.createRole(role1);

        boolean result = controller.containsName(null);

        assertFalse(result);
    }

    @Test
    public void testConstructor() {
        RoleController newController = new RoleController();

        assertNotNull(newController);
        assertNotNull(newController.getAllRoles());
    }

    @Test
    public void testMultipleRoleOperations() {
        controller.createRole(role1);
        controller.createRole(role2);
        controller.createRole(role3);
        assertEquals(3, controller.getAllRoles().size());

        Integer nextId = controller.getNextRoleId();
        assertNotNull(nextId);

        Role role4 = new Role(nextId, "Viewer");
        controller.createRole(role4);
        assertEquals(4, controller.getAllRoles().size());

        Role updatedRole = new Role(2, "Senior Manager");
        assertTrue(controller.updateRole(2, updatedRole));

        assertTrue(controller.deleteRole(1));
        assertEquals(3, controller.getAllRoles().size());

        assertFalse(controller.getRoleById(1).isPresent());
        assertTrue(controller.getRoleById(2).isPresent());
        assertTrue(controller.getRoleById(3).isPresent());
        assertTrue(controller.getRoleById(nextId).isPresent());
    }
}

