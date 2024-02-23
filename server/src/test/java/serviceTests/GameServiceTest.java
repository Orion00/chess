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
            auth = authDAO.createAuth(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the DB");
        }

        try {
            insertedGame = gameService.createGame(auth, "Game 34");
        } catch (DataAccessException i){
            System.out.println(i.getMessage());
        }

    }



    @Test
    @Order(1)
    @DisplayName("Create Game - Unauthorized")
    public void createGameUnauthorized() {
        String expectedException = "Unauthorized";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.createGame(authWrong,"Game 34"));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Create Game - Already Exists")
    public void createGameAlreadyExists() {
        String expectedException = "Game already exists";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.createGame(auth,"Game 34"));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Create Game - Authorized")
    public void createGameAuthorized() {
        GameData expected = new GameData(0,null,null,"Game 0", null);
        auth = authDAO.createAuth(user);
        GameData actual = Assertions.assertDoesNotThrow(() -> gameService.createGame(auth,"Game 0"));
        Assertions.assertEquals(expected.gameName(),actual.gameName());
    }

    @Test
    @Order(4)
    @DisplayName("List Games - Unauthorized")
    public void ListGamesUnauthorized() {
        String expectedException = "Unauthorized";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.ListGames(authWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("List Games - Authorized")
    public void ListGamesAuthorized() {
        GameData insertedGame2 = Assertions.assertDoesNotThrow(() -> gameService.createGame(auth, "Game 35"));

        List<GameData> expected = new ArrayList<>();
        expected.add(insertedGame);
        expected.add(insertedGame2);
        List<GameData> actual = Assertions.assertDoesNotThrow(() -> gameService.ListGames(auth));
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(6)
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
    @Order(7)
    @DisplayName("Join Game - Unauthorized")
    public void JoinGameUnauthorized() {
        String expectedException = "Unauthorized";
        DataAccessException actualException =assertThrows(DataAccessException.class,() ->gameService.joinGame(authWrong,"WHITE",34));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Join Game - Incorrect GameID")
    public void JoinGameWrongID() {
        auth = authDAO.createAuth(user);
        String expectedException = "GameID doesn't exist";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"WHITE",404));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Join Game - Color Already Used")
    public void JoinGameColorAlreadyUsed() {
        auth = authDAO.createAuth(user);
        Assertions.assertDoesNotThrow(() ->gameService.joinGame(auth,"WHITE",insertedGame.gameID()));
        String expectedException = "WHITE is already taken in this game";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"WHITE",insertedGame.gameID()));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Join Game - Invalid Color")
    public void JoinGameInvalidColor() {
        auth = authDAO.createAuth(user);
        String expectedException = "Invalid color entered";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,"MAGENTA",insertedGame.gameID()));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("Join Game - Authorized")
    public void JoinGameAuthorized() {
        auth = authDAO.createAuth(user);
        Assertions.assertDoesNotThrow(() ->gameService.joinGame(auth,"WHITE",insertedGame.gameID()));
    }


}