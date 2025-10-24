package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for User model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class UserTest {

    private User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void testDefaultConstructor() {
        User defaultUser = new User();

        assertNull("User ID should be null", defaultUser.getUserId());
        assertNull("Username should be null", defaultUser.getUsername());
        assertNull("Email should be null", defaultUser.getEmail());
        assertNull("Password should be null", defaultUser.getPassword());
        assertNull("Company ID should be null", defaultUser.getCompanyId());
        assertNull("Is Active should be null", defaultUser.getIsActive());
    }

    @Test
    public void testParameterizedConstructor() {
        Company company = new Company();
        User paramUser = new User(1, "johndoe", "john@example.com", "password123", company, true);

        assertEquals("User ID should be 1", Integer.valueOf(1), paramUser.getUserId());
        assertEquals("Username should be 'johndoe'", "johndoe", paramUser.getUsername());
        assertEquals("Email should be 'john@example.com'", "john@example.com", paramUser.getEmail());
        assertEquals("Password should be 'password123'", "password123", paramUser.getPassword());
        assertEquals("Company ID should match", company, paramUser.getCompanyId());
        assertEquals("Is Active should be true", Boolean.TRUE, paramUser.getIsActive());
    }

    @Test
    public void testSetAndGetUserId() {
        user.setUserId(10);

        assertEquals("User ID should be 10", Integer.valueOf(10), user.getUserId());
    }

    @Test
    public void testSetAndGetUsername() {
        user.setUsername("testuser");

        assertEquals("Username should be 'testuser'", "testuser", user.getUsername());
    }

    @Test
    public void testSetAndGetEmail() {
        user.setEmail("test@example.com");

        assertEquals("Email should be 'test@example.com'", "test@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetPassword() {
        user.setPassword("securepass");

        assertEquals("Password should be 'securepass'", "securepass", user.getPassword());
    }

    @Test
    public void testSetAndGetCompanyId() {
        Company company = new Company();
        user.setCompanyId(company);

        assertEquals("Company ID should match", company, user.getCompanyId());
    }

    @Test
    public void testSetAndGetIsActive() {
        user.setIsActive(false);

        assertEquals("Is Active should be false", Boolean.FALSE, user.getIsActive());
    }

    @Test
    public void testSetUserIdToNull() {
        user.setUserId(5);
        user.setUserId(null);

        assertNull("User ID should be null", user.getUserId());
    }

    @Test
    public void testSetUsernameToNull() {
        user.setUsername("admin");
        user.setUsername(null);

        assertNull("Username should be null", user.getUsername());
    }

    @Test
    public void testSetEmailToNull() {
        user.setEmail("admin@example.com");
        user.setEmail(null);

        assertNull("Email should be null", user.getEmail());
    }

    @Test
    public void testSetPasswordToNull() {
        user.setPassword("pass123");
        user.setPassword(null);

        assertNull("Password should be null", user.getPassword());
    }

    @Test
    public void testSetCompanyIdToNull() {
        Company company = new Company();
        user.setCompanyId(company);
        user.setCompanyId(null);

        assertNull("Company ID should be null", user.getCompanyId());
    }

    @Test
    public void testSetIsActiveToNull() {
        user.setIsActive(true);
        user.setIsActive(null);

        assertNull("Is Active should be null", user.getIsActive());
    }

    @Test
    public void testConstructorWithNullValues() {
        User nullUser = new User(null, null, null, null, null, null);

        assertNull("User ID should be null", nullUser.getUserId());
        assertNull("Username should be null", nullUser.getUsername());
        assertNull("Email should be null", nullUser.getEmail());
        assertNull("Password should be null", nullUser.getPassword());
        assertNull("Company ID should be null", nullUser.getCompanyId());
        assertNull("Is Active should be null", nullUser.getIsActive());
    }

    @Test
    public void testConstructorWithEmptyStrings() {
        Company company = new Company();
        User emptyUser = new User(1, "", "", "", company, true);

        assertEquals("Username should be empty string", "", emptyUser.getUsername());
        assertEquals("Email should be empty string", "", emptyUser.getEmail());
        assertEquals("Password should be empty string", "", emptyUser.getPassword());
    }

    @Test
    public void testToString() {
        Company company = new Company();
        user.setUserId(7);
        user.setUsername("janesmith");
        user.setEmail("jane@example.com");
        user.setPassword("pass789");
        user.setCompanyId(company);
        user.setIsActive(true);

        String result = user.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain userId", result.contains("userId=7"));
        assertTrue("toString should contain username", result.contains("username='janesmith'"));
        assertTrue("toString should contain email", result.contains("email='jane@example.com'"));
        assertTrue("toString should contain isActive", result.contains("isActive=true"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = user.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'User{'", result.contains("User{"));
    }
}

