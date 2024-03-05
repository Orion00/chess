package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBAuthDAO implements AuthDAO {

    public DBAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public AuthData getAuthUser(AuthData auth) {
        if (auth == null || auth.authToken() == null || auth.username() == null
                || auth.authToken().isEmpty() || auth.username().isEmpty() ) {
            // No user found
            return null;
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            // Returns null if can't get user
            return null;
        }
        return null;
    }

    @Override
    public AuthData createAuth(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.email() == null || user.password() == null
            || user.username().isEmpty() || user.email().isEmpty() || user.password().isEmpty()) {
            // TODO: Figure out if this is the right message
            throw new DataAccessException("bad request");
        }

        String uuid = UUID.randomUUID().toString();
        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, uuid, user.username());
        return new AuthData(uuid, user.username());
    }

    @Override
    public void removeAuthUser(AuthData auth) throws DataAccessException {
        if (getAuthUser(auth) == null) {
            throw new DataAccessException("That AuthToken doesn't exist");
        }
        var statement = "DELETE FROM auths WHERE authToken = ?";
        executeUpdate(statement, auth.authToken());
    }

    @Override
    public void clearAuths() throws DataAccessException {
        var statement = "TRUNCATE TABLE auths";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
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
