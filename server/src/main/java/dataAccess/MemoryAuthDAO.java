package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();
    @Override
    public AuthData getAuthUser(AuthData auth) {
        return auths.getOrDefault(auth.authToken(), null);
    }

    @Override
    public AuthData createAuth(UserData user) {
        String uuid = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(uuid, user.username());
        auths.put(uuid, newAuth);
        return newAuth;
    }

    @Override
    public void removeAuthUser(AuthData auth) throws DataAccessException {
        if (getAuthUser(auth) == null) {
            throw new DataAccessException("That AuthToken doesn't exist");
        }
        auths.remove(auth.authToken());
    }

    @Override
    public void clearAuths() throws DataAccessException {
        auths.clear();
    }
}
