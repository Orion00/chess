package clientTests;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

}
