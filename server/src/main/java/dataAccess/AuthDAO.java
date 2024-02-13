package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData getAuthUser(AuthData auth);
    AuthData createAuth(UserData user) throws DataAccessException;
    void removeAuthUser(AuthData auth) throws DataAccessException;
    void clearAuths() throws DataAccessException;
}
