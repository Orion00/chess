package webSocketMessages.userCommands;

import chess.ChessGame;

public class Leave extends UserGameCommand{
    private Integer gameID;

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    private ChessGame.TeamColor playerColor;

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
