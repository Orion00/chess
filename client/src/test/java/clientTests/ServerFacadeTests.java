package clientTests;

import client.ServerFacade;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        ServerFacade facade = new ServerFacade(url);

        try {
            facade.clear();
        } catch (ResponseException i) {
            System.out.print(i);
        }

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
