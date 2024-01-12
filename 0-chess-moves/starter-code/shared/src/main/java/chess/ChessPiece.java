package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

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
                        Arrays.asList(-1, 1),
                        Arrays.asList(0, 1),
                        Arrays.asList(1, 1),
                        Arrays.asList(-1, 0),
                        Arrays.asList(1, 0),
                        Arrays.asList(-1, -1),
                        Arrays.asList(0, -1),
                        Arrays.asList(1, -1)
                );

                System.out.println("Orion is trying to print moves");
                for (List<Integer> move : availableMoves) {
                    int H = move.get(0);
                    int V = move.get(1);

                    System.out.println(H + ", " + V);

                    int newRow = myPosition.getRow() + H;
                    int newColumn = myPosition.getColumn() + V;

                    if (isInBounds(newRow) && isInBounds(newColumn)) {
                        ChessPosition newPosition = new ChessPosition(newRow,newColumn);
                        legalMoves.add(new ChessMove(myPosition,newPosition, null));
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
}


