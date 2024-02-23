package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;
import service.DatabaseService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {
    private static DatabaseService databaseService;
    private static GameService gameService;
    private static UserService userService;
    private static MemoryAuthDAO authDAO;
    private static MemoryGameDAO gameDAO;
    private static MemoryUserDAO userDAO;


   @BeforeAll
   public static void init() {
       authDAO = new MemoryAuthDAO();
       gameDAO = new MemoryGameDAO();
       userDAO = new MemoryUserDAO();
       databaseService = new DatabaseService(authDAO,gameDAO,userDAO);
       gameService = new GameService(authDAO,gameDAO);
       userService = new UserService(authDAO,userDAO);
   }

    @Test
    public void clearDatabaseSucceedTest() {
        Assertions.assertDoesNotThrow(() -> databaseService.clearApp());

    }

    public void clearDatabaseFailTest() {
        UserData user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        Assertions.assertDoesNotThrow(() -> databaseService.clearApp());
//        Assertions.assertThrows(new DataAccessException(),() -> userService.login(user),);

        // TODO: Finish this test

    }

}