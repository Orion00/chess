package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    public List<GameData> getGames();
    public GameData getGame(String gameName);
    public GameData getGame(Integer gameID);
    public GameData createGame(String gameName);
    public void addParticipant(Integer gameID, String username, String ClientColor);
    void clearGames() throws DataAccessException;
}
