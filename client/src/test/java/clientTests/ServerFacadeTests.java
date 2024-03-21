package clientTests;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private static UserData user;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");

    }

    @BeforeEach
    void reset() {
        try {
            facade.clear();
        } catch (ResponseException i) {
            throw new RuntimeException(i);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerFail() {
        // Incorrect Input
        String expectedException = "Failure: Bad Request";
        ResponseException actualException = assertThrows(ResponseException.class,() ->facade.register(null,null,null));
        assertEquals(expectedException, actualException.getMessage());

        // User already registered
        Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));

        String expectedException2 = "Failure: Forbidden";
        ResponseException actualException2 = assertThrows(ResponseException.class,() ->facade.register(user.username(), user.password(), user.email()));
        assertEquals(expectedException2, actualException2.getMessage());
    }

    @Test
    public void registerPass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        Assertions.assertDoesNotThrow(() ->facade.logout(authToken.authToken()));

        AuthData authToken2 = Assertions.assertDoesNotThrow(() ->facade.register(user.username()+"Thesecond", user.password(), user.email()));
        Assertions.assertDoesNotThrow(() ->facade.logout(authToken2.authToken()));

    }

    @Test
    public void loginFail() {
        // No username/password
        String expectedException = "Failure: Unauthorized";
        ResponseException actualException = assertThrows(ResponseException.class,() ->facade.login(null,null));
        assertEquals(expectedException, actualException.getMessage());

        // Wrong password
        Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));

        String expectedException2 = "Failure: Unauthorized";
        ResponseException actualException2 = assertThrows(ResponseException.class,() ->facade.login(user.username(), user.password()+"abcdef"));
        assertEquals(expectedException2, actualException2.getMessage());

        // No user with that username
        String expectedException3 = "Failure: Unauthorized";
        ResponseException actualException3 = assertThrows(ResponseException.class,() ->facade.login("Mittens4Kittens", user.password()));
        assertEquals(expectedException3, actualException3.getMessage());
    }

    @Test
    public void loginPass() {
        // Log in twice
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        Assertions.assertDoesNotThrow(() ->facade.logout(authToken.authToken()));

        AuthData authToken2 = Assertions.assertDoesNotThrow(() ->facade.login(user.username(), user.password()));
        Assertions.assertDoesNotThrow(() ->facade.logout(authToken2.authToken()));

        Assertions.assertDoesNotThrow(() ->facade.login(user.username(), user.password()));
    }

    @Test
    public void logoutFail() {
        String expectedException = "Failure: Unauthorized";
        // Wrong authToken
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        ResponseException actualException = Assertions.assertThrows(ResponseException.class,() ->facade.logout(authToken.authToken()+"extraBaconBits"));
        assertEquals(expectedException, actualException.getMessage());

        //No AuthToken
        Assertions.assertDoesNotThrow(() ->facade.login(user.username(), user.password()));
        ResponseException actualException2 = Assertions.assertThrows(ResponseException.class,() ->facade.logout(null));
        assertEquals(expectedException, actualException2.getMessage());

        //Empty AuthToken
        Assertions.assertDoesNotThrow(() ->facade.login(user.username(), user.password()));
        ResponseException actualException3 = Assertions.assertThrows(ResponseException.class,() ->facade.logout(""));
        assertEquals(expectedException, actualException3.getMessage());
    }

    @Test
    public void logoutPass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        Assertions.assertDoesNotThrow(() ->facade.logout(authToken.authToken()));

        // Makes sure you can't use the authToken after logging out
        Assertions.assertThrows(ResponseException.class,() ->facade.listGames(authToken.authToken()));
        Assertions.assertThrows(ResponseException.class,() ->facade.logout(authToken.authToken()));

    }

    @Test
    public void listGamesFail() {
        String expectedException = "Failure: Unauthorized";
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));

        // Wrong authToken
        ResponseException actualException = Assertions.assertThrows(ResponseException.class,() ->facade.listGames(authToken.authToken()+"extraBaconBits"));
        assertEquals(expectedException, actualException.getMessage());

        // Null authtoken
        ResponseException actualException2 = Assertions.assertThrows(ResponseException.class,() ->facade.listGames(null));
        assertEquals(expectedException, actualException2.getMessage());

        // Empty authToken
        ResponseException actualException3 = Assertions.assertThrows(ResponseException.class,() ->facade.listGames(""));
        assertEquals(expectedException, actualException3.getMessage());
    }

    @Test
    public void listGamesPass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));

        Assertions.assertDoesNotThrow(() ->facade.listGames(authToken.authToken()));
        Assertions.assertDoesNotThrow(() ->facade.listGames(authToken.authToken()));
        Assertions.assertDoesNotThrow(() ->facade.listGames(authToken.authToken()));
    }

    @Test
    public void createGameFail() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));

        // Wrong authToken
        String expectedException = "Failure: Unauthorized";
        ResponseException actualException = Assertions.assertThrows(ResponseException.class,() ->facade.createGame(authToken.authToken()+"extraBaconBits","CoolGameName"));
        assertEquals(expectedException, actualException.getMessage());

        // Null authtoken
        ResponseException actualException2 = Assertions.assertThrows(ResponseException.class,() ->facade.createGame(null,"CoolGameName"));
        assertEquals(expectedException, actualException2.getMessage());

        // Empty authToken
        ResponseException actualException3 = Assertions.assertThrows(ResponseException.class,() ->facade.createGame("","CoolGameName"));
        assertEquals(expectedException, actualException3.getMessage());

        // Null gamename
        String expectedException2 = "Failure: Bad Request";
        ResponseException actualException4 = Assertions.assertThrows(ResponseException.class,() ->facade.createGame(authToken.authToken(), null));
        assertEquals(expectedException2, actualException4.getMessage());

        // Empty gamename
        ResponseException actualException5 = Assertions.assertThrows(ResponseException.class,() ->facade.createGame(authToken.authToken(), ""));
        assertEquals(expectedException2, actualException5.getMessage());
    }

    @Test
    public void createGamePass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        String gameName = "CoolGameName";

        Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));
//        Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));
//        Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));
        Assertions.assertDoesNotThrow(() ->facade.listGames(authToken.authToken()));
    }

    @Test
    public void joinGameFail() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        String gameName = "CoolGameName";

        GameData gameId = Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));

        String expectedError = "Failure: Unauthorized";

        // Wrong authToken
        ResponseException actualError = assertThrows(ResponseException.class, ()-> facade.joinGame(authToken.authToken()+"extraStuff","WHITE",gameId.gameID() ));
        assertEquals(expectedError, actualError.getMessage());

        // Empty authToken
        ResponseException actualError2 = assertThrows(ResponseException.class, ()-> facade.joinGame("","WHITE",gameId.gameID() ));
        assertEquals(expectedError, actualError2.getMessage());

        // Null authToken
        String expectedError2 = "Failure: Server Error";
        ResponseException actualError3 = assertThrows(ResponseException.class, ()-> facade.joinGame(null,"WHITE",gameId.gameID() ));
        assertEquals(expectedError2, actualError3.getMessage());

        // Joining where someone has already joined
        String expectedError3 = "Failure: Forbidden";
        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),"WHITE",gameId.gameID() ));
        ResponseException actualError4 = assertThrows(ResponseException.class,()-> facade.joinGame(authToken.authToken(),"WHITE",gameId.gameID() ));
        assertEquals(expectedError3, actualError4.getMessage());
    }

    @Test
    public void joinGamePass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        String gameName = "CoolGameName";

        GameData gameId = Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));

        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),"WHITE",gameId.gameID() ));
        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),"BLACK",gameId.gameID() ));

        // Observer
        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),null,gameId.gameID() ));
        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),null,gameId.gameID() ));
        assertDoesNotThrow(()-> facade.joinGame(authToken.authToken(),null,gameId.gameID() ));
    }

    @Test
    public void clearPass() {
        AuthData authToken = Assertions.assertDoesNotThrow(() ->facade.register(user.username(), user.password(), user.email()));
        String gameName = "CoolGameName";

        Assertions.assertDoesNotThrow(() ->facade.createGame(authToken.authToken(), gameName));
        List<GameData> returnedGames = Assertions.assertDoesNotThrow(() ->facade.listGames(authToken.authToken()));
        assertEquals(1, returnedGames.size());

        assertDoesNotThrow(()-> facade.clear());

        // Make sure you can't logout or list games after clearing
        assertThrows(ResponseException.class, ()-> facade.logout(authToken.authToken()));
        assertThrows(ResponseException.class, ()-> facade.listGames(authToken.authToken()));
    }

}
