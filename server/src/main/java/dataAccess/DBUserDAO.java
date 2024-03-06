package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class DBUserDAO implements UserDAO {

    public DBUserDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public UserData getUser(UserData user) {
        if (user == null || user.username() == null || user.password() == null
                || user.username().isEmpty() || user.password().isEmpty()) {
            // No user found
            return null;
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, email, hpassword FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
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
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.email() == null || user.password() == null
                || user.username().isEmpty() || user.email().isEmpty() || user.password().isEmpty()) {
            // TODO: Figure out if this is the right response
            // User is missing fields so we can't create it
            throw new DataAccessException("bad request");
        }
        if (getUser(user) != null) {
            // Username already exists
            throw new DataAccessException("already taken");
        }
        var statement = "INSERT INTO users (username, email, hpassword) VALUES (?, ?, ?)";
        // Password has already been hashed
        executeUpdate(statement, user.username(), user.email(), user.password());
    }

    @Override
    public void clearUsers() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var email = rs.getString("email");
        var hpassword = rs.getString("hpassword");
        return new UserData(username, hpassword, email);
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
