package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DBAuthDAO;
import dataAccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthDAOTest {
    private static AuthDAO authDAO;
    private static UserData user;
    private static UserData userWrong;


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
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
    }

    @Test
    @Order(1)
    @DisplayName("Create Auth - No Username")
    public void CreateAuthNoUsername() {
        String expectedException = "bad request";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> authDAO.createAuth(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Create Auth - Bad SQL?")
    public void CreateAuthSQLFailure() {
        // TODO: Finish this
        String expectedException = "bad request";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> authDAO.createAuth(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Create Auth - Correct")
    public void CreateGameAuthorized() {
        // TODO: Finish this
        String expectedException = "bad request";
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> authDAO.createAuth(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }
}