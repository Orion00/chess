package webSocketMessages.userCommands;

import chess.ChessGame;

public class Resign extends UserGameCommand{
    private Integer gameID;

    private ChessGame.TeamColor playerColor;

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Resign(String authToken) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return this.playerColor;
    }
}
