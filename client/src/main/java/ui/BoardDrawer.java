package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import java.io.PrintStream;


import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String EMPTY = "   ";
    private static String playerColor;

    private static ChessBoard board;

    public void drawBoard(PrintStream out, String playerColor, ChessBoard board) throws ResponseException {
        try {
            BoardDrawer.playerColor = playerColor;
            if (BoardDrawer.playerColor == "null") {
                //Observer
                BoardDrawer.playerColor = "WHITE";
            }
            BoardDrawer.board = board;
            out.print(ERASE_SCREEN);

            drawHeaders(out);
            drawSquares(out);
            drawHeaders(out);

            resetBGAndTextCol(out);
        } catch (ResponseException i) {
            resetBGAndTextCol(out);
            throw i;
        }
    }
    private static void drawHeaders(PrintStream out) throws ResponseException {
        String[] headers;
        if (playerColor.equals("WHITE")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else if (playerColor.equals("BLACK")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            throw new ResponseException(400, "Incorrect player color somehow");
        }

        drawHeader(out, " ");
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        drawHeader(out, " ");

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        setBlack(out);
        out.print(SET_TEXT_COLOR_CREME);
        out.print(" ");
        out.print(headerText);
        out.print(" ");
    }

    private static void drawSquares(PrintStream out) throws ResponseException {
        int startRow;
        int endRow;
        int increment;
        if (playerColor.equals("BLACK")) {
            startRow = 0;
            endRow = BOARD_SIZE_IN_SQUARES;
            increment = 1;
        } else {
            startRow = BOARD_SIZE_IN_SQUARES-1;
            endRow =-1;
            increment = -1;
        }

        for (int boardRow = startRow; boardRow != endRow; boardRow += increment) {
            ChessPiece[] boardRowValues = board.getBoard()[boardRow];

            drawRowHeaders(out, boardRow);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if ((boardCol % 2 == 0 && boardRow % 2 == 0) ||
                        (boardCol % 2 != 0 && boardRow % 2 != 0)) {
                    setCreme(out);
                } else {
                    setBrown(out);
                }
                out.print(getPieceSymbol(boardRowValues[boardCol]));
            }
            drawRowHeaders(out, boardRow);
            resetBGAndTextCol(out);
            out.println();

//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                drawVerticalLine(out);
//                setBlack(out);
//            }
        }
//        out.println();
    }


    private static String getPieceSymbol(ChessPiece chessPiece) {
        ChessPiece.PieceType type;
        ChessGame.TeamColor color;

        if (chessPiece == null) {
            return EMPTY;
        } else {
            type = chessPiece.getPieceType();
            color = chessPiece.getTeamColor();
        }


        return switch (type) {
            case ROOK -> color == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case KING -> color == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> color == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            default -> EMPTY; // Default case if type doesn't match any known cases
        };
    }


    private static void setCreme(PrintStream out) {
        out.print(SET_BG_COLOR_CREME);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBrown(PrintStream out) {
        out.print(SET_BG_COLOR_BROWN);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setFontSize(PrintStream out, Integer size) {
        String SET_FONT_SIZE = "\u001B[" + size + "m";
        out.print(SET_FONT_SIZE);
    }

    private static void resetFontSize(PrintStream out) {
        String RESET_FONT_SIZE = "\033[0m";
        out.print(RESET_FONT_SIZE);
    }


    private static void resetBGAndTextCol(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawRowHeaders(PrintStream out, Integer row) throws ResponseException {
        String[] headers = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        drawHeader(out, headers[row]);
    }
}
