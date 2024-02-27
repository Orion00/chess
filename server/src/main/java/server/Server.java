package server;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResponseException;
import handler.Handler;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private final Handler handler;
    public Server() {
        // Change to DB*DAO when swapping out interfaces
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();
        DatabaseService databaseService = new DatabaseService(authDAO, gameDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);
        UserService userService = new UserService(authDAO, userDAO);

        this.handler = new Handler(databaseService, gameService, userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::delete);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.exception(ResponseException.class, this::exceptionHandler);


        Spark.init();
        Spark.awaitInitialization();


        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object exceptionHandler(ResponseException ex, Request req, Response res) {
        String body = "{\"message\": \""+ex.getMessage()+"\"}";
        res.status(ex.statusCode());
        res.body(body);
        return body;
    }

    private Object delete(Request req, Response res) throws ResponseException {
        Object response = handler.clear(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object register(Request req, Response res) throws ResponseException {
        Object response = handler.register(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object login(Request req, Response res) throws ResponseException {
        Object response = handler.login(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        Object response = handler.logout(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        Object response = handler.listGames(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        Object response = handler.createGame(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        Object response = handler.joinGame(req, res);
        return handleHandlerResponse(req, res, response);
    }

    private Object handleHandlerResponse(Request req, Response res, Object handlerResponse) {
        res.status(200);
        return handlerResponse;
    }
}
