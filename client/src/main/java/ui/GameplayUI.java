package ui;

import chess.ChessBoard;
import client.ServerFacade;
import client.websocket.WebsocketFacade;
import exception.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GameplayUI implements ClientUI{
    private final String serverUrl;
    private final ServerFacade server;

    private Integer currentGameId;
    private String currentPlayerColor;
    private WebsocketFacade ws;


    public GameplayUI(String url, ServerFacade server) {
        serverUrl = url;
        this.server = server;
        currentGameId = null;
//        currentPlayerColor = "WHITE"; // TODO: Don't hardcode this
    }

    @Override
    public String help() {
        return """
            (Functionality limited)
            help - Display available commands
            print - Show your board
            move - Move selected piece
            show - Show available moves for selected piece
            leave - Return to previous window. Can resume later
            resign - Surrender the game
            quit - Close the program
            """;
    }


    public String eval(String currentAuthToken,String line) {
        try {
            var tokens = line.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "print" -> print(authToken, params);
                case "fail" -> throw new ResponseException(400,"You failed.");
                case "quit" -> "quit";
                case "help" -> help();
                case "print" -> print(currentAuthToken, line);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String print(String authToken, String... params) throws ResponseException {
        if (params.length > 0) {
            throw new ResponseException(400, "Too many inputs entered");
        }

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        BoardDrawer boardDrawer = new BoardDrawer();
//        boardDrawer.drawBoard(out, currentPlayerColor, board);
//        TODO: Get board from websocket
        String board = getCurrentGame(authToken);
        ChessBoard dummyBoard = new ChessBoard();
        dummyBoard.resetBoard();
        boardDrawer.drawBoard(out, "WHITE", dummyBoard);
        boardDrawer.drawBoard(out, "BLACK", dummyBoard);

        return "";
    }

    private String getCurrentGame(String authToken) throws ResponseException {
        ws.getGame(authToken,currentGameId);
        return "Orion need to change this";
    }

    public void setCurrentGameId(Integer gameId) {
        currentGameId = gameId;
    }
    public void setCurrentPlayerColor(String currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public void setWebSocketFacade(WebsocketFacade websocketFacade) {
        this.ws = websocketFacade;
    }
}
