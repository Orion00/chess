package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.JoiningGameData;
import model.UserData;

import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public List<GameData> ListGames(AuthData auth) throws DataAccessException {
        List<GameData> games;
        // Call Data Access Functions
        try {
            getAuthUser(auth);
            games = gameDAO.getGames();
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }

        return games;

    }
    public GameData createGame(AuthData auth, String newGameName) throws DataAccessException {
        // Call Data Access Functions
        try {
            getAuthUser(auth);
            if (gameDAO.getGame(newGameName) == null) {
                return gameDAO.createGame(newGameName);
            } else {
                throw new DataAccessException("Game already exists");
            }

        } catch  (DataAccessException i){
            throw new DataAccessException(i.getMessage());
        }
    }
    public void joinGame(AuthData auth, JoiningGameData data) throws DataAccessException{
        // Call Data Access Functions
        try {
            AuthData user = getAuthUser(auth);
            ChessGame.TeamColor color;
            if (data.playerColor() == null || data.playerColor().isEmpty()) {
                color = null;
            } else if (data.playerColor().equals("WHITE")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (data.playerColor().equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                throw new DataAccessException("Invalid color entered");
            }
            gameDAO.addParticipant(data.gameID(),user.username(), color);
        } catch (DataAccessException i) {
            throw new DataAccessException(i.getMessage());
        }
    }

    private AuthData getAuthUser(AuthData auth) throws DataAccessException {
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }
        AuthData authData = authDAO.getAuthUser(auth);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
            return authData;
    }
}
