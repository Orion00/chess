package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor turn;
    ChessBoard board;


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

        Collection<ChessMove> fullMoves = piece.pieceMoves(board,startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();

        for (ChessMove move : fullMoves) {
            ChessBoard tempBoard = board.clone();
            try {
                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), piece);

                if (isInCheck(piece.getTeamColor())) {
                    throw new InvalidMoveException("That leaves you in check");
                }
                validMoves.add(move);
            } catch (InvalidMoveException i) {
//                System.out.println("Can't move there. "+i.getMessage());
            } finally {
                setBoard(tempBoard);
            }

        }

        Collection<ChessMove> castleMoves = piece.castle(board,startPosition);
        for (ChessMove move : castleMoves) {
            ChessBoard tempBoard = board.clone();
            try {
                if (move.getEndPosition().getColumn() == 3) {
                    // Check a move to the left, so -1
                    ChessPosition intermediatePosition = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn()-1);
                    board.addPiece(move.getStartPosition(), null);
                    board.addPiece(intermediatePosition, piece);
                } else if (move.getEndPosition().getColumn() == 7) {
                    // Check a move to the right, so +1
                    ChessPosition intermediatePosition = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn()+1);
                    board.addPiece(move.getStartPosition(), null);
                    board.addPiece(intermediatePosition, piece);
                }


                if (isInCheck(piece.getTeamColor())) {
                    throw new InvalidMoveException("That leaves you in check");
                }
                validMoves.add(move);
            } catch (InvalidMoveException i) {
//                System.out.println("Can't move there. "+i.getMessage());
            } finally {
                setBoard(tempBoard);
            }
        }
        return validMoves;
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

        try {
            tryAMove(move, piece);
            promoteIfNeeded(move);


            // Check for Castling
            if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.getHasMoved()) {
                    if (move.getEndPosition().getColumn() == 3)  {
                        for (ChessMove rookMove : validMoves) {
                            if (rookMove.getStartPosition() != move.getStartPosition()) {
                                if (rookMove.getStartPosition().getColumn() == 1) {
                                    ChessPiece rook = board.getPiece(rookMove.getStartPosition());
                                    tryAMove(rookMove, board.getPiece(rookMove.getStartPosition()));

                                    rook.setHasMoved(true);
                                    break;
                                }
                            }
                        }
                    } else if (move.getEndPosition().getColumn() == 7){
                    // Cycle through all moves, if there's one that's different, it has to be the rook moving as well
                            for (ChessMove rookMove : validMoves) {
                                if (rookMove.getStartPosition() != move.getStartPosition()) {
                                    if (rookMove.getStartPosition().getColumn() == 8) {
                                        ChessPiece rook = board.getPiece(rookMove.getStartPosition());
                                        tryAMove(rookMove, board.getPiece(rookMove.getStartPosition()));

                                        rook.setHasMoved(true);
                                        break;
                                    }
                                }


                                }
                }

            }

            piece.setHasMoved(true);

            // Changes turn
            if (getTeamTurn() == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } catch (InvalidMoveException i) {
            throw new InvalidMoveException(i.getMessage());
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
        ChessPosition myKingPosition = findMyKing(teamColor);

        if (myKingPosition == null) {
            // If myKing's square is null, myKing isn't on the board, so can't be in check
            return false;
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
            // Already checked if myKing is null

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
        // Same as in Check, just check this condition at different times
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition myKingPosition = findMyKing(teamColor);

        if (myKingPosition == null) {
            // If myKing's square is null, myKing isn't on the board, so can't be in stalemate
            return false;
        }

        Collection<ChessMove> validMoves = validMoves(myKingPosition);
        if (validMoves == null) {
            return true;
        }

        for (ChessMove move : validMoves) {
            // See if king can move in any of these directions
            try {
                // If it can, return False;
                tryAMove(move, board.getPiece(myKingPosition));
                return false;
            } catch (InvalidMoveException i){
                // If it can't, continue through more valid moves
                continue;
            }

        }
        // If none of the valid moves were actually valid, in stalemate
        return true;
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

    private void tryAMove(ChessMove move, ChessPiece movingPiece) throws InvalidMoveException {
        ChessBoard tempBoard = board.clone();
        board.addPiece(move.getStartPosition(),null);
        board.addPiece(move.getEndPosition(), movingPiece);

        if (isInCheck(movingPiece.getTeamColor())) {
            // If in check, don't make the move
            setBoard(tempBoard);
            throw new InvalidMoveException("That leaves you in check");
        }
        // If not in check, make the move
    }

    private void promoteIfNeeded(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getEndPosition());

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (move.getEndPosition().getRow() == 8 && piece.getTeamColor() == TeamColor.WHITE) {
                // Promote White
                piece.type = move.getPromotionPiece();
            } else if (move.getEndPosition().getRow() == 1 && piece.getTeamColor() == TeamColor.BLACK) {
                // Promote Black
                piece.type = move.getPromotionPiece();
            }
        }
    }

    private ChessPosition findMyKing(TeamColor teamColor) {
        ChessPosition myKingPosition;

        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPiece temp = board.getPiece(new ChessPosition(r,c));
                if (temp != null && temp.getPieceType() == ChessPiece.PieceType.KING && temp.getTeamColor() == teamColor) {
                    myKingPosition = new ChessPosition(r,c);
                    return myKingPosition;
                }
            }
        }
        // King not on the board
        return null;
    }

}
