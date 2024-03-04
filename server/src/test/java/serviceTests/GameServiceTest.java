package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.JoiningGameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.DatabaseService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

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
        userService = new UserService(authDAO, userDAO);
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
        authWrong = null;
    }

    @BeforeEach
    public void reset() {
        try {
            databaseService.clearApp();
//            auth = authDAO.createAuth(user);
            auth = userService.register(user);
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
    @DisplayName("Create Game - unauthorized")
    public void CreateGameUnauthorized() {
        String expectedException = "unauthorized";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.createGame(authWrong,"Game 34"));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Create Game - Already Exists")
    public void CreateGameAlreadyExists() {
        String expectedException = "Game already exists";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.createGame(auth,"Game 34"));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Create Game - Authorized")
    public void CreateGameAuthorized() {
        GameData expected = new GameData(0,null,null,"Game 0", null);

        GameData actual = Assertions.assertDoesNotThrow(() -> gameService.createGame(auth,"Game 0"));
        Assertions.assertEquals(expected.gameName(),actual.gameName());
    }

    @Test
    @Order(4)
    @DisplayName("List Games - unauthorized")
    public void ListGamesUnauthorized() {
        String expectedException = "unauthorized";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> gameService.listGames(authWrong));
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
        List<GameData> actual = Assertions.assertDoesNotThrow(() -> gameService.listGames(auth));
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
        auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        List<GameData> actual = Assertions.assertDoesNotThrow(() ->gameService.listGames(auth));
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @Order(7)
    @DisplayName("Join Game - unauthorized")
    public void JoinGameUnauthorized() {
        String expectedException = "unauthorized";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(authWrong,new JoiningGameData("WHITE",34)));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Join Game - Incorrect GameID")
    public void JoinGameWrongID() {
        String expectedException = "bad request";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,new JoiningGameData("WHITE",404)));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(9)
    @DisplayName("Join Game - Color Already Used")
    public void JoinGameColorAlreadyUsed() {
        Assertions.assertDoesNotThrow(() ->gameService.joinGame(auth, new JoiningGameData("WHITE",insertedGame.gameID())));
        String expectedException = "already taken";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,new JoiningGameData("WHITE",insertedGame.gameID())));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(10)
    @DisplayName("Join Game - Invalid Color")
    public void JoinGameInvalidColor() {
        String expectedException = "Invalid color entered";
        DataAccessException actualException = assertThrows(DataAccessException.class,() ->gameService.joinGame(auth,new JoiningGameData("MAGENTA",insertedGame.gameID())));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(11)
    @DisplayName("Join Game - Authorized")
    public void JoinGameAuthorized() {
        auth = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        Assertions.assertDoesNotThrow(() ->gameService.joinGame(auth,new JoiningGameData("WHITE",insertedGame.gameID())));
        Assertions.assertDoesNotThrow(() ->gameService.joinGame(auth,new JoiningGameData("BLACK",insertedGame.gameID())));
    }


}