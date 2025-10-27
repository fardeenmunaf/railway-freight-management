package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for UserRole model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class UserRoleTest {

    private UserRole userRole;

    @Before
    public void setUp() {
        userRole = new UserRole();
    }

    @Test
    public void testDefaultConstructor() {
        UserRole defaultUserRole = new UserRole();

        assertNull("User should be null", defaultUserRole.getUser());
        assertNull("UserRole should be null", defaultUserRole.getUserRole());
    }

    @Test
    public void testParameterizedConstructor() {
        User user = new User();
        Role role = new Role();

        UserRole paramUserRole = new UserRole(user, role);

        assertEquals("User should match", user, paramUserRole.getUser());
        assertEquals("UserRole should match", role, paramUserRole.getUserRole());
    }

    @Test
    public void testSetAndGetUser() {
        User user = new User();
        userRole.setUser(user);

        assertEquals("User should match", user, userRole.getUser());
    }

    @Test
    public void testSetAndGetUserRole() {
        Role role = new Role();
        userRole.setUserRole(role);

        assertEquals("UserRole should match", role, userRole.getUserRole());
    }

    @Test
    public void testSetUserToNull() {
        User user = new User();
        userRole.setUser(user);
        userRole.setUser(null);

        assertNull("User should be null", userRole.getUser());
    }

    @Test
    public void testSetUserRoleToNull() {
        Role role = new Role();
        userRole.setUserRole(role);
        userRole.setUserRole(null);

        assertNull("UserRole should be null", userRole.getUserRole());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        User user = new User();
        Role role = new Role();

        userRole.setUser(user);
        userRole.setUserRole(role);

        assertEquals("User should match", user, userRole.getUser());
        assertEquals("UserRole should match", role, userRole.getUserRole());
    }

    @Test
    public void testConstructorWithNullValues() {
        UserRole nullUserRole = new UserRole(null, null);

        assertNull("User should be null", nullUserRole.getUser());
        assertNull("UserRole should be null", nullUserRole.getUserRole());
    }

    @Test
    public void testConstructorWithNullUser() {
        Role role = new Role();
        UserRole userRoleWithNullUser = new UserRole(null, role);

        assertNull("User should be null", userRoleWithNullUser.getUser());
        assertEquals("UserRole should match", role, userRoleWithNullUser.getUserRole());
    }

    @Test
    public void testConstructorWithNullRole() {
        User user = new User();
        UserRole userRoleWithNullRole = new UserRole(user, null);

        assertEquals("User should match", user, userRoleWithNullRole.getUser());
        assertNull("UserRole should be null", userRoleWithNullRole.getUserRole());
    }

    @Test
    public void testToString() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("testuser");

        Role role = new Role();
        role.setRoleId(2);
        role.setRoleName("Admin");

        userRole.setUser(user);
        userRole.setUserRole(role);

        String result = userRole.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'UserRole{'", result.contains("UserRole{"));
        assertTrue("toString should contain user", result.contains("user="));
        assertTrue("toString should contain userRole", result.contains("userRole="));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = userRole.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'UserRole{'", result.contains("UserRole{"));
    }
}

