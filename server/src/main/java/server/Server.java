package server;

import dataAccess.*;
import exception.ResponseException;
import handler.Handler;
import service.DatabaseService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final DatabaseService databaseService;
    private final GameService gameService;
    private final UserService userService;

    private final Handler handler;
    public Server() {
        // TODO: Change to DB*DAO when swapping out interfaces
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.userDAO = new MemoryUserDAO();
        this.databaseService = new DatabaseService(authDAO, gameDAO, userDAO);
        this.gameService = new GameService(authDAO, gameDAO);
        this.userService = new UserService(authDAO, userDAO);

        this.handler = new Handler(databaseService,gameService,userService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/pet", this::addPet);
        Spark.get("/pet", this::listPets);
        Spark.delete("/pet/:id", this::deletePet);
        Spark.delete("/pet", this::deleteAllPets);
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
}
