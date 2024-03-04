package dataAccess;

import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;

public class DBUserDAO implements UserDAO {

    public DBUserDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public UserData getUser(UserData user) {
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public void clearUsers() throws DataAccessException {

    }

    // Has DBManager create the DB if needed
    // This creates the necessary tables
    private void configureDatabase() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `hpassword` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username),
              INDEX(email)
            )
            """
        };

        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
