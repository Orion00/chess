package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class DBAuthDAO implements AuthDAO {

    public DBAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public AuthData getAuthUser(AuthData auth) {
        return null;
    }

    @Override
    public AuthData createAuth(UserData user) {
        return null;
    }

    @Override
    public void removeAuthUser(AuthData auth) throws DataAccessException {

    }

    @Override
    public void clearAuths() throws DataAccessException {

    }

    private void configureDatabase() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username),
              INDEX(authToken)
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
