package app.controller;

import app.model.User;
import app.model.Company;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserControllerTest {

    private UserController controller;
    private User user1;
    private User user2;
    private User user3;
    private Company company1;
    private Company company2;

    @Before
    public void setUp() {
        Repositories.getInstance().getUserRepository().clear();

        controller = new UserController();

        company1 = new Company(1, "Company A", "CA", "123456789");
        company2 = new Company(2, "Company B", "CB", "987654321");

        user1 = new User(1, "john_doe", "john@example.com", "pass123", company1, true);
        user2 = new User(2, "jane_smith", "jane@example.com", "pass456", company1, true);
        user3 = new User(3, "bob_jones", "bob@example.com", "pass789", company2, false);
    }

    @Test
    public void testCreateUser() {
        User result = controller.createUser(user1);

        assertNotNull(result);
        assertEquals(user1, result);
        assertEquals(1, controller.getAllUsers().size());
    }

    @Test
    public void testCreateUserWithNull() {
        User result = controller.createUser(null);

        assertNull(result);
        assertEquals(0, controller.getAllUsers().size());
    }

    @Test
    public void testGetAllUsers() {
        controller.createUser(user1);
        controller.createUser(user2);
        controller.createUser(user3);

        List<User> users = controller.getAllUsers();

        assertEquals(3, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        assertTrue(users.contains(user3));
    }

    @Test
    public void testGetAllUsersEmpty() {
        List<User> users = controller.getAllUsers();

        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    public void testGetUserByIdFound() {
        controller.createUser(user1);
        controller.createUser(user2);

        Optional<User> result = controller.getUserById(1);

        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
    }

    @Test
    public void testGetUserByIdNotFound() {
        controller.createUser(user1);

        Optional<User> result = controller.getUserById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetUserByUsernameFound() {
        controller.createUser(user1);
        controller.createUser(user2);

        Optional<User> result = controller.getUserByUsername("john_doe");

        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        controller.createUser(user1);

        Optional<User> result = controller.getUserByUsername("unknown_user");

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetUserByUsernameWithNull() {
        controller.createUser(user1);

        Optional<User> result = controller.getUserByUsername(null);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateUserSuccess() {
        controller.createUser(user1);

        User updatedUser = new User(1, "john_doe", "john.doe@example.com", "newpass", company1, true);

        boolean result = controller.updateUser(1, updatedUser);

        assertTrue(result);
        Optional<User> retrievedUser = controller.getUserById(1);
        assertTrue(retrievedUser.isPresent());
        assertEquals("john.doe@example.com", retrievedUser.get().getEmail());
    }

    @Test
    public void testUpdateUserNotFound() {
        controller.createUser(user1);

        boolean result = controller.updateUser(999, user2);

        assertFalse(result);
    }

    @Test
    public void testUpdateUserWithNull() {
        controller.createUser(user1);

        boolean result = controller.updateUser(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteUserSuccess() {
        controller.createUser(user1);
        controller.createUser(user2);
        controller.createUser(user3);

        boolean result = controller.deleteUser(2);

        assertTrue(result);
        assertEquals(2, controller.getAllUsers().size());
        assertFalse(controller.getUserById(2).isPresent());
    }

    @Test
    public void testDeleteUserNotFound() {
        controller.createUser(user1);

        boolean result = controller.deleteUser(999);

        assertFalse(result);
        assertEquals(1, controller.getAllUsers().size());
    }

    @Test
    public void testGetNextUserId() {
        Integer nextId = controller.getNextUserId();

        assertNotNull(nextId);
        assertTrue(nextId > 0);
    }

    @Test
    public void testClearAllUsers() {
        controller.createUser(user1);
        controller.createUser(user2);
        controller.createUser(user3);
        assertEquals(3, controller.getAllUsers().size());

        controller.clearAllUsers();

        assertEquals(0, controller.getAllUsers().size());
    }

    @Test
    public void testContainsUsernameTrue() {
        controller.createUser(user1);
        controller.createUser(user2);

        boolean result = controller.containsUsername("john_doe");

        assertTrue(result);
    }

    @Test
    public void testContainsUsernameFalse() {
        controller.createUser(user1);
        controller.createUser(user2);

        boolean result = controller.containsUsername("unknown_user");

        assertFalse(result);
    }

    @Test
    public void testContainsUsernameWithNull() {
        controller.createUser(user1);

        boolean result = controller.containsUsername(null);

        assertFalse(result);
    }

    @Test
    public void testConstructor() {
        UserController newController = new UserController();

        assertNotNull(newController);
        assertNotNull(newController.getAllUsers());
    }

    @Test
    public void testMultipleUserOperations() {
        controller.createUser(user1);
        controller.createUser(user2);
        controller.createUser(user3);
        assertEquals(3, controller.getAllUsers().size());

        Integer nextId = controller.getNextUserId();
        assertNotNull(nextId);

        User user4 = new User(nextId, "alice_wonder", "alice@example.com", "pass000", company2, true);
        controller.createUser(user4);
        assertEquals(4, controller.getAllUsers().size());

        User updatedUser = new User(2, "jane_smith", "jane.smith@example.com", "newpass456", company1, false);
        assertTrue(controller.updateUser(2, updatedUser));

        assertTrue(controller.deleteUser(1));
        assertEquals(3, controller.getAllUsers().size());

        assertFalse(controller.getUserById(1).isPresent());
        assertTrue(controller.getUserById(2).isPresent());
        assertTrue(controller.getUserById(3).isPresent());
        assertTrue(controller.getUserById(nextId).isPresent());
    }
}

