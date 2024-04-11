package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import exception.ResponseException;

public class MakeMove extends UserGameCommand {
    private Integer gameID;

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    private ChessMove move;
    private String playerColor;

//    public String getPlayerColor() {
//        return playerColor;
//    }

    public ChessGame.TeamColor getPlayerColor() throws ResponseException {
        return switch (playerColor) {
            case "WHITE" -> ChessGame.TeamColor.WHITE;
            case "BLACK" -> ChessGame.TeamColor.BLACK;
            case null -> null;
            default -> throw new ResponseException(400, "Invalid color");
        };
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public MakeMove(String authToken) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
    }
}
