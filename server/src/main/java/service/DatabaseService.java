package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

public class DatabaseService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public DatabaseService() {
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.userDAO = new MemoryUserDAO();
    }

    public void clearApp() throws DataAccessException {
        // Call Data Access Functions
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuths();
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }

    }
}
