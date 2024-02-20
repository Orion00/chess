package dataAccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    AuthData getAuthUser(AuthData auth);
    AuthData createAuth(UserData user);
    void removeAuthUser(AuthData auth) throws DataAccessException;
    void clearAuths() throws DataAccessException;
}
