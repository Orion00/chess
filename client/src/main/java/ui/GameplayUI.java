package ui;

import chess.*;
import client.ServerFacade;
import client.websocket.WebsocketFacade;
import exception.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

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
    }

    @Override
    public String help() {
        return """
            (Functionality limited)
            help - Display available commands
            print - Show your board
            move <START LOCATION> <END LOCATION> <PIECE TO PROMOTE TO>- Move selected piece
            show <PIECE LOCATION> - Show available moves for selected piece
            leave - Lose your place in current game and can resume later. Return to previous window.
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
                case "print" -> print(params);
                case "move" -> makeMove(currentAuthToken, params);
                case "show" -> showMoves(params);
                case "leave" -> leaveGame(params);
                case "resign" -> resignGame(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String resignGame(String... params) throws ResponseException {
        if (params.length > 0) {
            throw new ResponseException(400, "Too many inputs entered");
        }

        System.out.print("Are you sure you want to resign? y/n\n>>> ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine().toUpperCase();

        if (line.equals("YES") || line.equals("Y")) {
            ws.resignGame(currentAuthToken, currentGameId, currentPlayerColor, currentUsername);
            return "You've resigned the game. Type 'leave' when you're ready to leave.";
        }
        return "Resignation cancelled. You're still in the game.";
    }

    private String leaveGame(String... params) throws ResponseException {
        if (params.length > 0) {
            throw new ResponseException(400, "Too many inputs entered");
        }

        ws.leaveGame(currentAuthToken, currentGameId, currentPlayerColor, currentUsername);
        currentGameId = null;
        currentPlayerColor = null;
        currentGame = null;

        return "You've left the game. Type 'help' to view available commands";
    }

    private String showMoves(String... params) throws ResponseException {
        if (params.length > 1) {
            throw new ResponseException(400, "Too many inputs entered");
        }
        // Convert from location to piece

        // Show moves for that piece on current game

        // printBoard using special print that takes in locations? In printing, if row and column are in pair, print different color
        return "No functionality currently";
    }

    public ChessGame.TeamColor getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public String print(String... params) throws ResponseException {
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
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Printing updated board.");
        System.out.print(RESET_TEXT_COLOR);
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
        if (params.length > 4) {
            throw new ResponseException(400, "Too many inputs entered. Try again.");
        } else if (params.length < 2) {
            throw new ResponseException(400, "Please enter a starting location and ending location. Try again");
        }

        String regex = "(?i)[a-h][1-8]";
        Pattern pattern = Pattern.compile(regex);
        int increment = 0;
        for (String param : params) {
            if (increment > 1) {
                // Don't regex the promotion piece
                break;
            }
            if (!pattern.matcher(param).matches()) {
                throw new ResponseException(400, "Invalid starting or ending location: "+param+". Try again.");
            }
            increment++;
        }
        String startLocation = params[0];
        String endLocation = params[1];

        int startRow = Character.getNumericValue(startLocation.charAt(1));
        int startCol = convertColCharToInt(startLocation.charAt(0));
        int endRow = Character.getNumericValue(endLocation.charAt(1));
        int endCol = convertColCharToInt(endLocation.charAt(0));

        if (startCol < 1 || startCol > 8 || startRow < 1 || startRow > 8 ||
                endCol < 1 || endCol > 8 || endRow < 1 || endRow > 8) {
            throw new ResponseException(400, "Invalid move. Please select a position on the board.");
        }

        ChessPiece propPiece = currentGame.getBoard().getPiece(new ChessPosition(startRow, startCol));
        if (propPiece == null) {
            throw new ResponseException(400, "There's no piece at "+startLocation+". Try again.");
        }

        ChessPiece.PieceType promotionPiece = null;
        if ((currentGame.getBoard().getPiece(new ChessPosition(startRow, startCol)).getPieceType() == ChessPiece.PieceType.PAWN) &&
                (endRow == 1 || endRow == 8)) {
            if (params.length != 3) {
                throw new ResponseException(400, "Please enter a piece to promote to.");
            } else {
               promotionPiece = convertToPiece(params[2]).getPieceType();
            }

        }




        ChessMove propMove = new ChessMove(new ChessPosition(startRow, startCol), new ChessPosition(endRow, endCol), promotionPiece);
        try {
            // Tests if it works by using makeMove() on a clone
            ChessBoard testBoard = currentGame.getBoard().clone();
            ChessGame testGame = new ChessGame();
            testGame.setBoard(testBoard);
            testGame.setTeamTurn(currentGame.getTeamTurn());
            testGame.makeMove(propMove);

            ws.makeMove(currentAuthToken, currentGameId, currentUsername, getCurrentColor(),propMove);
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, "Move failed. "+e.getMessage());
        }


        //return String.format(SET_TEXT_COLOR_BLUE+"Move successful from %s to %s."+RESET_TEXT_COLOR, startLocation, endLocation);
        return "";
    }

    private ChessPiece convertToPiece(String param) throws ResponseException {
        String piece = param.toUpperCase();
        return switch (piece) {
            case "QUEEN" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
            case "BISHOP" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            case "ROOK", "CASTLE" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            case "KNIGHT", "HORSE" -> new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            default -> throw new ResponseException(400, "Invalid piece to promote to. Check spelling and try again.");
        };
    }

    public String getCurrentColor() {
        return switch (currentPlayerColor) {
            case WHITE -> "WHITE";
            case BLACK -> "BLACK";
            case null -> null;
        };
    }

    public boolean isPlaying() {
        return (currentGame != null && currentGameId != null);
    }
    private int convertColCharToInt(Character c) throws ResponseException {
        char cha = Character.toLowerCase(c);
        return switch(cha) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new ResponseException(400, "Move failed. Incorrect column letter.");
        };
    }

}
