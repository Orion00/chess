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
        System.out.println("We're starting at "+myPosition.getRow()+","+myPosition.getColumn());

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
                break;
            }
            case QUEEN -> {
                //TODO: Implement this
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,-1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,-1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,0,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,0,-1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,0));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,0));
                break;
            }
            case BISHOP -> {
                //TODO: Implement this
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,1,-1));
                legalMoves.addAll(recurKeepMoving(board,myPosition,myPosition,-1,-1));
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

                if (myPosition.getRow() == 2 && this.pieceColor == ChessGame.TeamColor.WHITE) {
                    availableMoves.add(Arrays.asList(verticalMovement*2, 0));
                } else if (myPosition.getRow() == 7 && this.pieceColor == ChessGame.TeamColor.BLACK) {
                    availableMoves.add(Arrays.asList(verticalMovement*2, 0));
                }

                for (List<Integer> move : availableMoves) {
                    legalMoves.addAll(pawnMove(board, myPosition,move));
                }
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
        return finalDestination >= 1 && finalDestination <= 8;
    }

    private ChessPiece pieceIsThere(ChessBoard board, ChessPosition position) {
        ChessPiece blockingPiece = board.getPiece(position);
        return blockingPiece;
    }

//    private HashSet<ChessMove> legalAndBlocked(ChessBoard board, ChessPosition myPosition, List<List<Integer>> availablePositions) {
//        HashSet<ChessMove> legalMoves = new HashSet<>();
//        for (List<Integer> position : availablePositions) {
//            int horizontalIncrease = position.get(0);
//            int verticalIncrease = position.get(1);
//
//            int newRow = myPosition.getRow() + horizontalIncrease;
//            int newColumn = myPosition.getColumn() + verticalIncrease;
//
//            ChessPosition newPosition = new ChessPosition(newRow, newColumn);
//            // Check if in bounds
//            if (isInBounds(newRow) && isInBounds(newColumn)) {
//                ChessPiece blockingPiece = pieceIsThere(board, newPosition);
//
//                // Check if a piece is blocking
//                if (blockingPiece == null) {
//                    // No piece blocking
//                    System.out.println("No piece blocking so you're free to move to " + newPosition);
//                    legalMoves.add(new ChessMove(myPosition, newPosition, null));
//                } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
//                    // Same team is blocking
//                    System.out.println("Your own team (" + this.getTeamColor() + ") is blocking that! " + newPosition);
//                } else {
//                    // Enemy team is blocking (and can be taken)
//                    System.out.println("You can take a " + blockingPiece.getPieceType() + " at " + newPosition);
//                    legalMoves.add(new ChessMove(myPosition, newPosition, null));
//                }
//
//            } else {
//                System.out.println("It's out of bounds");
//            }
//        }
//        return legalMoves;
//    }

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
                System.out.println("No piece blocking.");
                if (horizontalIncrease == 0) {
                    System.out.println("so you're free to move to " + newPosition);
                    legalMoves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    System.out.println("But you can't move sideways without violence");
                }
            } else if (blockingPiece.getTeamColor() == this.getTeamColor() && horizontalIncrease == 0) {
                // Same team is blocking
                System.out.println("Your own team (" + this.getTeamColor() + ") is blocking that! " + newPosition);
            } else if (blockingPiece.getTeamColor() != this.getTeamColor() && horizontalIncrease == 1) {
                // Enemy team is blocking (and can be taken)
                System.out.println("You can take a " + blockingPiece.getPieceType() + " at " + newPosition);
                legalMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
                System.out.println("A pawn can't move that that!");
            }
        } else {
            System.out.println(newPosition+" is out of bounds.");
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
                System.out.println("No piece blocking so you're free to move to " + newPosition);
                legalMoves.add(new ChessMove(myPosition, newPosition, null));
            } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
                // Same team is blocking
                System.out.println("Your own team (" + this.getTeamColor() + ") is blocking that! " + newPosition);
            } else {
                // Enemy team is blocking (and can be taken)
                System.out.println("You can take a " + blockingPiece.getPieceType() + " at " + newPosition);
                legalMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        } else {
            System.out.println(newPosition+" is out of bounds.");
        }
        return legalMoves;
    }


    private HashSet<ChessMove> recurKeepMoving(ChessBoard board, ChessPosition startPosition, ChessPosition currentPosition, int r,int c) {
        HashSet<ChessMove> legalMoves = new HashSet<>();
//        System.out.println("Trying to move to " + (currentPosition.getRow()+r)+","+(currentPosition.getColumn()+c));

        // Checks we're not off the game board
        if (currentPosition.getRow()+r > 8 || currentPosition.getRow()+r < 1 || currentPosition.getColumn()+c > 8 || currentPosition.getColumn()+c < 1) {
//            System.out.println((currentPosition.getRow()+r)+","+(currentPosition.getColumn()+c) +" is an illegal move off the board. Stopping recursion.");
            return legalMoves;
        }

            ChessPosition newPosition = new ChessPosition(currentPosition.getRow()+r, currentPosition.getColumn()+c);
            ChessPiece blockingPiece = board.getPiece(newPosition);

            // Check if a piece is blocking
        if (blockingPiece == null) {
            // No piece blocking, track the legal move and call function again
            System.out.println("No piece blocking so you're free to move to " + newPosition);
            legalMoves.add(new ChessMove(startPosition, newPosition, null));
            legalMoves.addAll(recurKeepMoving(board,startPosition,newPosition,r,c));
        } else if (blockingPiece.getTeamColor() == this.getTeamColor()) {
            // Same team is blocking
            System.out.println("Your own team (" + this.getTeamColor() + ") is blocking that! " + newPosition);
        } else {
            // Enemy team is blocking (and can be taken)
            System.out.println("You can take a " + blockingPiece.getPieceType() + " at " + newPosition);
            legalMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        return legalMoves;
    }

}


//        | | |x| | | |x| |
//        | | | |x| |x| | |
//        | | | | |b| | | |
//        | | | |x| |x| | |
//        | | |x| | | |x| |
//        | |x| | | | | |x|
//        |x| | | | | | | |
//        | | | | | | | | |