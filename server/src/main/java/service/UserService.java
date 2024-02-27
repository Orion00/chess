package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Objects;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData user) throws DataAccessException{
        // Call Data Access Functions
        try {
            UserData foundUser = userDAO.getUser(user);
            if (foundUser != null) {
                throw new DataAccessException("already taken");
            } else if (Objects.isNull(user.username()) || user.username().isEmpty() ||
                    Objects.isNull(user.password()) || user.password().isEmpty() ||
                    Objects.isNull(user.email()) || user.email().isEmpty()) {
                // Empty username, password, or email
                throw new DataAccessException("bad request");
            }
            userDAO.createUser(user);
            return authDAO.createAuth(user);
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }
    }
    public AuthData login(UserData user) throws DataAccessException {
        // Call Data Access Functions
        try {
            UserData foundUser = userDAO.getUser(user);
            if (foundUser == null) {
                // User doesn't exist
                throw new DataAccessException("unauthorized"); // Used to pass Test Case
                //throw new DataAccessException("bad request");
            }
            if (!user.password().equals(foundUser.password())) {
                throw new DataAccessException("unauthorized");
            }
            return authDAO.createAuth(user);

        } catch (DataAccessException i){
            throw new DataAccessException(i.getMessage());
        }
    }
    public void logout(AuthData auth) throws DataAccessException {
        // Call Data Access Functions
        try {
            if (authDAO.getAuthUser(auth) == null) {
                throw new DataAccessException("unauthorized");
            }
           authDAO.removeAuthUser(auth);
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }
    }
}
