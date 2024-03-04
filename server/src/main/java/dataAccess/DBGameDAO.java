package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public class DBGameDAO implements GameDAO {
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
        return null;
    }

    @Override
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor clientColor) throws DataAccessException {

    }

    @Override
    public void clearGames() throws DataAccessException {

    }
}
