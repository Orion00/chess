package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;

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
                throw new DataAccessException("Username is already taken");
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
                throw new DataAccessException("User doesn't exist");
            }
            if (!user.password().equals(foundUser.password())) {
                throw new DataAccessException("Unauthorized");
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
                throw new DataAccessException("401: Unauthorized");
            }
            authDAO.removeAuthUser(auth);
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }
    }
}
