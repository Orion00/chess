package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auths = new HashMap<>();
    @Override
    public AuthData getAuthUser(AuthData auth) {
        return auths.getOrDefault(auth.username(), null);
    }

    @Override
    public AuthData createAuth(UserData user) {
        // Makes sure there's only 1 authToken per user
        if (auths.containsKey(user.username())) {
            auths.remove(user.username());
        }
        UUID uuid = UUID.randomUUID();
        return new AuthData(uuid, user.username());
    }

    @Override
    public void removeAuthUser(AuthData auth) throws DataAccessException {
        if (getAuthUser(auth) == null) {
            throw new DataAccessException("That AuthToken doesn't exist");
        }
        auths.remove(auth.username());
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }
}
