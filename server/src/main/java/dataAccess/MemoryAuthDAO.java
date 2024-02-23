package dataAccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
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
        // Makes sure there's only 1 authToken per user
        // Update: Apparently this isn't intended functionality
//        if (auths.containsKey(user.username())) {
//            auths.remove(user.username());
//        }

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
        auths.remove(auth.username());
    }

    @Override
    public void clearAuths() throws DataAccessException {
        auths.clear();
    }
}
