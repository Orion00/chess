package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    private static GameDAO gameDAO;
    private static String gameName;
    private static String gameName2;


    @BeforeAll
    static void setUp() {
        try {
            gameName = "game1";
            gameName2 = "game2";
            gameDAO = new DBGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void reset() {
        try {
            gameDAO.clearGames();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the auths DB");
        }
//        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");

    }

    @Test
    @Order(1)
    @DisplayName("Get Game by ID - Broken ID")
    public void getGameIDWrong() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));

        // Should return null if it can't find a game
        assertNull(gameDAO.getGame(createdGame.gameID()-100));
        assertNull(gameDAO.getGame((Integer) null));
    }

    @Test
    @Order(2)
    @DisplayName("Get Game by ID - Works")
    public void getGameIDCorrect() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData createdGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        GameData requestedGame = assertDoesNotThrow(() -> gameDAO.getGame(createdGame.gameID()));
        GameData requestedGame2 = assertDoesNotThrow(() -> gameDAO.getGame(createdGame2.gameID()));

        assertEquals(createdGame.gameID(), requestedGame.gameID());
        assertEquals(createdGame2.gameID(), requestedGame2.gameID());
    }

    @Test
    @Order(3)
    @DisplayName("Get Game by Name - Broken Name")
    public void getGameNameWrong() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));

        // Should return null if it can't find a game
        assertNull(gameDAO.getGame(createdGame.gameName()+"wrong"));
        assertNull(gameDAO.getGame((String) null));
    }

    @Test
    @Order(4)
    @DisplayName("Get Games - Empty")
    public void getGamesEmpty() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData createdGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        GameData requestedGame = assertDoesNotThrow(() -> gameDAO.getGame(createdGame.gameName()));
        GameData requestedGame2 = assertDoesNotThrow(() -> gameDAO.getGame(createdGame2.gameName()));

        assertEquals(createdGame.gameID(), requestedGame.gameID());
        assertEquals(createdGame2.gameID(), requestedGame2.gameID());
    }

    @Order(5)
    @DisplayName("Get Game by Name - Works")
    public void getGameNameCorrect() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData createdGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        GameData requestedGame = assertDoesNotThrow(() -> gameDAO.getGame(createdGame.gameName()));
        GameData requestedGame2 = assertDoesNotThrow(() -> gameDAO.getGame(createdGame2.gameName()));

        assertEquals(createdGame.gameID(), requestedGame.gameID());
        assertEquals(createdGame2.gameID(), requestedGame2.gameID());
    }

    @Test
    @Order(6)
    @DisplayName("Get Games - Works")
    public void getGamesCorrect() {
        List<GameData> expected = new ArrayList<>();
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData createdGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        expected.add(createdGame);
        expected.add(createdGame2);
        List<GameData> actual = gameDAO.getGames();
        assertEquals(expected.size(),actual.size());
    }

    @Test
    @Order(7)
    @DisplayName("Create Game - Works")
    public void CreateGameCorrect() {
        // Should return null if there's no user
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData actualGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        assertEquals(gameName, actualGame.gameName());
        assertEquals(gameName2, actualGame2.gameName());
    }
}
