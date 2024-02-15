package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData getUser(UserData user) {
        // Returns the user, if it doesn't exist, returns null
        return users.getOrDefault(user.username(), null);

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Throws error if username already exists, otherwise adds it
        if (users.containsKey(user.username())) {
            throw new DataAccessException("That username already exists");
        } else {
            users.put(user.username(), user);
        }

    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
