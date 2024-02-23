package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

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
        String expectedException = "Username is already taken";
        Assertions.assertDoesNotThrow(() -> userService.register(user));
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.register(user));
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
    @DisplayName("Login - Unauthorized Wrong Password")
    public void LoginWrongPassword() {
        String expectedException = "Unauthorized";
        AuthData auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
        UserData userWrong = new UserData(user.username(),"4321",user.email());
        DataAccessException actualException = assertThrows(DataAccessException.class,() -> userService.login(userWrong));
        assertEquals(expectedException,actualException.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Login - User Doesn't Exist")
    public void LoginUserDoesntExist() {
        String expectedException = "User doesn't exist";
        AuthData auth = Assertions.assertDoesNotThrow(() -> userService.register(user));
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
}