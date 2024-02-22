package server;

import dataAccess.*;
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
    public Server() {
        // TODO: Change to DB*DAO when swapping out interfaces
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.userDAO = new MemoryUserDAO();
        this.databaseService = new DatabaseService(authDAO, gameDAO, userDAO);
        this.gameService = new GameService(authDAO, gameDAO);
        this.userService = new UserService(authDAO, userDAO);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
