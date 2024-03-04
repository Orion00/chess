package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DBAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private static AuthDAO authDAO;
    private static UserData user;
    private static UserData userWrong;
    private static AuthData authWrong;


    @BeforeAll
    static void setUp() {
        try {
            authDAO = new DBAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void reset() {
        try {
            authDAO.clearAuths();
            user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the auths DB");
        }
    }

    @Test
    @Order(1)
    @DisplayName("Get Auth - Wrong Username")
    public void GetAuthWrongUsername() {
        // Should return null if there's no user with that authToken

        authWrong = null;
        assertNull(authDAO.getAuthUser(authWrong));

        authWrong = new AuthData(null, "coolUsername");
        assertNull(authDAO.getAuthUser(authWrong));

        authWrong = new AuthData("wrong-auth-token", "coolUsername");
        assertNull(authDAO.getAuthUser(authWrong));
    }

    @Test
    @Order(2)
    @DisplayName("Get Auth - Works")
    public void GetAuthCorrect() {
        AuthData auth = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        AuthData auth2 = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));

        AuthData result = assertDoesNotThrow(() -> authDAO.getAuthUser(auth));
        assertEquals(auth, result);

        AuthData result2 = assertDoesNotThrow(() -> authDAO.getAuthUser(auth2));
        assertEquals(auth2, result2);
    }

    @Test
    @Order(3)
    @DisplayName("Create Auth - No Username")
    public void CreateAuthNoUsername() {
        String expectedException = "bad request";

        userWrong = null;
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> authDAO.createAuth(userWrong));
        assertEquals(expectedException,actualException.getMessage());

        userWrong = new UserData("","mypassword","@gmail.com");
        DataAccessException actualException2 = assertThrows(DataAccessException.class,() -> authDAO.createAuth(userWrong));
        assertEquals(expectedException,actualException2.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Create Auth - Correct")
    public void CreateGameAuthorized() {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
    }

    @Test
    @Order(5)
    @DisplayName("Remove Auth - Wrong Auth")
    public void RemoveAuthWrongAuth() {
        String expectedException = "That AuthToken doesn't exist";

        AuthData authWrong1 = new AuthData(null, null);
        DataAccessException actualException1 = assertThrows(DataAccessException.class,() -> authDAO.removeAuthUser(authWrong1));
        assertEquals(expectedException,actualException1.getMessage());

        AuthData authWrong2 = new AuthData("Cool Auth Token", "Me");
        DataAccessException actualException2 = assertThrows(DataAccessException.class,() -> authDAO.removeAuthUser(authWrong2));
        assertEquals(expectedException,actualException2.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Remove Auth - Correct Auth")
    public void RemoveAuthGood() {
        AuthData auth = Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        assertDoesNotThrow(() -> authDAO.removeAuthUser(auth));
    }

    @Test
    @Order(7)
    @DisplayName("Clear Auth - Correct")
    public void ClearAuthGood() {
        // Add some dummy data
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(user));

        assertDoesNotThrow(() -> authDAO.clearAuths());
    }
}