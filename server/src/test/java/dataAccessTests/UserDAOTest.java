package dataAccessTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private static UserDAO userDAO;
    private static UserData user;
    private static UserData userWrong;
    private static AuthData authWrong;


    @BeforeAll
    static void setUp() {
        try {
            userDAO = new DBUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
//        userDAO = new MemoryUserDAO();
    }

    @BeforeEach
    void reset() {
        try {
//            authDAO.clearAuths();
            userDAO.clearUsers();
            user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the auths DB");
        }

    }

    @Test
    @Order(1)
    @DisplayName("Get User - Wrong User")
    public void GetAuthWrongUsername() {
        // Should return null if there's no user

        userWrong = null;
        assertNull(userDAO.getUser(userWrong));

        userWrong = new UserData(null, "secretpassword", "@gmail.com");
        assertNull(userDAO.getUser(userWrong));

        userWrong = new UserData("", "secretpassword", "@gmail.com");
        assertNull(userDAO.getUser(userWrong));

        userWrong = new UserData("nonexistentUsername", "secretpassword", "@gmail.com");
        assertNull(userDAO.getUser(userWrong));
    }

    @Test
    @Order(2)
    @DisplayName("Get User - Works")
    public void GetUserCorrect() {
        UserData user2 = new UserData("EarnestJ", user.password(), user.email());
        assertDoesNotThrow(() -> userDAO.createUser(user));
        assertDoesNotThrow(() -> userDAO.createUser(user2));

        UserData result = Assertions.assertDoesNotThrow(() -> userDAO.getUser(user));
        assertEquals(user, result);

        UserData result2 = Assertions.assertDoesNotThrow(() -> userDAO.getUser(user2));
        assertEquals(user2, result2);
    }

    @Test
    @Order(3)
    @DisplayName("Create User - Bad User Data")
    public void CreateUserBadData() {
        String expectedException = "bad request";

        userWrong = null;
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userDAO.createUser(userWrong));
        assertEquals(expectedException, actualException.getMessage());

        userWrong = new UserData(null, "secretpassword", "@gmail.com");
        DataAccessException actualException2 = assertThrows(DataAccessException.class,() -> userDAO.createUser(userWrong));
        assertEquals(expectedException, actualException2.getMessage());

        userWrong = new UserData("", "secretpassword", "@gmail.com");
        DataAccessException actualException3 = assertThrows(DataAccessException.class,() -> userDAO.createUser(userWrong));
        assertEquals(expectedException, actualException3.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Create User - Already Taken Username")
    public void CreateUserAlreadyTaken() {
        String expectedException = "already taken";

        userWrong = new UserData("takenUsername", "secretpassword", "@gmail.com");
        UserData userWrong2 = new UserData("takenUsername", "othersecretpassword", "cool@gmail.com");
        assertDoesNotThrow(() -> userDAO.createUser(userWrong));
        DataAccessException actualException4 = assertThrows(DataAccessException.class, () -> userDAO.createUser(userWrong2));
        assertEquals(expectedException, actualException4.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Create User - Works")
    public void CreateUserGood() {
        UserData user2 = new UserData("EarnestJI", user.password(), user.email());

        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user2));

        assertEquals(user, userDAO.getUser(user));
        assertEquals(user2,userDAO.getUser(user2));
    }

    @Test
    @Order(5)
    @DisplayName("Clear User - Works")
    public void ClearUserGood() {
        UserData user2 = new UserData("EarnestJI", user.password(), user.email());
        // Add some dummy data
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user));
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user2));

        assertDoesNotThrow(() -> userDAO.clearUsers());
        assertNull(userDAO.getUser(user));
        assertNull(userDAO.getUser(user2));
    }
}
