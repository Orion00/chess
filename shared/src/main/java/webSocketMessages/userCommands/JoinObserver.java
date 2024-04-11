package webSocketMessages.userCommands;


//public class JoinObserver extends UserGameCommand {
//    private Integer gameID;
//    private String username;
//
//    public JoinObserver(String authToken) {
//        super(authToken);
//        this.commandType = CommandType.JOIN_OBSERVER;
//    }
//
//    public Integer getGameID() {
//        return gameID;
//    }
//
//    public void setGameID(Integer gameID) {
//        this.gameID = gameID;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public void setUsername(String username) {
//        this.username = username;
//    }
//}

public class JoinObserver extends UserGameCommand{
    private Integer gameID;

    public JoinObserver(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}