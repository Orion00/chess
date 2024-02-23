package serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DatabaseService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {
    private static DatabaseService databaseService;
    private static MemoryAuthDAO authDAO;
    private static MemoryGameDAO gameDAO;
    private static MemoryUserDAO userDAO;


   @BeforeAll
   public static void init() {
       authDAO = new MemoryAuthDAO();
       gameDAO = new MemoryGameDAO();
       userDAO = new MemoryUserDAO();
       databaseService = new DatabaseService(authDAO,gameDAO,userDAO);
   }

    @Test
    public void clearDatabaseSucceedTest() {
        Assertions.assertDoesNotThrow(() -> databaseService.clearApp());

    }

    @Test
    public void clearDatabaseFailTest() {
        // NOTE: I don't know how to get a fail case on this until we get to SQL
        // TODO: Finish this once we're done using in memory stuff
        Assertions.assertDoesNotThrow(() -> databaseService.clearApp());
    }

}