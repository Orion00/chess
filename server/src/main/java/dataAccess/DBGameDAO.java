package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static java.sql.Types.NULL;

public class DBGameDAO implements GameDAO {
    public DBGameDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public List<GameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(String gameName) {
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        // TODO: Check using getGame if it exists
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var newGame = new ChessGame();
        var json = new Gson().toJson(newGame);
        var id = executeUpdate(statement, null, null, gameName, json);
        return new GameData(id, null, null, gameName, newGame);
    }

    @Override
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor clientColor) throws DataAccessException {

    }

    @Override
    public void clearGames() throws DataAccessException {

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = Integer.parseInt(rs.getString("gameID"));
        var wUsername = rs.getString("whiteUsername");
        var bUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJSON = rs.getString("game");
        var game = new Gson().fromJson(gameJSON, ChessGame.class);
        return new GameData(gameID, wUsername, bUsername, gameName, game);
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` JSON NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameID),
              INDEX(gameName)
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
