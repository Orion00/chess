package dataAccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class DBGameDAO implements GameDAO {
    public DBGameDAO() throws DataAccessException {
        configureDatabase();
    }
    @Override
    public List<GameData> getGames() {
        var result = new ArrayList<GameData>();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID,whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            // Returns null if can't get game
            return null;
        }
        return result;

    }

    @Override
    public GameData getGame(String gameName) {
        if (gameName == null || gameName.isEmpty()) {
            // No game found
            return null;
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID,whiteUsername, blackUsername, gameName, game FROM games WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            // Returns null if can't get game
            return null;
        }
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        if (gameID == null) {
            // No game found
            return null;
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID,whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(gameID));
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException | DataAccessException ex) {
            // Returns null if can't get game
            return null;
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()){
            throw new DataAccessException("bad request");
        } else if (getGame(gameName) != null) {
            throw new DataAccessException("already taken");
        }
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var newGame = new ChessGame();
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        newGame.setBoard(chessBoard);
        newGame.setTeamTurn(ChessGame.TeamColor.WHITE);
        var json = new Gson().toJson(newGame);
        var id = executeUpdate(statement, null, null, gameName, json);
        return new GameData(id, null, null, gameName, newGame);
    }

    @Override
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor clientColor) throws DataAccessException {
        if (gameID == null || username == null) {
            // Invalid input
            throw new DataAccessException("bad request");
        }
        GameData currentGame = getGame(gameID);
        // Check if game exists
        if (currentGame == null) {
            // GameID doesn't exist
            throw new DataAccessException("bad request");
        }
        // Check if someone is already playing
        if ((clientColor == ChessGame.TeamColor.WHITE && currentGame.whiteUsername() != null)
                || (clientColor == ChessGame.TeamColor.BLACK && currentGame.blackUsername() != null)) {
            // Color is already taken in this game
            throw new DataAccessException("already taken");
        }

        GameData updatedGame = createUpdatedGameData(username, clientColor, currentGame);
        updateGames(updatedGame);
    }

    private static GameData createUpdatedGameData(String username, ChessGame.TeamColor clientColor, GameData currentGame) {
        GameData updatedGame;
        if (clientColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(currentGame.gameID(), username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
        } else if (clientColor == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
        } else {
            // Observer
            updatedGame = currentGame;
        }
        return updatedGame;
    }

    @Override
    public void updateGames(GameData updatedGame) throws DataAccessException {
        if (updatedGame == null || updatedGame.gameName() == null || updatedGame.game() == null) {
            // Invalid input
            throw new DataAccessException("bad request");
        }

        GameData currentGame = getGame(updatedGame.gameID());
        if (currentGame == null) {
            // Game doesn't exist
            throw new DataAccessException("bad request");
        }

        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        var json = new Gson().toJson(updatedGame.game());
        executeUpdate(statement, updatedGame.whiteUsername(),updatedGame.blackUsername(),updatedGame.gameName(),json,updatedGame.gameID());
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        executeUpdate(statement);
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
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
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
        for (var statement : createStatements) {
            executeUpdate(statement);
        }
    }
}
