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
    private static AuthData authWrong;

    private static GameData insertedGame;

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

        try {
            auth = authDAO.createAuth(user);
            insertedGame = gameService.createGame(auth, "Game 34");
        } catch (DataAccessException i){
            System.out.println(i.getMessage());
        }

    }



    @Test
    @Order(1)
    @DisplayName("Create Game - Unauthorized")
    public void createGameUnauthorized() {
        Assertions.assertThrows(DataAccessException.class,() -> gameService.createGame(auth,"Game 34"));
    }

    @Test
    @Order(2)
    @DisplayName("Create Game - Authorized")
    public void createGameAuthorized() {
        GameData expected = new GameData(0,null,null,"Game 34", null);
        auth = authDAO.createAuth(user);
        GameData actual = Assertions.assertDoesNotThrow(() -> gameService.createGame(auth,"Game 34"));
        Assertions.assertEquals(expected.gameName(),actual.gameName());
    }

    @Test
    @Order(3)
    @DisplayName("List Games - Unauthorized")
    public void ListGamesUnauthorized() {
        Assertions.assertThrows(DataAccessException.class,() -> gameService.ListGames(auth));
    }

    @Test
    @Order(4)
    @DisplayName("List Games - Authorized")
    public void ListGamesAuthorized() {
        List<GameData> expected = new ArrayList<>();
        expected.add(insertedGame);
        List<GameData> actual = Assertions.assertDoesNotThrow(() ->gameService.ListGames(auth));
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(5)
    @DisplayName("List Games - Empty")
    public void ListGamesEmpty() {
        try {
            databaseService.clearApp();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the DB");
        }

        List<GameData> expected = new ArrayList<>();
        auth = authDAO.createAuth(user);
        List<GameData> actual = Assertions.assertDoesNotThrow(() ->gameService.ListGames(auth));
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(6)
    @DisplayName("Join Game - Unauthorized")
    public void JoinGameUnauthorized() {
        Assertions.assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"WHITE",404));
    }

    @Test
    @Order(7)
    @DisplayName("Join Game - Incorrect GameID")
    public void JoinGameWrongID() {
        auth = authDAO.createAuth(user);
        Assertions.assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"WHITE",404));
    }

    @Test
    @Order(8)
    @DisplayName("Join Game - Authorized")
    public void JoinGameAuthorized() {
        auth = authDAO.createAuth(user);
        Assertions.assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"WHITE",404));
    }


}