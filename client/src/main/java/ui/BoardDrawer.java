package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.GameData;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";
    private static Random rand = new Random();
    private static String playerColor;

    private static ChessBoard board;

    public void drawBoard(PrintStream out, String playerColor, ChessBoard board) throws ResponseException {
        BoardDrawer.playerColor = playerColor;
        BoardDrawer.board = board;
        out.print(ERASE_SCREEN);

        drawHeaders(out);

        drawSquares(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void drawHeaders(PrintStream out) throws ResponseException {

        setBlack(out);
        setFontSize(out,30);

        String[] headers;

        if (playerColor.equals("WHITE")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else if (playerColor.equals("BLACK")) {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            throw new ResponseException(400, "Incorrect player color somehow");
        }
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawHeader(out, headers[boardCol]);

            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
            }
        }

        out.println();
        resetFontSize(out);
    }

    private static void drawHeader(PrintStream out, String headerText) {
        int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printHeaderText(out, headerText);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(player);

        setBlack(out);
    }

    private static void drawSquares(PrintStream out) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            ChessPiece[] boardRowValues = board.getBoard()[boardRow];

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if ((boardCol % 2 == 0 && boardRow % 2 == 0) ||
                        (boardCol % 2 != 0 && boardRow % 2 != 0)) {
                    setCreme(out);
                } else {
                    setBrown(out);
                }
                out.print(getPieceSymbol(boardRowValues[boardCol]));
            }
            setBlack(out);
            out.print("a");
            out.print(RESET_BG_COLOR);
            out.println();

//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                drawVerticalLine(out);
//                setBlack(out);
//            }
        }
        out.println();
    }

    private static void drawRowOfSquares(PrintStream out, ChessPiece[] row) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol % 2 == 0) {
                setCreme(out);
            } else {
                setBrown(out);
            }
            out.print(getPieceSymbol(row[boardCol]));
        }
//        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//            setCreme(out);
//
//            if (squareRow == SQUARE_SIZE_IN_CHARS / 2) {
//                int prefixLength = SQUARE_SIZE_IN_CHARS / 2;
//                int suffixLength = SQUARE_SIZE_IN_CHARS - prefixLength - 1;
//
//                out.print(EMPTY.repeat(prefixLength));
//
//
//                printPlayer(out, rand.nextBoolean() ? X : O);
//                out.print(EMPTY.repeat(suffixLength));
//            }
//            else {
//                out.print(EMPTY.repeat(SQUARE_SIZE_IN_CHARS));
//            }
//
//            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw right line
//                setBrown(out);
//                out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
//            }
//
//            setBlack(out);
//        }
//
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

    private static void drawVerticalLine(PrintStream out) {

        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_CHARS; ++lineRow) {
            setBrown(out);
            out.print(EMPTY.repeat(boardSizeInSpaces));

            setBlack(out);
            out.println();
        }
    }

    private static void setCreme(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
        out.print(SET_BG_COLOR_CREME);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBrown(PrintStream out) {
//        out.print(SET_BG_COLOR_RED);
//        out.print(SET_TEXT_COLOR_RED);
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

    private static void printPlayer(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setCreme(out);
    }
}
