package webSocketMessages.userCommands;

import com.google.gson.Gson;

public class JoinObserver extends UserGameCommand {
    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    private Integer gameID;
    private String username;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    public JoinObserver(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
