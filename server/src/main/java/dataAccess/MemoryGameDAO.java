package dataAccess;

import chess.ChessGame;
import chess.ChessPiece;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<String, GameData> games = new HashMap<>();
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
        return games.getOrDefault(gameName, null);
    }

    @Override
    public GameData getGame(Integer gameID) {
        // Iterates through games for game with same ID
        for (GameData game : games.values()) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if (games.containsKey(gameName)) {
            throw new DataAccessException("That game name already exists");
        } else {
            // TODO: Find a better way to create a gameID other than the size of the HashMap
            GameData game = new GameData(games.size(),null,null,gameName,new ChessGame());
            games.put(gameName, game);
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
            throw new DataAccessException(ClientColor.toString()+" is already taken in this game");
        }

        if (ClientColor == ChessGame.TeamColor.WHITE) {
            GameData updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            games.replace(game.gameName(), updatedGame);
        }
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
