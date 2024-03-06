package dataAccessTests;

import chess.*;
import dataAccess.*;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
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

        assertEquals(createdGame, requestedGame);
        assertEquals(createdGame2, requestedGame2);
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
    @DisplayName("Get Game by Name - Works")
    public void getGameNameCorrect() {
        GameData createdGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData createdGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        GameData requestedGame = assertDoesNotThrow(() -> gameDAO.getGame(createdGame.gameName()));
        GameData requestedGame2 = assertDoesNotThrow(() -> gameDAO.getGame(createdGame2.gameName()));

        assertEquals(createdGame, requestedGame);
        assertEquals(createdGame2, requestedGame2);
    }

    @Test
    @Order(5)
    @DisplayName("Get Games - Empty")
    public void getGamesEmpty() {
        List<GameData> actual = gameDAO.getGames();
        assertTrue(actual.isEmpty());
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

        assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    @DisplayName("Create Game - Already Exists")
    public void CreateGameAlreadyTaken() {
        String expectedError = "bad request";
        String expectedError2 = "already taken";
        DataAccessException actualError = assertThrows(DataAccessException.class,() -> gameDAO.createGame(""));
        assertEquals(expectedError, actualError.getMessage());

        DataAccessException actualError2 = assertThrows(DataAccessException.class,() -> gameDAO.createGame(null));
        assertEquals(expectedError, actualError2.getMessage());

        assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        DataAccessException actualError3 = assertThrows(DataAccessException.class,() -> gameDAO.createGame(gameName));
        assertEquals(expectedError2, actualError3.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Create Game - Works")
    public void CreateGameCorrect() {
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        GameData actualGame2 = assertDoesNotThrow(() -> gameDAO.createGame(gameName2));

        GameData expectedGame = new GameData(actualGame.gameID(), null,null,gameName,new ChessGame());
        GameData expectedGame2 = new GameData(actualGame2.gameID(), null,null,gameName2,new ChessGame());

        assertEquals(expectedGame, actualGame);
        assertEquals(expectedGame2, actualGame2);
    }

    @Test
    @Order(10)
    @DisplayName("Update Game - Bad Update")
    public void UpdateGamesBadUpdate() {
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));

        String expectedError = "bad request";
        GameData editedGame = new GameData(actualGame.gameID()-100, "WhiteUsername","BlackUsername","UniqueGameName",actualGame.game());
        DataAccessException actualError = assertThrows(DataAccessException.class,()-> gameDAO.updateGames(editedGame));
        assertEquals(expectedError,actualError.getMessage());

        String expectedError2 = "bad request";
        GameData editedGame2 = new GameData(actualGame.gameID(), null,null,null,null);
        DataAccessException actualError2 = assertThrows(DataAccessException.class,()-> gameDAO.updateGames(editedGame2));
        assertEquals(expectedError2,actualError2.getMessage());

        String expectedError3 = "bad request";
        GameData editedGame3 = null;
        DataAccessException actualError3 = assertThrows(DataAccessException.class,()-> gameDAO.updateGames(editedGame3));
        assertEquals(expectedError3,actualError3.getMessage());

    }

    @Test
    @Order(10)
    @DisplayName("Update Games - Works")
    public void UpdateGamesGood() {
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));
        ChessBoard editedBoard = new ChessBoard();
        editedBoard.resetBoard();
        actualGame.game().setBoard(editedBoard);

        // Starting Board
        GameData editedGame = new GameData(actualGame.gameID(), "WhiteUsername","BlackUsername","UniqueGameName",actualGame.game());
        assertDoesNotThrow(()-> gameDAO.updateGames(editedGame));

        // Change Board and Update
        // Makes a move in the clunkiest way possible, but the only way I know how right now.
        Collection<ChessMove> availMoves = editedGame.game().validMoves(new ChessPosition(2,1));
        try {
            for (ChessMove availMove : availMoves) {
                editedGame.game().makeMove(availMove);
                break;
            }
        } catch (InvalidMoveException i) {
            System.out.println(i.getMessage());
        }

        assertDoesNotThrow(()-> gameDAO.updateGames(editedGame));



        GameData requestedGame = assertDoesNotThrow(() -> gameDAO.getGame(editedGame.gameID()));
        assertEquals(editedGame, requestedGame);


    }

    @Test
    @Order(11)
    @DisplayName("Add Participant - Bad Request")
    public void AddParticipantBadUpdate() {
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));

        String expectedError = "bad request";
        DataAccessException actualError = assertThrows(DataAccessException.class,()-> gameDAO.addParticipant(actualGame.gameID()-100, "WhiteUsername", ChessGame.TeamColor.WHITE));
        assertEquals(expectedError,actualError.getMessage());

        String expectedError2 = "bad request";
        DataAccessException actualError2 = assertThrows(DataAccessException.class,()-> gameDAO.addParticipant(actualGame.gameID(), null, ChessGame.TeamColor.WHITE));
        assertEquals(expectedError2,actualError2.getMessage());

        String expectedError3 = "already taken";
        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "WhiteUsername", ChessGame.TeamColor.WHITE));
        DataAccessException actualError3 = assertThrows(DataAccessException.class,()-> gameDAO.addParticipant(actualGame.gameID(), "WhiteUsername", ChessGame.TeamColor.WHITE));
        assertEquals(expectedError3,actualError3.getMessage());

    }

    @Test
    @Order(12)
    @DisplayName("Add Participant - Works")
    public void AddParticipantGood() {
        GameData actualGame = assertDoesNotThrow(() -> gameDAO.createGame(gameName));

        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "WhiteUsername", ChessGame.TeamColor.WHITE));
        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "BlackUsername", ChessGame.TeamColor.BLACK));
        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "Observer1", null));
        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "Observer2", null));
        assertDoesNotThrow(()-> gameDAO.addParticipant(actualGame.gameID(), "Observer2", null));
    }

    @Test
    @Order(13)
    @DisplayName("Clear Games - Works")
    public void ClearGamesGood() {
        assertDoesNotThrow(()-> gameDAO.clearGames());
    }
}
