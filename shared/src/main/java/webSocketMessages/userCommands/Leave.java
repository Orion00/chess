package webSocketMessages.userCommands;

import chess.ChessGame;

public class Leave extends UserGameCommand{
    private Integer gameID;

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Leave(String authToken) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
    }
}
