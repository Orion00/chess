package handler;

import com.google.gson.*;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.*;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
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
            return "{}";
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
            String authToken = req.headers("Authorization");
            userService.logout(new AuthData(authToken, null));
            return "{}";
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object listGames(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("Authorization");
            List<GameData> games = gameService.listGames(new AuthData(authToken, null));
            return gson.toJson(new ListGameResult(games));
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object createGame(Request req, Response res) throws ResponseException {
        try {
            Request a = req;
            String authToken = gson.fromJson(req.headers("Authorization"), String.class);
           GameData sentGame = gson.fromJson(req.body(), GameData.class);

            GameData game = gameService.createGame(new AuthData(authToken,null), sentGame.gameName());
            return gson.toJson(game);
        } catch (DataAccessException i) {
            throw convertException(i);
        }
    }

    public Object joinGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("Authorization").trim();

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(GameData.class, new JoiningGameDataDeserializer());
            Gson gson2 = builder.create();

            JoiningGameData incomingGame = gson2.fromJson(req.body(), JoiningGameData.class);

            gameService.joinGame(new AuthData(authToken, null), incomingGame);
            return "{}";
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

    private static class JoiningGameDataDeserializer implements JsonDeserializer<JoiningGameData> {
        @Override
        public JoiningGameData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String colorString = jsonObject.get("playerColor").getAsString();
            Integer gameIDString = jsonObject.get("gameID").getAsInt();

            return new JoiningGameData(colorString, gameIDString);

        }
    }

}
