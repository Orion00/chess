package ui;

import chess.*;
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
    private ChessGame.TeamColor currentPlayerColor;
    private WebsocketFacade ws;
    private String currentUsername;
    private ChessGame currentGame;

    private String currentAuthToken;

    public String getCurrentAuthToken() {
        return currentAuthToken;
    }

    public void setCurrentAuthToken(String currentAuthToken) {
        this.currentAuthToken = currentAuthToken;
    }

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
            move <START LOCATION> <END LOCATION> <PIECE TO PROMOTE TO>- Move selected piece
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
                case "quit" -> "quit";
                case "help" -> help();
                case "print" -> print(currentAuthToken, params);
                case "move" -> makeMove(currentAuthToken, params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public ChessGame.TeamColor getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public String print(String authToken, String... params) throws ResponseException {
        if (params.length > 1) {
            throw new ResponseException(400, "Too many inputs entered");
        }
        printBoard(currentGame.getBoard());

        return "";
    }

    public void setCurrentGameId(Integer gameId) {
        currentGameId = gameId;
    }
    public void setCurrentPlayerColor(ChessGame.TeamColor currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }

    public void setWebSocketFacade(WebsocketFacade websocketFacade) {
        this.ws = websocketFacade;
    }

    public void setCurrentUsername(String currentUsername) { this.currentUsername = currentUsername;}

    public void printAndUpdateBoard(ChessGame game) throws ResponseException {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.out.print("A move was made. Printing updated board.");
        printBoard(game.getBoard());
        currentGame = game;
    }
    private void printBoard(ChessBoard board) throws ResponseException {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        System.out.println();
        BoardDrawer boardDrawer = new BoardDrawer();
        boardDrawer.drawBoard(out, String.valueOf(currentPlayerColor), board);
    }

    private String makeMove(String authToken, String... params) throws ResponseException {
        if (params.length > 3) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        } else if (params.length < 2) {
            throw new ResponseException(400, "Please enter a starting location and ending location. Try again");
        }
        String startLocation = params[0];
        String endLocation = params[1];

        // TODO: Check with regex to make sure it works
        Integer startRow = Character.getNumericValue(startLocation.charAt(1));
        Integer startCol = startLocation.charAt(0) - 'a' + 1; // uses ASCII to calculate column
        Integer endRow = Character.getNumericValue(endLocation.charAt(1));
        Integer endCol = endLocation.charAt(0) - 'a' + 1;

        if (startCol < 1 || startCol > 8 || startRow < 1 || startRow > 8 ||
                endCol < 1 || endCol > 8 || endRow < 1 || endRow > 8) {
            throw new ResponseException(400, "Invalid move. Please select a position on the board.");
        }

        ChessPiece.PieceType promotionPiece = null;
        if ((currentGame.getBoard().getPiece(new ChessPosition(startRow, startCol)).getPieceType() == ChessPiece.PieceType.PAWN) &&
                (endRow == 1 || endRow == 8)) {
            if (params.length != 3) {
                throw new ResponseException(400, "Please enter a piece to promote to.");
            } else {
               promotionPiece = convertToPiece(params[3]).getPieceType();
            }

        }




        //TODO: Implement validation and send move
        ChessMove propMove = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), promotionPiece);
        try {
            // currentGame.makeMove() just tests if it works. currentGame will be changed by this function,
            // but it will be updated if it succeeded or failed by LOAD_GAME
            ChessBoard testBoard = currentGame.getBoard().clone();
            ChessGame testGame = new ChessGame();
            testGame.setBoard(testBoard);
            testGame.setTeamTurn(currentGame.getTeamTurn());
            testGame.makeMove(propMove);

            ws.makeMove(currentAuthToken, currentGameId, currentUsername, propMove);
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "Move failed. "+e.getMessage());
        }


        return String.format("Move successful from %s to %s", startLocation, endLocation);
    }

    private ChessPiece convertToPiece(String param) throws ResponseException {
        String piece = param.toUpperCase();
        return switch (piece) {
            case "QUEEN" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
            case "BISHOP" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            case "ROOK" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            case "CASTLE" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            case "KNIGHT" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            case "HORSE" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            default -> throw new ResponseException(400, "Invalid piece to promote to. Check spelling and try again.");
        };
    }
}
