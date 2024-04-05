package webSocketMessages.userCommands;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken, String userName) {
        this.authToken = authToken;
    }

    public UserGameCommand(String authToken, CommandType commandType, String userName, String gameId) {
        this.authToken = authToken;
        this.commandType = commandType;
        this.userName = userName;
        this.gameId = gameId;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    private String userName;

    private String gameId;

    private String color;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public String getUserName() {return this.userName;}

    public String getGameId() {return this.gameId;}

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
