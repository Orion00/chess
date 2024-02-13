package dataAccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(UserData user);
    void createUser(UserData user) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
