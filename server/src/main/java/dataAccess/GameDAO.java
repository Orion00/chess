package dataAccess;

import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;

import java.util.List;

public interface GameDAO {
    public List<GameData> getGames();
    public GameData getGame(String gameName);
    public GameData getGame(Integer gameID);
    public GameData createGame(String gameName) throws DataAccessException;
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor clientColor) throws DataAccessException;
    void clearGames() throws DataAccessException;
}
