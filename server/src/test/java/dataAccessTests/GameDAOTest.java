package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    private static GameDAO gameDAO;
    private static UserData user;
    private static UserData userWrong;
    private static AuthData authWrong;


    @BeforeAll
    static void setUp() {
        try {
            gameDAO = new DBGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
//        userDAO = new MemoryUserDAO();
    }

    @BeforeEach
    void reset() {
//        try {
//            authDAO.clearAuths();
//            user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
//        } catch (DataAccessException e) {
//            throw new RuntimeException("Failed to clear the auths DB");
//        }
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");

    }

    @Test
    @Order(5)
    @DisplayName("Create Game - Works")
    public void CreateGameCorrect() {
        // Should return null if there's no user
        ChessGame dummyGame = new ChessGame();
        GameData expectedGameWithoutID = new GameData(1, null,null,"game1", dummyGame);
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame("game1"));
        assertEquals(expectedGameWithoutID, actualGame);
    }
}
