package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor turn;
    ChessBoard board;

    // TODO: Decide if I only need once since they contain the color
    Collection<ChessPiece> blackCaptured;
    Collection<ChessPiece> whiteCaptured;


    public ChessGame() {
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            // No piece there
            return null;
        }

        return piece.pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Check if legal

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Your piece can't make that move");
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());

        if(piece == null) {
            throw new InvalidMoveException("There's not a piece there");
        }

        if (piece.getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException("It's not your turn");
        }

        tryAMove(move, piece);

        // TODO: Check if King is in Check
//        ChessBoard tempBoard = new ChessBoard(board);
//
//        board.addPiece(move.getStartPosition(),null);
//        board.addPiece(move.getEndPosition(), piece);



        // Resets where it was to nothing
//        board.addPiece(move.getStartPosition(),null);
//
//        // TODO: Add if that if it captures to add to captured pieces for the opposing color
//        // TODO: Add Pawn Promotion
//        board.addPiece(move.getEndPosition(), piece);

        // Changes turn
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Get same color king's position
        ChessPosition myKingPosition = null;

        foundKing:
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPiece temp = board.getPiece(new ChessPosition(r,c));
                if (temp != null && temp.getPieceType() == ChessPiece.PieceType.KING && temp.getTeamColor() == teamColor) {
                    myKingPosition = new ChessPosition(r,c);
                    break foundKing;
                }
            }
        }

        // Check all pieces of opposing team's legal moves. If position of same color king is in those moves, return true;
        Collection<ChessMove> totalMoves = new HashSet<>();
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPiece temp = board.getPiece(new ChessPosition(r,c));
                if (temp != null && temp.getTeamColor() != teamColor) {
                    totalMoves.addAll(temp.pieceMoves(board, new ChessPosition(r,c)));
                }
            }
        }

        for (ChessMove move : totalMoves) {
            // Check in enemy pieces can move into myKing's square
            // If myKing's square is null, myKing isn't on the board
            ChessPosition a = move.getEndPosition();

            if (myKingPosition.equals(move.getEndPosition())) {
                return true;
            }
        }

        // They made it through everything, so it must not be in check
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void tryAMove(ChessMove move, ChessPiece movingPiece) throws InvalidMoveException {
        ChessBoard tempBoard = new ChessBoard(board);
        ChessBoard t = board.clone();
        board.addPiece(move.getStartPosition(),null);
        board.addPiece(move.getEndPosition(), movingPiece);

        if (isInCheck(movingPiece.getTeamColor())) {
            // If in check, don't make the move
            setBoard(tempBoard);
            throw new InvalidMoveException("That leaves you in check");
        }
        // If not in check, make the move


    }
}
