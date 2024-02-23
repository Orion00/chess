package serviceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.DatabaseService;
import service.GameService;
import service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static DatabaseService databaseService;
    private static GameService gameService;
    private static UserService userService;
    private static MemoryAuthDAO authDAO;
    private static MemoryGameDAO gameDAO;
    private static MemoryUserDAO userDAO;

    private static UserData user;
    private static AuthData auth;
    private static AuthData authWrong;

    private static GameData insertedGame;

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        databaseService = new DatabaseService(authDAO,gameDAO,userDAO);
        gameService = new GameService(authDAO,gameDAO);
        userService = new UserService(authDAO,userDAO);
        user = new UserData("EarnestI","1234","Earnest@Incompotence.com");
        authWrong = null;
    }

    @BeforeEach
    public void reset() {
        try {
            databaseService.clearApp();
//            auth = authDAO.createAuth(user);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to clear the DB");
        }

//        try {
//            insertedGame = gameService.createGame(auth, "Game 34");
//        } catch (DataAccessException i){
//            System.out.println(i.getMessage());
//        }
    }

    @Test
    @Order(1)
    @DisplayName("Register - User Already Exists")
    public void RegisterUserAlreadyExists() {
        String expectedException = "already taken";
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.register(user));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Register - No Username, Password, or Email Given")
    public void RegisterNoPassword() {
        String expectedException = "bad request";

        UserData userWrong = new UserData("EarnestI",null,"Earnest@Incompotence.com");
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.register(userWrong));
        assertEquals(expectedException,actualException.getMessage());

        UserData userWrong2 = new UserData("EarnestI","","Earnest@Incompotence.com");
        actualException = assertThrows(DataAccessException.class,() -> userService.register(userWrong2));
        assertEquals(expectedException,actualException.getMessage());

        UserData userWrong3 = new UserData("","1234","Earnest@Incompotence.com");
        actualException = assertThrows(DataAccessException.class,() -> userService.register(userWrong3));
        assertEquals(expectedException,actualException.getMessage());

        UserData userWrong4 = new UserData("EarnestI","",null);
        actualException = assertThrows(DataAccessException.class,() -> userService.register(userWrong4));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Register - Works Fine")
    public void RegisterUserPass() {
        Assertions.assertDoesNotThrow(() -> userService.register(user));
    }

    @Test
    @Order(3)
    @DisplayName("Login - unauthorized Wrong Password")
    public void LoginWrongPassword() {
        String expectedException = "unauthorized";
        AuthData auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        UserData userWrong = new UserData(user.username(),"4321",user.email());
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.login(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Login - User Doesn't Exist")
    public void LoginUserDoesntExist() {
        String expectedException = "bad request";
        UserData userWrong = new UserData("EarnestWigglebee","4321",user.email());
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.login(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }


    @Test
    @Order(5)
    @DisplayName("Login - Works Fine")
    public void LoginPass() {
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        Assertions.assertDoesNotThrow(() -> userService.login(user));
    }

    @Test
    @Order(6)
    @DisplayName("Logout - unauthorized Wrong AuthToken")
    public void LogoutWrongAuthToken() {
        String expectedException = "unauthorized";

        String uuid = UUID.randomUUID().toString();
        authWrong = new AuthData(uuid, user.username());

        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.logout(authWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Logout - unauthorized Already Logged Out")
    public void LogoutAlreadyLoggedOut() {
        String expectedException = "unauthorized";
        AuthData auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        Assertions.assertDoesNotThrow(() -> userService.login(user));
        Assertions.assertDoesNotThrow(() -> userService.logout(auth));

        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.logout(auth));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Logout - Works Fine (Multiple Auth Tokens)")
    public void LogoutPass() {
        AuthData auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        Assertions.assertDoesNotThrow(() -> userService.login(user));
        Assertions.assertDoesNotThrow(() -> userService.logout(auth));

        UserData userOther = new UserData("OtherEarnest","4321",user.email());
        AuthData authOther = Assertions.assertDoesNotThrow(() -> userService.register(userOther));
        Assertions.assertDoesNotThrow(() -> userService.login(userOther));
        Assertions.assertDoesNotThrow(() -> userService.logout(authOther));
    }
}