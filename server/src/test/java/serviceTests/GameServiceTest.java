package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.DatabaseService;
import service.GameService;
import service.UserService;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private static DatabaseService databaseService;
    private static GameService gameService;
    private static UserService userService;
    private static MemoryAuthDAO authDAO;
    private static MemoryGameDAO gameDAO;
    private static MemoryUserDAO userDAO;

    private static UserData user;
    private static AuthData auth;

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        databaseService = new DatabaseService(authDAO,gameDAO,userDAO);
        gameService = new GameService(authDAO,gameDAO);
        userService = new UserService(authDAO,userDAO);
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
    }

    @BeforeEach
    public void reset() {
        try {
            databaseService.clearApp();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the DB");
        }

    }

    @Test
    @Order(1)
    @DisplayName("List Games - Unauthorized")
    public void ListGamesUnauthorized() {
        Assertions.assertThrows(DataAccessException.class,() -> gameService.ListGames(auth));
    }

    @Test
    @Order(2)
    @DisplayName("List Games - Authorized")
    public void ListGamesAuthorized() {
        List<GameData> expected = new ArrayList<>();
        try {
            auth = authDAO.createAuth(user);
            GameData insertedGame = gameService.createGame(auth, "Game 34");
            expected.add(insertedGame);
        } catch (DataAccessException i){
            System.out.println(i.getMessage());
        }
        List<GameData> actual = Assertions.assertDoesNotThrow(() ->gameService.ListGames(auth));
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(2)
    @DisplayName("List Games - Empty")
    public void ListGamesEmpty() {
        List<GameData> expected = new ArrayList<>();
        auth = authDAO.createAuth(user);
        List<GameData> actual = Assertions.assertDoesNotThrow(() ->gameService.ListGames(auth));
        Assertions.assertEquals(expected,actual);
    }


}