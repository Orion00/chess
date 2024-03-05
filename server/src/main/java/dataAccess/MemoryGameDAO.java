package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

    private int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public MemoryGameDAO() {

    }

    @Override
    public List<GameData> getGames() {
        List<GameData> allGames = new ArrayList<>(games.values());
        return allGames;
    }

    @Override
    public GameData getGame(String gameName) {
        // Iterates through games for game with same name
        for (GameData game : games.values()) {
            if (gameName.equals(game.gameName())) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        // Iterates through games for game with same ID
        return games.getOrDefault(gameID, null);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if (getGame(gameName) != null) {
            throw new DataAccessException("That game name already exists");
        } else {
            // Autoincrements instead of randomly generating (check if exists, otherwise generate again)
            GameData game = new GameData(nextId,null,null,gameName,new ChessGame());
            games.put(nextId, game);
            nextId++;
            return game;
        }
    }

    @Override
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor clientColor) throws DataAccessException{
        GameData game = getGame(gameID);
        // Check if game exists
        if (game == null) {
            // GameID doesn't exist
            throw new DataAccessException("bad request");
        }
        // Check if someone is already playing
        if ((clientColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null)
                || (clientColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
            // Color is already taken in this game
            throw new DataAccessException("already taken");
        }

        GameData updatedGame;
        if (clientColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (clientColor == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            // Observer
            updatedGame = game;
        }
        updateGames(updatedGame);
    }

    @Override
    public void updateGames(GameData updatedGame) throws DataAccessException {
        if (updatedGame == null) {
            throw new DataAccessException("bad request");
        }
        games.replace(updatedGame.gameID(), updatedGame);
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }
}
