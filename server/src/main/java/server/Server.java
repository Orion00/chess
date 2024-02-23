package server;

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
        // TODO: Change to DB*DAO when swapping out interfaces
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
        Spark.post("/db", this::delete);
//        Spark.get("/pet", this::listPets);
//        Spark.delete("/pet/:id", this::deletePet);
//        Spark.delete("/pet", this::deleteAllPets);
        Spark.exception(ResponseException.class, this::exceptionHandler);


        Spark.init();
        Spark.awaitInitialization();


        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.getMessage());
    }

    private Object delete(Request req, Response res) throws ResponseException {
        handler.clear(req, res);
        res.status(200);
        return "";
    }
}
