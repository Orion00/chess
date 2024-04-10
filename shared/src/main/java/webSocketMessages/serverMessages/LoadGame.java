package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGame extends ServerMessage{
    ChessGame game;

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public LoadGame(ServerMessageType type) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }

//    @Override
//    public String toString() {
//        return "LoadGame"+game.toString();
//    }
}
