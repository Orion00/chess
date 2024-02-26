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

//        for (GameData game : games.values()) {
//            allGames.add(game);
//        }
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
            // TODO: Find a better way to create a gameID other than the size of the HashMap
            // Randomly generate (check if exists, otherwise generate again) or autoincrement
            GameData game = new GameData(nextId,null,null,gameName,new ChessGame());
            games.put(nextId, game);
            nextId++;
            return game;
        }
    }

    @Override
    public void addParticipant(Integer gameID, String username, ChessGame.TeamColor ClientColor) throws DataAccessException{
        GameData game = getGame(gameID);
        // Check if game exists
        if (game == null) {
            throw new DataAccessException("GameID doesn't exist");
        }
        // Check if someone is already playing
        // TODO: See if this needs to check if same player is trying to join their own game
        if ((ClientColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null)
                || (ClientColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
            throw new DataAccessException(ClientColor+" is already taken in this game");
        }

        GameData updatedGame;
        if (ClientColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (ClientColor == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            // Observer
            updatedGame = game;
        }
        games.replace(gameID, updatedGame);
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }
}
