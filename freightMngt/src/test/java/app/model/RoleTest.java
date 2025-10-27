package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Role model.
 * Tests constructors, getters, setters, and business logic.
 */
public class RoleTest {

    private Role role;

    @Before
    public void setUp() {
        role = new Role();
    }

    @Test
    public void testDefaultConstructor() {
        Role defaultRole = new Role();

        assertNull("Role ID should be null", defaultRole.getRoleId());
        assertNull("Name should be null", defaultRole.getRoleName());
    }

    @Test
    public void testParameterizedConstructor() {
        Role paramRole = new Role(1, "Administrator");

        assertEquals("Role ID should be 1", Integer.valueOf(1), paramRole.getRoleId());
        assertEquals("Name should be 'Administrator'", "Administrator", paramRole.getRoleName());
    }

    @Test
    public void testSetAndGetRoleId() {
        role.setRoleId(10);

        assertEquals("Role ID should be 10", Integer.valueOf(10), role.getRoleId());
    }

    @Test
    public void testSetAndGetName() {
        role.setRoleName("Manager");

        assertEquals("Name should be 'Manager'", "Manager", role.getRoleName());
    }

    @Test
    public void testSetRoleIdToNull() {
        role.setRoleId(5);
        role.setRoleId(null);

        assertNull("Role ID should be null", role.getRoleId());
    }

    @Test
    public void testSetNameToNull() {
        role.setRoleName("Test Role");
        role.setRoleName(null);

        assertNull("Name should be null", role.getRoleName());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        role.setRoleId(3);
        role.setRoleName("Operator");

        assertEquals("Role ID should be 3", Integer.valueOf(3), role.getRoleId());
        assertEquals("Name should be 'Operator'", "Operator", role.getRoleName());
    }

    @Test
    public void testConstructorWithNullValues() {
        Role nullRole = new Role(null, null);

        assertNull("Role ID should be null", nullRole.getRoleId());
        assertNull("Name should be null", nullRole.getRoleName());
    }

    @Test
    public void testConstructorWithEmptyName() {
        Role emptyNameRole = new Role(1, "");

        assertEquals("Role ID should be 1", Integer.valueOf(1), emptyNameRole.getRoleId());
        assertEquals("Name should be empty string", "", emptyNameRole.getRoleName());
    }

    @Test
    public void testToString() {
        role.setRoleId(4);
        role.setRoleName("Manager");

        String result = role.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain roleId", result.contains("roleId=4"));
        assertTrue("toString should contain name", result.contains("name='Manager'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = role.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Role{'", result.contains("Role{"));
    }
}

