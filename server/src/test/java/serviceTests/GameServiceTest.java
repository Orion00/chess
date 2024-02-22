package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.*;
import service.DatabaseService;
import service.GameService;

import java.net.HttpURLConnection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private static GameService gameService;
    private static DatabaseService databaseService;

    @BeforeAll
    public static void init() {
        gameService = new GameService(new MemoryAuthDAO(), new MemoryGameDAO());
    }

    @Test
    @DisplayName("List Games - Unauthorized")
    public void ListGamesUnauthorized() {
//        Assertions.assertDoesNotThrow(gameService.ListGames(new AuthData(new UUID(5,2),"username")));
    }

}