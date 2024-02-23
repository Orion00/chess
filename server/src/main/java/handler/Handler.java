package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

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

    public Object register(Request req, Response res) throws ResponseException {
        try {
            UserData user = (UserData)gson.fromJson(String.valueOf(req), UserData.class);
            AuthData result = userService.register(user);
            return gson.toJson(result);
        } catch (DataAccessException i){
            throw handleException(i);
        }
    }

    public Object login(Request req, Response res) throws DataAccessException {
        UserData user = (UserData)gson.fromJson(String.valueOf(req), UserData.class);
        AuthData result = userService.login(user);
        return gson.toJson(result);
    }

    private ResponseException handleException(DataAccessException i) {
        Integer statusCode = null;
        String errorMessage = i.getMessage();
        if (errorMessage.equals("Unauthorized")) {
            statusCode = 401;
        } else {
            statusCode = 500;
        }

        return new ResponseException(statusCode, "Error: "+errorMessage);
    }
}
