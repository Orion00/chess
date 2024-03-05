package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(UserData user) {
        if (user == null || user.username() == null || user.email() == null || user.password() == null
                || user.username().isEmpty() || user.email().isEmpty() || user.password().isEmpty()) {
            // No user found
            return null;
        }
        // Returns the user, if it doesn't exist, returns null
        return users.getOrDefault(user.username(), null);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.email() == null || user.password() == null
                || user.username().isEmpty() || user.email().isEmpty() || user.password().isEmpty()) {
            // No user found
            throw new DataAccessException("bad request");
        }

        // Throws error if username already exists, otherwise adds it
        if (users.containsKey(user.username())) {
            throw new DataAccessException("already taken");
        } else {
            users.put(user.username(), user);
        }

    }

    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
