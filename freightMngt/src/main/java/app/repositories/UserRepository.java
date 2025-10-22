package app.repositories;

import app.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing users in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public void addUser(User user) {
        if (user != null && user.getUserId() != null) {
            if (!containsUser(user.getUserId())) {
                users.add(user);
            }
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(Integer userId) {
        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                return user;
            }
        }
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (username != null && username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public boolean updateUser(Integer userId, User updatedUser) {
        User existingUser = getUserById(userId);
        if (existingUser != null) {
            int index = users.indexOf(existingUser);
            users.set(index, updatedUser);
            return true;
        }
        return false;
    }

    public boolean deleteUser(Integer userId) {
        return users.removeIf(user -> userId.equals(user.getUserId()));
    }

    public Integer getNextUserId() {
        int maxId = 0;
        for (User user : users) {
            if (user.getUserId() != null && user.getUserId() > maxId) {
                maxId = user.getUserId();
            }
        }
        return maxId + 1;
    }

    private boolean containsUser(Integer userId) {
        return getUserById(userId) != null;
    }

    public boolean containsUsername(String username) {
        return getUserByUsername(username) != null;
    }

    public void clear() {
        users.clear();
    }
}

