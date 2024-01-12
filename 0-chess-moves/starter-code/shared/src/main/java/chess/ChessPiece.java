package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
//        Check based on color if it goes up or down for pawns
//        Check if moving goes less than 0 or greater than 7
//        Add 1 to everything since the moves seem to not be 0 based
        HashSet<ChessMove> legalMoves = new HashSet<>();
        switch (this.type) {
            case KING -> {

//TODO: Have each piece generate the available moves, then have the same function run with those available moves
                //TODO: Use slightly different functions for King and Pawn

//                List<Integer> availableHMoves = Arrays.asList(-1, 0, 1, -1, 1, -1, 0, 1);
//                List<Integer> availableVMoves = Arrays.asList(1, 1, 1, 0, 0, -1, -1, -1);
//
//                System.out.println("Orion is trying to print moves");
//                for (Integer,Integer H,V : availableHMoves,availableVMoves) {
//                    System.out.println(H,V);
//                    if (isInBounds(myPosition.getRow()+H) && isInBounds(myPosition.getColumn())+V) {
//                        legalMoves.add(ChessPosition(H,V));
//                    }
//                }
//
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

                System.out.println("Orion is trying to print moves");
                for (List<Integer> move : availableMoves) {
                    int H = move.get(0);
                    int V = move.get(1);

//                    System.out.println(H + ", " + V);

                    int newRow = myPosition.getRow() + H;
                    int newColumn = myPosition.getColumn() + V;


                    ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                    // Check if in bounds
                    if (isInBounds(newRow) && isInBounds(newColumn)) {
                        ChessPiece blockingPiece = pieceIsThere(board, newPosition);

                        // Check if a piece is blocking
                        if (blockingPiece == null) {
                            // No piece blocking
                            System.out.println("No piece blocking so you're free to move to "+newPosition);
                            legalMoves.add(new ChessMove(myPosition, newPosition, null));
                        } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
                            // Same team is blocking
                            System.out.println("Your own team ("+this.getTeamColor()+") is blocking that! "+newPosition);
                        } else {
                            // Enemy team is blocking (and can be taken)
                            System.out.println("You can take a "+blockingPiece.getPieceType()+" at "+newPosition);
                            legalMoves.add(new ChessMove(myPosition, newPosition, null));
                        }

                    } else {
                        System.out.println("It's out of bounds");
                    }
                }
//TODO: Implement this
                break;
            }
            case QUEEN -> {
                //TODO: Implement this
                break;
            }
            case BISHOP -> {
                //TODO: Implement this
                break;
            }
            case KNIGHT -> {
                //TODO: Implement this
                break;
            }
            case ROOK -> {
                //TODO: Implement this
                break;
            }
            case PAWN -> {
                //TODO: Implement this
                break;
            }
            default -> {
                throw new RuntimeException("Piece type is not recognized");
            }
        }

        System.out.println("Legal Moves include");
        System.out.print(legalMoves);
        return legalMoves;
    }

    /*
        Checks if a move is legal so I don't need to write it each switch statement
     */
    private boolean isInBounds(int finalDestination) {
        return finalDestination >= 0 && finalDestination <= 7;
    }

    private ChessPiece pieceIsThere(ChessBoard board, ChessPosition position) {
        ChessPiece blockingPiece = (ChessPiece) board.getPiece(position);
//
//        if (blockingPiece == null) {
//            System.out.println("Nothing blocking this");
//        } else {
//            System.out.println(blockingPiece+" is blocking "+position);
//        }
        return blockingPiece;
    }

}



