package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.List;

public class Handler {
    private final DatabaseService databaseService;
    private final GameService gameService;
    private final UserService userService;
    private final Gson gson;

    public Handler(DatabaseService databaseService, GameService gameService, UserService userService) {
        this.databaseService = databaseService;
        this.gameService = gameService;
        this.userService = userService;
        this.gson = new Gson();
    }

    public Object clear(Request req, Response res) throws ResponseException {
        try {
            databaseService.clearApp();
            return gson.toJson("{}");
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object register(Request req, Response res) throws ResponseException {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData result = userService.register(user);
            return gson.toJson(result);
        } catch (DataAccessException i){
            throw convertException(i);
        }
    }

    public Object login(Request req, Response res) throws ResponseException {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData result = userService.login(user);
            return gson.toJson(result);
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object logout(Request req, Response res) throws ResponseException {
        try {
            AuthData auth = gson.fromJson(req.headers("Authorization"), AuthData.class);
            userService.logout(auth);
            return gson.toJson("");
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object listGames(Request req, Response res) throws ResponseException {
        try {
            AuthData auth = gson.fromJson(req.headers("Authorization"), AuthData.class);
            List<GameData> games = gameService.ListGames(auth);
            return gson.toJson(games);
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object createGame(Request req, Response res) throws ResponseException {
        try {
            AuthData auth = gson.fromJson(req.headers("Authorization"), AuthData.class);
            String gameName = gson.toJson(req.body());
            GameData game = gameService.createGame(auth, gameName);
            return gson.toJson(game.gameID());
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object joinGame(Request req, Response res) throws ResponseException {
        try {
            // TODO: Figure out how to get two things from the body
            AuthData auth = gson.fromJson(req.headers("Authorization"), AuthData.class);
//            String playerColor = (String)gson.toJson(req.body("playerColor"));
//            Integer gameID = (String)gson.toJson(req.body("gameID"));
//
//            GameData game = gameService.joinGame(auth, playerColor, gameID);
//            return gson.toJson(game.gameID());
            throw new DataAccessException("Orion is stuck");
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    private ResponseException convertException(DataAccessException i) {
        // Converts DataAccessException into Status Codes and Messages
        int statusCode;
        String errorMessage = i.getMessage();
        statusCode = switch (errorMessage) {
            case "bad request" ->
                // Currently using for user that doesn't exist
                // Or creating user without a password
                    400;
            case "unauthorized" ->
                // Wrong auth
                    401;
            case "already taken" ->
                // Used for username not available in register or color not available in game
                    403;
            default ->
                // Catchall
                    500;
        };

        return new ResponseException(statusCode, "Error: "+errorMessage);
    }
}
