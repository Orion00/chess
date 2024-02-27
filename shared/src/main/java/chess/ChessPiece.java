package chess;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    PieceType type;

    boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type, boolean hasMoved) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = hasMoved;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> legalMoves = new HashSet<>();

        switch (this.type) {
            case KING -> {
                List<List<Integer>> availableMoves = Arrays.asList(
                        Arrays.asList(0, 1),
                        Arrays.asList(1, 1),
                        Arrays.asList(-1, 0),
                        Arrays.asList(1, 0),
                        Arrays.asList(-1, -1),
                        Arrays.asList(0, -1),
                        Arrays.asList(1, -1),
                        Arrays.asList(-1, 1)
                );

                for (List<Integer> move : availableMoves) {
                    legalMoves.addAll(moveOnce(board, myPosition,move));
                }
                // Adds Castling moves if available

                break;
            }
            case QUEEN -> {
                for (int row = -1; row < 2; row += 1) {
                    for (int col = -1; col < 2; col += 1) {
                        if (row == 0 && col == 0) {
                            // Don't run if not moving anywhere
                            continue;
                        }
                        legalMoves.addAll(recurKeepMoving(board, myPosition, myPosition, row, col));
                    }
                }
                break;
            }
            case BISHOP -> {
                for (int row = -1; row < 2; row += 2) {
                    for (int col = -1; col < 2; col += 2) {
                        legalMoves.addAll(recurKeepMoving(board, myPosition, myPosition, row, col));
                    }
                }
            }
            case KNIGHT -> {
                List<List<Integer>> availableMoves = Arrays.asList(
                        Arrays.asList(2, 1),
                        Arrays.asList(2, -1),
                        Arrays.asList(-2, 1),
                        Arrays.asList(-2, -1),
                        Arrays.asList(1, 2),
                        Arrays.asList(1, -2),
                        Arrays.asList(-1, 2),
                        Arrays.asList(-1, -2)
                );

                for (List<Integer> move : availableMoves) {
                    legalMoves.addAll(moveOnce(board, myPosition,move));
                }
                break;
            }
            case ROOK -> {
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,0,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,0,-1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,0));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,0));
                break;
            }
            case PAWN -> {
                int verticalMovement = 1;
                if (this.pieceColor == ChessGame.TeamColor.BLACK) {
                    verticalMovement = -1;
                }
                List<List<Integer>> availableMoves = new ArrayList<>(Arrays.asList(
                        Arrays.asList(verticalMovement,0),
                        Arrays.asList(verticalMovement,-1),
                        Arrays.asList(verticalMovement,1)
                ));

                for (List<Integer> move : availableMoves) {
                    legalMoves.addAll(pawnMove(board, myPosition,move));
                }

                if (myPosition.getRow() == 2 && this.pieceColor == ChessGame.TeamColor.WHITE && !hasMoved
                        && legalMoves.contains(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()),null))) {
                    legalMoves.addAll(pawnMove(board, myPosition,Arrays.asList(verticalMovement*2,0)));
                } else if (myPosition.getRow() == 7 && this.pieceColor == ChessGame.TeamColor.BLACK && !hasMoved
                        && legalMoves.contains(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()),null))) {
                    legalMoves.addAll(pawnMove(board, myPosition,Arrays.asList(verticalMovement*2,0)));
                }
                break;
            }
            default -> {
                throw new RuntimeException("Piece type is not recognized");
            }
        }

        return legalMoves;
    }

    /*
        Checks if a move is legal so I don't need to write it each switch statement
     */
    private boolean isInBounds(int finalDestination) {
        return finalDestination >= 1 && finalDestination <= 8;
    }

    private ChessPiece pieceIsThere(ChessBoard board, ChessPosition position) {
        ChessPiece blockingPiece = board.getPiece(position);
        return blockingPiece;
    }

    private HashSet<ChessMove> pawnMove(ChessBoard board,ChessPosition myPosition,List<Integer> move) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        int verticalIncrease = move.get(0);
        int horizontalIncrease = move.get(1);


        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + verticalIncrease, myPosition.getColumn() + horizontalIncrease);
        if (isInBounds(newPosition.getRow()) && isInBounds(newPosition.getColumn())) {

            ChessPiece blockingPiece = pieceIsThere(board, newPosition);
            // Check if a piece is blocking
            if (blockingPiece == null) {
                // No piece blocking
                if (horizontalIncrease == 0) {
                    if (newPosition.getRow() == 8 | newPosition.getRow() == 1) {
                        // Promotion
                        legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                        legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                        legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                        legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                    } else {
                        // No Promotion
                        legalMoves.add(new ChessMove(myPosition, newPosition, null));
                    }

                }
            } else if (blockingPiece.getTeamColor() == this.getTeamColor() && horizontalIncrease == 0) {
                // Same team is blocking
            } else if (blockingPiece.getTeamColor() != this.getTeamColor() && abs(horizontalIncrease) == 1) {
                // Enemy team is blocking (and can be taken)

                if (newPosition.getRow() == 8 | newPosition.getRow() == 1) {
                    // Promotion
                    legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                    legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
                    legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                    legalMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                } else {
                    // No Promotion
                    legalMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }

        return legalMoves;
    }

    private HashSet<ChessMove> moveOnce(ChessBoard board,ChessPosition myPosition,List<Integer> move) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        int horizontalIncrease = move.get(0);
        int verticalIncrease = move.get(1);

        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + horizontalIncrease, myPosition.getColumn() + verticalIncrease);
        if (isInBounds(newPosition.getRow()) && isInBounds(newPosition.getColumn())) {

            ChessPiece blockingPiece = pieceIsThere(board, newPosition);
            // Check if a piece is blocking
            if (blockingPiece == null) {
                // No piece blocking
                legalMoves.add(new ChessMove(myPosition, newPosition, null));
            } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
                // Same team is blocking
            } else {
                // Enemy team is blocking (and can be taken)
                legalMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return legalMoves;
    }


    private HashSet<ChessMove> recurKeepMoving(ChessBoard board, ChessPosition startPosition, ChessPosition currentPosition, int r,int c) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
        // Checks we're not off the game board
        if (currentPosition.getRow()+r > 8 || currentPosition.getRow()+r < 1 || currentPosition.getColumn()+c > 8 || currentPosition.getColumn()+c < 1) {
            return legalMoves;
        }

            ChessPosition newPosition = new ChessPosition(currentPosition.getRow()+r, currentPosition.getColumn()+c);
            ChessPiece blockingPiece = board.getPiece(newPosition);

            // Check if a piece is blocking
        if (blockingPiece == null) {
            // No piece blocking, track the legal move and call function again
            legalMoves.add(new ChessMove(startPosition, newPosition, null));
            legalMoves.addAll(recurKeepMoving(board,startPosition,newPosition,r,c));
        } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
            // Same team is blocking
        } else {
            // Enemy team is blocking (and can be taken)
            legalMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        return legalMoves;
    }

    public HashSet<ChessMove> castle(ChessBoard board,ChessPosition myPosition) {
        HashSet<ChessMove> legalMoves = new HashSet<>();

        // Skip if moved or not the right piece type
        if (getHasMoved() || ((myPosition.getColumn() != 5) || !(myPosition.getRow() == 1 || myPosition.getRow() == 8)) || !(getPieceType() == PieceType.KING)) {
            return legalMoves;
        }


        // Move Right
        ChessPosition newPosition1 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1);
        ChessPiece blockingPiece1 = pieceIsThere(board, newPosition1);
        ChessPosition newPosition2 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 2);
        ChessPiece blockingPiece2 = pieceIsThere(board, newPosition2);
        ChessPosition rightRookPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 3);
        ChessPiece rightRook = board.getPiece(rightRookPosition);


        if (blockingPiece1 == null && blockingPiece2 == null
            && rightRook != null && !rightRook.hasMoved) {
            // King
            legalMoves.add(new ChessMove(myPosition, newPosition2, null));
            // Rook
            legalMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(),myPosition.getColumn() + 3), newPosition1, null));
        }

        // Move Left
        newPosition1 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1);
        blockingPiece1 = pieceIsThere(board, newPosition1);
        newPosition2 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 2);
        blockingPiece2 = pieceIsThere(board, newPosition2);
        ChessPosition newPosition3 = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 3);
        ChessPiece blockingPiece3 = pieceIsThere(board, newPosition3);
        ChessPosition leftRookPosition = new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 4);
        ChessPiece leftRook = board.getPiece(leftRookPosition);

        if (blockingPiece1 == null && blockingPiece2 == null && blockingPiece3 == null
            && leftRook != null && !leftRook.hasMoved) {
            // King
            legalMoves.add(new ChessMove(myPosition, newPosition2, null));
            // Rook
            legalMoves.add(new ChessMove(new ChessPosition(myPosition.getRow(),myPosition.getColumn() - 4), newPosition1, null));
        }

        return legalMoves;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

}